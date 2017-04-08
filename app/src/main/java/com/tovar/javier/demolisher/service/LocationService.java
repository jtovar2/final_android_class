package com.tovar.javier.demolisher.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by JXT0589 on 4/4/17.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private Timer mTimer;
    LocationRequest mLocationRequest;

    private int msg;

    public static final int GET_LOCATION = 222;
    public static final int PERMISSION_SET = 444;

    public static final int notifyLocation = 1111;
    public static final int notifyError = 000;
    public static final int notifyPermission = 1234;


    public static final String LocationServiceStatus = "com.tovar.javier.demolisher.service.LocationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LocationService.class.toString(), "service created");
        //start locaiton services

    }

    public void createGoogleApiClient()
    {
        Log.d(LocationService.class.toString(), "creating google api client");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        mGoogleApiClient.connect();
    }

    public boolean checkForLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            notifyPermission();
            return false;
        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) return super.onStartCommand(intent, flags, startId);

        else{
            msg = intent.getIntExtra("MSG", 0); // 播放信息


            switch (msg) {

                case GET_LOCATION:
                    if(mLastLocation == null || !checkForLocationPermission())
                    {
                        notifyError("No current Location or no permission");
                    }
                    notifyLocation();
                    break;
                case PERMISSION_SET:
                    if(!checkForLocationPermission())
                    {
                        notifyError("No permissions");
                    }
                    createGoogleApiClient();
                    break;
                default:
                    createGoogleApiClient();
            }

            return super.onStartCommand(intent, flags, startId);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mGoogleApiClient.disconnect();
        mTimer = null;
        //end location services
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LocationService.class.toString(), "yoo onlocationchanged");
        mLastLocation = location;
        notifyLocation();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LocationService.class.toString(), "connected???");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            notifyPermission();
            Log.d(LocationService.class.toString(), "connected but doesnt have permissions");
            return;
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(20000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(LocationService.class.toString(), "timer started and location set");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void notifyLocation()
    {
        Intent intent = new Intent();
        intent.putExtra("LocationServiceStatus", notifyLocation);
        intent.putExtra("lat", mLastLocation.getLatitude());
        intent.putExtra("lon", mLastLocation.getLongitude());
        sendNotify(intent);

    }

    private void notifyPermission()
    {
        Intent intent = new Intent();
        intent.putExtra("LocationServiceStatus", notifyPermission);
        sendNotify(intent);
    }

    private void notifyError(String message)
    {
        Intent intent = new Intent();
        intent.putExtra("LocationServiceStatus", notifyError);
        intent.putExtra("message", message);
        sendNotify(intent);
    }

    private void sendNotify(Intent intent)
    {
        intent.setAction(LocationServiceStatus);
        sendBroadcast(intent);
    }
}
