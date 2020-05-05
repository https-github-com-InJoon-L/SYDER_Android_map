package com.example.syder;

public class MarkerModel {
    Double lat;
    Double lng;
    String title;

    public MarkerModel(Double lat,Double lng, String title) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
