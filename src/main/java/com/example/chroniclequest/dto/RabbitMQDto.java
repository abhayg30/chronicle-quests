package com.example.chroniclequest.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQDto {

    private String sessionId;
    private double lat;
    private double lon;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
