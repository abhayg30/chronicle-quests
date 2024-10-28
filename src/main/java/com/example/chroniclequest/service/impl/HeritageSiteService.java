package com.example.chroniclequest.service.impl;

import com.example.chroniclequest.entity.HeritageSiteEntity;
import com.example.chroniclequest.entity.Suburb;
import com.example.chroniclequest.entity.TouristSites;
import com.example.chroniclequest.repository.HeritageSiteRepository;
import com.example.chroniclequest.repository.SuburbRepository;
import com.example.chroniclequest.repository.TouristSitesRepository;
import com.example.chroniclequest.service.HeritageSiteInterface;
import com.example.chroniclequest.util.StringUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HeritageSiteService implements HeritageSiteInterface {
    private final HeritageSiteRepository heritageSiteRepository;

    private final SuburbRepository suburbRepository;
    private final TouristSitesRepository touristSitesRepository;

    public HeritageSiteService(HeritageSiteRepository heritageSiteRepository, SuburbRepository suburbRepository, TouristSitesRepository touristSitesRepository) {
        this.heritageSiteRepository = heritageSiteRepository;
        this.suburbRepository = suburbRepository;
        this.touristSitesRepository = touristSitesRepository;
    }

    public List<HeritageSiteEntity> findHeritageSiteNearby(double lat, double lon, double radius){
        return getSites(lat, lon, radius);

    }

    @Override
    public List<TouristSites> findHeritageSitesNearSuburb(String suburb, double radius) {

        Map<String, Double> suburbLocation = findSuburbLatLng(suburb);
        assert suburbLocation != null;
        double lat = suburbLocation.get(StringUtils.LATITUDE);
        double lon = suburbLocation.get(StringUtils.LONGITUDE);
        return getTouristSites(lat, lon, radius);
    }

    @Override
    public List<Suburb> findByNameRegex(String suburb) {
        String regex = "(?i)^"+suburb+".*";
        return suburbRepository.findByNameRegex(regex);
    }

    @Override
    public List<TouristSites> findTouristSitesNearby(double lat, double lon, double radius) {
        return getTouristSites(lat, lon, radius);
    }

    private List<TouristSites> getTouristSites(double lat, double lon, double radius) {

        Point point = new Point(lon, lat);
        Distance radiusOfSearch = new Distance(radius, Metrics.KILOMETERS);
        List<TouristSites> touristSites = touristSitesRepository.findByGeometryNear(point, radiusOfSearch);
        return touristSites;
    }

    private Map<String, Double> findSuburbLatLng(String suburb) {
        try{
            Suburb suburbDetails = suburbRepository.findSuburbByName(suburb);
            assert suburbDetails != null;
            Map<String, Double> suburbLocation = new HashMap<>();
            suburbLocation.put(StringUtils.LATITUDE, suburbDetails.getGeometry().getY());
            suburbLocation.put(StringUtils.LONGITUDE, suburbDetails.getGeometry().getX());
            return suburbLocation;
        } catch (Exception e){
            return null;
        }
    }

    private List<HeritageSiteEntity> getSites(double lat, double lon, double radius) {
        Point point = new Point(lon, lat);
        Distance radiusOfSearch = new Distance(radius, Metrics.KILOMETERS);
        return heritageSiteRepository.findByLocationNear(point, radiusOfSearch);
    }

}
