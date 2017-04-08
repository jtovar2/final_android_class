package com.tovar.javier.demolisher;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tovar.javier.demolisher.service.LocationService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public final int LOCATION_PERMISSION_RESULT = 10000;

    LocationReciever  locationReciever;
    @BindView(R.id.main_location) TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initLocationReciever();
        askForLocationPermissions();

    }


    private void askForLocationPermissions()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_RESULT);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            Log.d(MainActivity.class.toString(), "permission are set");
            startLocationService();
        }
    }


    public void startLocationService()
    {
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("MSG", LocationService.PERMISSION_SET);
        this.startService(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(this, LocationService.class);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void initLocationReciever()
    {
        locationReciever = new LocationReciever();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.LocationServiceStatus);
        this.registerReceiver(locationReciever, filter);
    }

    public class LocationReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(LocationService.LocationServiceStatus))
            {
                switch(intent.getIntExtra("LocationServiceStatus", 0000))
                {
                    case LocationService.notifyLocation:
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lon = intent.getDoubleExtra("lon", 0);
                        location.setText("Your current location lat: " + lat + " longitude: " + lon);
                        break;
                    case LocationService.notifyPermission:
                        askForLocationPermissions();

                default:
                    location.setText("theres been an error");
                }
            }
        }
    }
}
