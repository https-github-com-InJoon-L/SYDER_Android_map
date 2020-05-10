package com.example.syder;

import com.google.android.gms.maps.model.Marker;

public class MarkerModel {
    private Marker marker;
    private Double lat;
    private Double lng;
    private String title;

    public MarkerModel(Marker marker, String title) {
        this.marker = marker;
        this.title = title;
    }

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

    public Marker getMarker() { return marker; }

    public void setMarker(Marker marker) { this.marker = marker; }
}
