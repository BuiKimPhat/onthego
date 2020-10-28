package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

public class Destination {
    private int id;
    private String name;
    private String description;
    private String category;
    private int inCost;
    private int avgCost;
    private float rating;
    private String city;
    private String position;

    public Destination(int id, String name, String description, String category, int inCost, int avgCost, float rating, String city, String position) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.inCost = inCost;
        this.avgCost = avgCost;
        this.rating = rating;
        this.city = city;
        this.position = position;
    }
    public Destination(int id, String name, @Nullable String description, @Nullable String category, @Nullable String city, @Nullable String position) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.inCost = inCost;
        this.avgCost = avgCost;
        this.rating = rating;
        this.city = city;
        this.position = position;
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
}
