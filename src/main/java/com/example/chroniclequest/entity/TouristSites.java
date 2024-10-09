package com.example.chroniclequest.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "osm-museums-data")
public class TouristSites {
    @Id
    private String id;
    private String type;
    private Point geometry;

    private Properties properties;

    public String getId() {
        return id;
    }

    public TouristSites(String id, String type, Point geometry, Properties properties) {
        this.id = id;
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

    public TouristSites(){}

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Point getGeometry() {
        return geometry;
    }

    public void setGeometry(Point geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
