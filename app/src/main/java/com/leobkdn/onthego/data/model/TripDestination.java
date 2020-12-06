package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.Date;

public class TripDestination extends Destination {
    private Timestamp startTime;
    private Timestamp finishTime;

    public TripDestination(int id, String name, float latitude, float longitude , @Nullable Timestamp startTime, @Nullable Timestamp finishTime) {
        super(id, name, latitude, longitude);
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public TripDestination(int id, String name, String address, String description, float rating, int rateNum, float latitude, float longitude , @Nullable Timestamp startTime, @Nullable Timestamp finishTime) {
        super(id, name, address, description,null, rating, rateNum, latitude, longitude);
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public TripDestination(int id, String name, @Nullable Timestamp startTime, @Nullable Timestamp finishTime) {
        super(id, name);
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }
}
