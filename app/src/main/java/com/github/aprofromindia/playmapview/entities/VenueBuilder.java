package com.github.aprofromindia.playmapview.entities;

/**
 * Created by mlu on 17.01.2017.
 */

public class VenueBuilder {
    private long id;
    private String name;
    private String address;
    private String description;
    private String category;
    private double latitude;
    private double longitude;

    public VenueBuilder addName(String name){
        this.name = name;
        return this;
    }

    public VenueBuilder addAddress(String address){
        this.address = address;
        return this;
    }

    public VenueBuilder addDescription(String description){
        this.description = description;
        return this;
    }

    public VenueBuilder addCategory(String category){
        this.category = category;
        return this;
    }

    public VenueBuilder addLatitude(double latitude){
        this.latitude = latitude;
        return this;
    }

    public VenueBuilder addLongitude(double longitude){
        this.longitude = longitude;
        return this;
    }

    public VenueBuilder addId(long id) {
        this.id = id;
        return this;
    }

    public Venue build(){
        Venue venue = new Venue();
        venue.setId(this.id);
        venue.setName(this.name);
        venue.setAddress(this.address);
        venue.setDescription(this.description);
        venue.setCategory(this.category);
        venue.setLatitude(this.latitude);
        venue.setLongitude(this.longitude);
        return venue;
    }
}
