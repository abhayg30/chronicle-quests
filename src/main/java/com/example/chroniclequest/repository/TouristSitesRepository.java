package com.example.chroniclequest.repository;

import com.example.chroniclequest.entity.TouristSites;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TouristSitesRepository extends MongoRepository<TouristSites, String> {
    List<TouristSites> findByGeometryNear (Point point, Distance distance);
}
