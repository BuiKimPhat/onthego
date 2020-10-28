package com.leobkdn.onthego.ui.go;

import com.leobkdn.onthego.data.model.Destination;

import java.util.ArrayList;
import java.util.Date;

public class Trip {
    private int id;
    private String name;
    private String owner;
    private Date createdAt;
    private ArrayList<Destination> destinations;

    public Trip(int id, String name, String owner, Date createdAt) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public Trip(int id, String name, String owner, Date createdAt, ArrayList<Destination> destinations) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.createdAt = createdAt;
        this.destinations = destinations;
    }

    public ArrayList<Destination> getDestinations() {
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
