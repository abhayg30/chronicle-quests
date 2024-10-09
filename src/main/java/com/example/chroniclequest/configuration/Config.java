package com.example.chroniclequest.configuration;
import com.example.chroniclequest.HeritageSite;
import com.example.chroniclequest.dto.RabbitMQDto;
import com.example.chroniclequest.serializer.PointDeserializer;
import com.example.chroniclequest.serializer.PointSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Integer.parseInt;


@Configuration
public class Config {

    private final static String handshakeRoutingKey = "handshake";
    private final static String directExchangeName = "handshakeExchange";
    private final static String topicExchangeName = "topicExchange";
    private final static String handshakeQueueName = "handshakeQueue";

    @Autowired
    private Environment environment;

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    @Bean
    public Queue handshakeQueue() {
        return new Queue(handshakeQueueName, false);
    }

    @Bean
    public Binding buildBinding(){
        return BindingBuilder.bind(handshakeQueue()).to(directExchange()).with(handshakeRoutingKey);
    }
    @Bean("Jackson2JsonMessageConverter")
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("RM_CF")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.host")));
        connectionFactory.setPort(parseInt(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.port"))));
        connectionFactory.setUsername(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.username")));
        connectionFactory.setPassword(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.password")));
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate (@Qualifier("RM_CF") ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }


    @Bean
    public RabbitAdmin rabbitAdmin (@Qualifier("RM_CF")ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean("REDIS_CF")
    RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(Objects.requireNonNull(environment.getProperty("spring.data.redis.host")));
        redisStandaloneConfiguration.setPort(parseInt(Objects.requireNonNull(environment.getProperty("spring.data.redis.port"))));
        return new JedisConnectionFactory(redisStandaloneConfiguration);


    }

    @Bean("RabbitListenerConnectionFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(@Qualifier("RM_CF")ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addSerializer(Point.class, new PointSerializer());
        module.addDeserializer(Point.class, new PointDeserializer());

        mapper.registerModule(module);
        return mapper;
    }
}
