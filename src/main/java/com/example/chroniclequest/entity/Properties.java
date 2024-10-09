package com.example.chroniclequest.entity;

import lombok.Data;

@Data
public class Properties {

    private String name;
    private String tourism;

    public Properties(String name, String tourism) {
        this.name = name;
        this.tourism = tourism;
    }
    public Properties(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTourism() {
        return tourism;
    }

    public void setTourism(String tourism) {
        this.tourism = tourism;
    }
}
