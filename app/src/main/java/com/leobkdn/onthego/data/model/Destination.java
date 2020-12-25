package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

public class Destination {
    protected int id;
    protected String name;
    protected String address;
    protected String description;
    protected String category;
    protected float rating;
    protected int rateNum;
    protected float lat;
    protected float lon;

    public Destination(){
    }
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
    public Destination(int id, String name, float lat, float lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setRateNum(int rateNum) {
        this.rateNum = rateNum;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
