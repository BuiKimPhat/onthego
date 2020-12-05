package com.leobkdn.onthego.data.model;

public class Weather {
    private float temp;
    private String description;
    private String icon;

    public Weather(float temp, String description, String icon) {
        this.temp = temp;
        this.description = description;
        this.icon = icon;
    }

    public float getTemp() {
        return temp;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
