package com.tovar.javier.demolisher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.tovar.javier.demolisher.adapters.AnotherViewPagerAdapter;
import com.tovar.javier.demolisher.api.MartaApi;
import com.tovar.javier.demolisher.fragments.MapFragment;
import com.tovar.javier.demolisher.fragments.MartaBusListFragment;
import com.tovar.javier.demolisher.fragments.WebViewFragment;
import com.tovar.javier.demolisher.model.MartaBus;
import com.tovar.javier.demolisher.service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by javier on 4/9/17.
 */

public class ViewPagerActivity extends AppCompatActivity
{

    ViewPagerLocationReciever locationReciever;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private MapFragment mapFragment;
    private MartaBusListFragment busListFragment;
    AnotherViewPagerAdapter adapter;
    LatLng currentLocation;
    MartaApi martaApi;
    Timer mTimer;


    @OnClick(R.id.view_pager_next_fragment)
    public void nextFragment()
    {
        if(viewPager.getCurrentItem() + 1 ==  adapter.getCount())
        {
            viewPager.setCurrentItem(0);
        }
        else
        {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }

    }

    @OnClick(R.id.view_pager_prev_fragment)
    public void prevFragment()
    {
        if(viewPager.getCurrentItem() != 0)
        {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
        else
        {
            viewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_view_pager);
        ButterKnife.bind(this);
        initializeViewPager();
        initLocationReciever();



    }

    @Override
    protected void onStart() {
        super.onStart();
        initRetrofit();
    }

    public void initRetrofit()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://developer.itsmarta.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        martaApi = retrofit.create(MartaApi.class);
        startTimer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReciever);
    }

    public void initializeViewPager()
    {
        busListFragment = MartaBusListFragment.newInstance();
        if(currentLocation == null)
        {
            mapFragment = MapFragment.newInstance(null, null);
        }
        else
        {
            mapFragment = MapFragment.newInstance(currentLocation.latitude, currentLocation.longitude);
        }
        fragments = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        fragments.add(WebViewFragment.newInstance("http://www.gsu.edu"));
        fragments.add(mapFragment);
        fragments.add(busListFragment);

        adapter = new AnotherViewPagerAdapter(this.getSupportFragmentManager());
        adapter.setContent(fragments);

        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mTimer != null)
        {
            mTimer.cancel();
        }
    }

    private void initLocationReciever()
    {
        locationReciever = new ViewPagerLocationReciever();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.LocationServiceStatus);
        this.registerReceiver(locationReciever, filter);
    }


    public class ViewPagerLocationReciever extends BroadcastReceiver {

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
                        currentLocation = new LatLng(lat, lon);
                        mapFragment.updateUserLocation(currentLocation);


                        break;
                    case LocationService.notifyPermission:
                        Toast.makeText(context, "You must allow the app to use Location services", Toast.LENGTH_SHORT).show();


                    default:
                        Toast.makeText(context, "Theres an error with location services", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class getMartaData extends TimerTask {
        public void run()
        {
            Log.d(ViewPagerActivity.class.toString(), "we making api calls and shit");
            Call<List<MartaBus>> getBuses = martaApi.getMartaBuses();
            getBuses.enqueue(new Callback<List<MartaBus>>() {
                @Override
                public void onResponse(Call<List<MartaBus>> call, Response<List<MartaBus>> response) {
                    List<MartaBus> buses = response.body();
                    if(buses != null && mapFragment.isGoogleMapInit()) {
                        mapFragment.updateMartaInfo(buses);
                        busListFragment.updateMartaInfo(buses);
                    }
                    else
                    {
                        Log.d(ViewPagerActivity.class.toString(), response.message());
                        Log.d(ViewPagerActivity.class.toString(), response.body().toString());

                    }
                    Toast.makeText(getBaseContext(), "Successfully downloaded bus data", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<List<MartaBus>> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "Error retrieving bus data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startTimer()
    {
        mTimer = new Timer();
        mTimer.schedule(new getMartaData(), 0, 10000);
    }


}
