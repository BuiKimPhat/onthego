package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.Date;

public class TripDestination extends Destination {
    private Timestamp startTime;
    private Timestamp finishTime;

//    public TripDestination(int id, String name, @Nullable String address, @Nullable String phone, @Nullable String description, @Nullable String category, @Nullable String city, @Nullable String position, @Nullable Timestamp startTime, @Nullable Timestamp finishTime) {
//        super(id, name, address, phone, description, category, city, position);
//        this.startTime = startTime;
//        this.finishTime = finishTime;
//    }

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
