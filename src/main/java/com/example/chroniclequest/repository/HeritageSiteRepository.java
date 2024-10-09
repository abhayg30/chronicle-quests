package com.example.chroniclequest.repository;

import com.example.chroniclequest.entity.HeritageSiteEntity;
import com.example.chroniclequest.entity.Suburb;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HeritageSiteRepository extends MongoRepository<HeritageSiteEntity, String> {
    List<HeritageSiteEntity> findByLocationNear (Point point, Distance distance);
}
