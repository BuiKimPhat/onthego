package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

public class Destination {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private String category;
    private int inCost;
    private int avgCost;
    private float rating;
    private String city;
    private String position;

    public Destination(Destination x){
        this.id = x.id;
        this.name = x.name;
        this.address = x.address;
        this.phone = x.phone;
        this.description = x.description;
        this.category = x.category;
        this.inCost = x.inCost;
        this.avgCost = x.avgCost;
        this.rating = x.rating;
        this.city = x.city;
        this.position = x.position;
    }

    public Destination(int id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public Destination(int id, String name, String city, float rating) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.rating = rating;
    }

    public Destination(int id, String name, String address, String phone, String description, String category, int inCost, int avgCost, float rating, String city, String position) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.category = category;
        this.inCost = inCost;
        this.avgCost = avgCost;
        this.rating = rating;
        this.city = city;
        this.position = position;
    }

    public Destination(int id, String name, @Nullable String address, @Nullable String phone, @Nullable String description, @Nullable String category, @Nullable String city, @Nullable String position) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.inCost = inCost;
        this.avgCost = avgCost;
        this.rating = rating;
        this.city = city;
        this.position = position;
        this.address = address;
        this.phone = phone;
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

    public int getInCost() {
        return inCost;
    }

    public int getAvgCost() {
        return avgCost;
    }

    public float getRating() {
        return rating;
    }

    public String getCity() {
        return city;
    }

    public String getPosition() {
        return position;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
