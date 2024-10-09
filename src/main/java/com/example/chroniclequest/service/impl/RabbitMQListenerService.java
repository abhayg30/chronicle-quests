package com.example.chroniclequest.service.impl;

import com.example.chroniclequest.entity.HeritageSiteEntity;
import com.example.chroniclequest.dto.RabbitMQDto;
import com.example.chroniclequest.entity.TouristSites;
import com.example.chroniclequest.service.HeritageSiteInterface;
import com.example.chroniclequest.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class RabbitMQListenerService {

    private final AmqpAdmin amqpAdmin;

    private final HeritageSiteInterface heritageSiteInterface;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerService.class);
    public RabbitMQListenerService(AmqpAdmin amqpAdmin, HeritageSiteInterface heritageSiteInterface, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.amqpAdmin = amqpAdmin;
        this.heritageSiteInterface = heritageSiteInterface;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "handshakeQueue")
    public void handleHandshake(String sessionId){
        Queue newSessionQueue = new Queue("queue."+sessionId, false, false, true);
        amqpAdmin.declareQueue(newSessionQueue);

        Binding buildBinding = BindingBuilder.bind(newSessionQueue).to(new TopicExchange("topicExchange"))
                .with("topic."+sessionId);

        amqpAdmin.declareBinding(buildBinding);
        System.out.println("Dynamic queue created and bound for sessionId: " + sessionId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "autoDelete", autoDelete = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "topicExchange", type = ExchangeTypes.TOPIC),
            key = "topic.#"), ackMode = StringUtils.ACK_MANUAL)
    public void receiveLocationAndFetchHeritageSites(RabbitMQDto rabbitMQDto, Channel channel, Message message) throws IOException {
        System.out.println("Added to queue with: "+rabbitMQDto.getSessionId());
        try {
//            List<HeritageSiteEntity> heritageSiteList = heritageSiteInterface.findHeritageSiteNearby(rabbitMQDto.getLat(),
//                    rabbitMQDto.getLon(), 5.0);
            List<TouristSites> touristSites = heritageSiteInterface.findTouristSitesNearby(rabbitMQDto.getLat(),
                    rabbitMQDto.getLon(), 5.0);
            logger.info("heritage sites returned from mongo");
            String jsonHeritageSites = objectMapper.writeValueAsString(touristSites);
            redisTemplate.opsForValue().set(StringUtils.REDIS_KEY_HERITAGE + rabbitMQDto.getSessionId(), jsonHeritageSites);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e){
            logger.error("heritage sites not returned from mongo");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

}
