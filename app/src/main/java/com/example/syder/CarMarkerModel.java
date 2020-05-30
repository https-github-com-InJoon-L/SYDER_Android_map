package com.example.syder;

import com.google.android.gms.maps.model.Marker;

public class CarMarkerModel {
    private Marker carMarker;
    private String carNumber;
    private int car_status;
    private Double car_lat;
    private Double car_lng;
    private int car_battery;

    public CarMarkerModel(Marker carMarker, String carNumber) {
        this.carMarker      = carMarker;
        this.carNumber      = carNumber;
    }
    public CarMarkerModel(String carNumber, int car_status, Double car_lat,Double car_lng, int car_battery) {
        this.car_lat        = car_lat;
        this.car_lng        = car_lng;
        this.car_battery    = car_battery;
        this.carNumber      = carNumber;
        this.car_status     = car_status;
    }



    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public int getCar_status() {
        return car_status;
    }

    public void setCar_status(int car_status) {
        this.car_status = car_status;
    }

    public Double getCar_lat() {
        return car_lat;
    }

    public void setCar_lat(Double car_lat) {
        this.car_lat = car_lat;
    }

    public Double getCar_lng() {
        return car_lng;
    }

    public void setCar_lng(Double car_lng) {
        this.car_lng = car_lng;
    }

    public int getCar_battery() {
        return car_battery;
    }

    public void setCar_battery(int car_battery) {
        this.car_battery = car_battery;
    }

    public Marker getCarMarker() {
        return carMarker;
    }

    public void setCarMarker(Marker carMarker) {
        this.carMarker = carMarker;
    }
}