package com.fontys.android.andr2.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserLocation implements Serializable {
    private Double latitude;
    private Double longitude;
    private Marker marker;
    private String status;
    private long lstUpdate;
    private long logoutAt;

    public UserLocation() {
    }

    public UserLocation(Double latitude, Double longitude, String status, long lstUpdate) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.lstUpdate = lstUpdate;
    }

    public static double CalculateUsersDistance(LatLng user1, LatLng user2) {
        double distance;
        Location locationA = new Location("A");
        locationA.setLatitude(user1.latitude);
        locationA.setLongitude(user1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(user2.latitude);
        locationB.setLongitude(user2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(long lastUpdate) {
        this.lstUpdate = lastUpdate;
    }

    public long getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(long logoutAt) {
        this.logoutAt = logoutAt;
    }
}
