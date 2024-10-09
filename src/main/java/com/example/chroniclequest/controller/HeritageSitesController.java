package com.example.chroniclequest.controller;

import com.example.chroniclequest.entity.HeritageSiteEntity;
import com.example.chroniclequest.entity.Suburb;
import com.example.chroniclequest.entity.TouristSites;
import com.example.chroniclequest.service.HeritageSiteInterface;
import com.example.chroniclequest.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class HeritageSitesController {

    private static final Logger logger = LoggerFactory.getLogger(HeritageSitesController.class);

    private final HeritageSiteInterface heritageSiteInterface;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HeritageSitesController(HeritageSiteInterface heritageSiteInterface, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.heritageSiteInterface = heritageSiteInterface;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String home(){
        return "Hello";
    }

    @GetMapping("/places/nearby")
    public ResponseEntity<List<TouristSites>> getHeritageSites(@RequestParam double radius, @RequestParam String sessionId) {

        logger.info("Request received for controller /places/nearby");
        String jsonHeritageSites = redisTemplate.opsForValue().get(StringUtils.REDIS_KEY_HERITAGE+sessionId);
        logger.info("Redis entry for key "+StringUtils.REDIS_KEY_HERITAGE+sessionId+" is invoked");
        assert jsonHeritageSites != null;
        try{
            return ResponseEntity.ok(objectMapper.readValue(jsonHeritageSites, new TypeReference<List<TouristSites>>() {
            }));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return (ResponseEntity<List<TouristSites>>) ResponseEntity.badRequest();
        }

    }

    @GetMapping("/places/suburb")
    public ResponseEntity<List<TouristSites>> getBySuburb(@RequestParam String suburb, @RequestParam double radius){
        logger.info("Request received for controller /places/suburb");
        try {
            List<TouristSites> sites = heritageSiteInterface.findHeritageSitesNearSuburb(suburb.toUpperCase(), radius);
            return ResponseEntity.ok(sites);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/autocomplete")
    public ResponseEntity<List<Suburb>> getSuburbByRegex(@RequestParam String suburb) {
        try{
            List<Suburb> suburbs = heritageSiteInterface.findByNameRegex(suburb);
            return ResponseEntity.ok(suburbs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
