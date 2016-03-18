package com.infinity.dev.nearby;

/**
 * Created by suny on 6/8/15.
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GetLocation implements LocationListener {

    private Context context;
    private Location location;
    protected LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    boolean isNetworkEnabled = false;

    double latitude;
    double longitude;

    private final static long MIN_DISTANCE_FOR_UPDATE = 10;
    private final static long MIN_TIME_TO_UPDATE = 0;

    Utility utility;

    public GetLocation(Context context){
        this.context = context;

        utility = new Utility(context);

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled){
            utility.showSettingsDialog();
        }
        else{
            canGetLocation = true;
            if(isNetworkEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_TO_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
            if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    public void stopGPSUpdate(){
        if(locationManager != null){
            locationManager.removeUpdates(GetLocation.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

