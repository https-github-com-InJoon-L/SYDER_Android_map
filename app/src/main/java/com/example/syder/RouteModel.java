package com.example.syder;

public class RouteModel {


    private String routeId;
    private String startingId;
    private String arrivalPoint;
    private int travelTime;
    private int travelDistance;

    RouteModel(String routeId, String startingId, String arrival_point ,int travelTime, int travelDistance) {
        this.routeId = routeId;
        this.startingId = startingId;
        this.arrivalPoint = arrival_point;
        this.travelTime = travelTime;
        this.travelDistance = travelDistance;
    }

    public String getArrivalPoint() {
        return arrivalPoint;
    }

    public void setArrivalPoint(String arrivalPoint) {
        this.arrivalPoint = arrivalPoint;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStartingId() {
        return startingId;
    }

    public void setStartingId(String startingId) {
        this.startingId = startingId;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }

    public int getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(int travelDistance) {
        this.travelDistance = travelDistance;
    }
}