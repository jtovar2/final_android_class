package com.tovar.javier.demolisher;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tovar.javier.demolisher.service.LocationService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final int LOCATION_PERMISSION_RESULT = 10000;
    public final int STOP_COUNTER = 123;
    public final int RESET_COUNTER = 124;
    public final int START_COUNTER = 111;

    public final int DEFAULT_TIME = 60000;
    int countdown_time;

    LocationReciever  locationReciever;
    @BindView(R.id.main_location) TextView location;
    @BindView(R.id.main_radio_group) RadioGroup radioGroup;

    @BindView(R.id.main_countdown_start_radio)
    RadioButton startCountdownRadio;

    @BindView(R.id.main_countdown_stop_radio)
    RadioButton stopCountdownRadio;

    @BindView(R.id.main_countdown_reset_radio)
    RadioButton resetCountdownRadio;

    @BindView(R.id.main_countdown_text)
    TextView countdownText;
    int selectedRadioButton;

    Handler mHandler;

    Runnable countdown = new Runnable() {
        @Override
        public void run() {
                countdown_time = countdown_time - 1000;
                countdownText.setText(String.valueOf(countdown_time));
            if(!(countdown_time == 0))
            {
                mHandler.postDelayed(countdown, 1000);
            }
        }
    };

    @OnClick(R.id.main_dialog_button)
    public void radioGroupSelect()
    {
        switch(selectedRadioButton)
        {
            case R.id.main_viewpager_radio:
                goToViewPager();
                break;
            case R.id.main_countdown_start_radio:
                startCountdown();
                break;
            case R.id.main_countdown_reset_radio:
                resetCountdown();
                break;
            case R.id.main_countdown_stop_radio:
                stopCountdown();
                break;
            default:
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToViewPager()
    {
        Intent intent = new Intent(this, ViewPagerActivity.class);
        startActivity(intent);

    }

    public void startCountdown()
    {
        startCountdownRadio.setVisibility(View.INVISIBLE);
        stopCountdownRadio.setVisibility(View.VISIBLE);
        mHandler.postDelayed(countdown, 1000);
    }

    public void stopCountdown()
    {
        stopCountdownRadio.setVisibility(View.INVISIBLE);
        startCountdownRadio.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(countdown);
    }

    public void resetCountdown()
    {
        countdown_time = DEFAULT_TIME;
        countdownText.setText(String.valueOf(countdown_time));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initLocationReciever();
        askForLocationPermissions();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                selectedRadioButton = checkedId;
            }
        });

        initHandler();
        countdown_time = DEFAULT_TIME;
        countdownText.setText(String.valueOf(countdown_time));
        stopCountdownRadio.setVisibility(View.INVISIBLE);

    }


    public void initHandler()
    {
        mHandler = new Handler(Looper.getMainLooper());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReciever);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountdown();
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
                        location.setText("You must allow the app to use Location services");
                        askForLocationPermissions();

                default:
                    location.setText("theres been an error with the location services");
                }
            }
        }
    }




}
