package com.tovar.javier.demolisher.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tovar.javier.demolisher.BaseMartaFragment;
import com.tovar.javier.demolisher.R;
import com.tovar.javier.demolisher.model.MartaBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier on 4/9/17.
 */

public class MapFragment extends Fragment implements BaseMartaFragment{
    MapView mMapView;
    private GoogleMap googleMap;


    private static final String LAT = "latitude";
    private static final String LON = "website_url";



    private Double mLatitude;
    private Double mLongitude;

    private Marker user;
    private List<Marker> busMarkers;

    private Boolean userCameraZoomToggle;
    private Boolean busCameraZoomToggle;




    public MapFragment() {
        // Required empty public constructor
    }



    public static MapFragment newInstance(Double latitude, Double longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        if(latitude != null && longitude != null)
        {
            args.putDouble(LAT, latitude);
            args.putDouble(LON, longitude);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLatitude = getArguments().getDouble(LAT);
            mLongitude = getArguments().getDouble(LON);


        }
        busCameraZoomToggle = true;
        userCameraZoomToggle = true;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.fragment_map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if(mLongitude != null && mLongitude != null) {
                    LatLng usersPosition = new LatLng(mLatitude, mLongitude);


                    MarkerOptions options = new MarkerOptions().position(usersPosition).title("This is you").snippet("Your geolocation");
                    user = googleMap.addMarker(options);


                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(usersPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        return rootView;
    }


    public void updateUserLocation(LatLng position)
    {
        if(user != null)
        {
            user.setPosition(position);
        }

        else
        {
            MarkerOptions options = new MarkerOptions().position(position).title("This is you").snippet("Your geolocation");
            user = googleMap.addMarker(options);

        }

        if(userCameraZoomToggle)
        {
            userCameraZoomToggle = false;
            updateCameraZoom();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void updateMartaInfo(List<MartaBus> buses)
    {
           if(busMarkers != null)
           {
               for(Marker busMarker: busMarkers)
               {
                   busMarker.remove();
               }
           }

           ArrayList<Marker> newBusMarkers = new ArrayList<>();

        for(MartaBus bus: buses)
        {
            newBusMarkers.add(createMarker(bus.getNumLatitude(), bus.getNumLongitude(), "Marta Bus Route: " + bus.getRoute(), "Location: " + bus.getTimepoint()));
        }
        busMarkers = newBusMarkers;

        if(busCameraZoomToggle)
        {
            busCameraZoomToggle = false;
            updateCameraZoom();
        }

    }

    public void updateCameraZoom()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(user != null)
        {
            builder.include(user.getPosition());
        }

        if(busMarkers != null)
        {
            for(Marker busMarker: busMarkers)
            {
                builder.include(busMarker.getPosition());
            }
        }
        LatLngBounds bounds = builder.build();
        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cu);

    }

    public boolean isGoogleMapInit()
    {
        if(googleMap != null)
        {
            return true;
        }
        return false;
    }
    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
}
