package com.fontys.android.andr2.activities;

import android.location.Location;

import com.fontys.android.andr2.models.User;
import com.fontys.android.andr2.models.UserLocation;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CalculateUsersDistanceTest {
    @Test
    public void testCalculateUsersDistance() {
        double DELTA = 1e-15;
        UserLocation userLocation = new UserLocation();

        UserLocation userLocation1 = Mockito.mock(UserLocation.class);
        when(userLocation1.getLatitude()).thenReturn(51.441643);
        when(userLocation1.getLongitude()).thenReturn(51.441743);

        UserLocation userLocation2 = Mockito.mock(UserLocation.class);
        when(userLocation2.getLatitude()).thenReturn(51.469722);
        when(userLocation2.getLongitude()).thenReturn(51.283423);


        Location locationA = Mockito.mock(Location.class);
        locationA.setLatitude(userLocation1.getLatitude());
        locationA.setLongitude(userLocation1.getLongitude());

        Location locationB = Mockito.mock(Location.class);
        locationB.setLatitude(userLocation2.getLatitude());
        locationB.setLongitude( userLocation2.getLongitude());



        double result = userLocation.CalculateUsersDistance(new LatLng(locationA.getAltitude(),locationA.getLongitude()), new LatLng(locationB.getLatitude(), locationB.getLongitude()));

        Assert.assertEquals(0.0,result,DELTA);
    }
}