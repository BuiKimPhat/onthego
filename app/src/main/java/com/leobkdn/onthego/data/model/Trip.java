package com.leobkdn.onthego.data.model;

import com.leobkdn.onthego.data.model.Destination;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private int id;
    private String name;
    private String owner;
    private Date createdAt;
    private ArrayList<TripDestination> destinations;

    public Trip(Trip x){
        this.id = x.id;
        this.name = x.name;
        this.owner = x.owner;
        this.createdAt = x.createdAt;
    }

    public Trip(int id, String name, String owner, Date createdAt) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public Trip(int id, String name, String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public Trip(int id, String name, String owner, Date createdAt, ArrayList<TripDestination> destinations) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.createdAt = createdAt;
        this.destinations = destinations;
    }

    public ArrayList<TripDestination> getDestinations() {
        return destinations;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    public int tripCost(){
        return 0;
    }
}
