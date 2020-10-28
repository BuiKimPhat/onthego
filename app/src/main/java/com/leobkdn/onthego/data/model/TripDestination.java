package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

import java.util.Date;

public class TripDestination extends Destination {
    private Date startTime;
    private Date finishTime;

    public TripDestination(int id, String name, @Nullable String description, @Nullable String category, @Nullable String city, @Nullable String position, @Nullable Date startTime, @Nullable Date finishTime) {
        super(id, name, description, category, city, position);
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
    public TripDestination(int id, String name, @Nullable Date startTime, @Nullable Date finishTime) {
        super(id, name, null, null, null, null);
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }
}
