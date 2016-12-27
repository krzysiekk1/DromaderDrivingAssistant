package com.skobbler.sdkdemo.costs.utils;

/**
 * Created by Krzysiek
 */

public class TollPoint {

    private int id;
    private String name;
    private String roadNr;
    private double latitude;
    private double longitude;
    private String countryCode;
    private double cost;

    public TollPoint(int id, String name, String roadNr, String latitude, String longitude, String countryCode, double cost) {
        this.id = id;
        this.name = name;
        this.roadNr = roadNr;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.countryCode = countryCode;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoadNr() {
        return roadNr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getCost() {
        return cost;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoadNr(String roadNr) {
        this.roadNr = roadNr;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

}
