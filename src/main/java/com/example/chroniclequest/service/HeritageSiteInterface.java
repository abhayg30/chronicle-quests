package com.example.chroniclequest.service;

import com.example.chroniclequest.entity.HeritageSiteEntity;
import com.example.chroniclequest.entity.Suburb;
import com.example.chroniclequest.entity.TouristSites;

import java.io.IOException;
import java.util.List;

public interface HeritageSiteInterface {
    List<HeritageSiteEntity> findHeritageSiteNearby(double lat, double lon, double radius);
    List<TouristSites> findHeritageSitesNearSuburb(String suburb, double radius);
    List<Suburb> findByNameRegex(String suburb);

    List<TouristSites> findTouristSitesNearby(double lat, double lon, double radius);
}
