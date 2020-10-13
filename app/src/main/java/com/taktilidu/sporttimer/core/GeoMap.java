package com.taktilidu.sporttimer.core;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.view.YaMapEx;

public class GeoMap {

    private GoogleMap mGoogleMap;
    private YaMapEx mYandexMap;
    private View mView;

    static Location imHere;

    public GeoMap(Activity activity, Context context, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {
                            imHere = location;
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });

            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


            /*
            View rootView = inflater.inflate(
                    R.layout.activity_maps, container, false);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) activity).getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    // Add a marker in Sydney and move the camera
                    LatLng sydney = new LatLng(-34, 151);
                    mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setZoomControlsEnabled( true );
                }
            });

            //*/

            //*
            View rootView = inflater.inflate(
                    R.layout.map_view, container, false);

            MapView mapView = (MapView) rootView.findViewById(R.id.google_map);
            mapView.onCreate(savedInstanceState);

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    // Add a marker in Sydney and move the camera
                    LatLng sydney = new LatLng(-34, 151);
                    mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setZoomControlsEnabled( true );
                }
            });
            //*/
            this.mView = rootView;
        }
    }

    public View getView() {
        return mView;
    }

    public static GeoMap sportObjectsMap(Activity activity, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        GeoMap geoMap;
        geoMap = new GeoMap(activity, activity.getBaseContext(),inflater,container,savedInstanceState);
        return geoMap;
    }
}
