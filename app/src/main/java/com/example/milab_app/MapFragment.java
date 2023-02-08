package com.example.milab_app;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private GoogleMap mMap;
    private Location currentDeviceLocation;
    private boolean locationPermissionGranted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // get location permission and current device location from main activity
        if (getArguments() != null) {
            locationPermissionGranted = getArguments().getBoolean("locationPermissionGranted");
            currentDeviceLocation = getArguments().getParcelable("currentDeviceLocation");
            Log.d(TAG, "locationPermissionGranted: " + locationPermissionGranted);
            Log.d(TAG, "currentDeviceLocation: " + currentDeviceLocation);
        }

        // init map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            // move camera to current location
            if (currentDeviceLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(currentDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude()), 15
                ));
            }
            updateLocationUi();
        });

        return rootView;
    }

    private void updateLocationUi() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                currentDeviceLocation = null;
            }
        } catch (SecurityException e) {
            Log.e(TAG,"Exception: %s", e);
        }
    }
}