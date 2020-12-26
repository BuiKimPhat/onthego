package com.leobkdn.onthego.data.model;

import com.leobkdn.onthego.data.model.Destination;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private int id;
    private int ownerId;
    private String name;
    private String owner;
    private Date createdAt;
    private ArrayList<TripDestination> destinations;

    public Trip(Trip x){
        this.id = x.id;
        this.name = x.name;
        this.owner = x.owner;
        this.ownerId=x.ownerId;
        this.createdAt = x.createdAt;
    }

    public Trip(int id, String name, String owner, Date createdAt) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public Trip(int id, String name,int owner, Date createdAt) {
        this.id = id;
        this.name = name;
        this.ownerId = owner;
        this.createdAt = createdAt;
    }
    public Trip(int id, String name,int owner) {
        this.id = id;
        this.name = name;
        this.ownerId = owner;
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

    public int getOwnerId() {
        return ownerId;
    }

    public int tripCost(){
        return 0;
    }
}
