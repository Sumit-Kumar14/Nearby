package com.infinity.dev.Utility;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class Locator extends AsyncTask<Void, Void, Location> implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = "NEARBY_FIND_LOC_TASK";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    Locatable user;
    Context context;

    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    public Locator(Context context, Locatable user) {
        this.context = context;
        this.user = user;
        this.mCurrentLocation = null;
    }

    @Override
    protected void onPreExecute() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if(ConnectionResult.SUCCESS == status) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        } else {
            //Google play service is not available. Inform user and exit
        }
    }

    @Override
    protected Location doInBackground(Void... params) {
        while (mCurrentLocation == null) {
            // Busy wait
        }
        return mCurrentLocation;
    }

    @Override
    protected void onPostExecute(Location l) {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        user.onLocationComplete(l);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation == null) {
            startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.e("FindCurrentLocation", "Latitude : " + mCurrentLocation.getLatitude() + " Longitude : " + mCurrentLocation.getLongitude());
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }
}