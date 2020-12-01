package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

public class Destination {
    private int id;
    private String name;
    private String address;
    private String description;
    private String category;
    private float rating;
    private int rateNum;
    private float lat;
    private float lon;

    public Destination(Destination x) {
        this.id = x.id;
        this.name = x.name;
        this.address = x.address;
        this.description = x.description;
        this.category = x.category;
        this.rating = x.rating;
        this.rateNum = x.rateNum;
        this.lat = x.lat;
        this.lon = x.lon;
    }

    public Destination(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Destination(int id, String name, @Nullable String address, @Nullable String description, @Nullable String category, float rating, int rateNum, float lat, float lon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.rating = rating;
        this.address = address;
        this.rateNum = rateNum;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public float getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }

    public int getRateNum() {
        return rateNum;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}
