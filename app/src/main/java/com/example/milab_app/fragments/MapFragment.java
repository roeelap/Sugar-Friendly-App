package com.example.milab_app.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.milab_app.R;
import com.example.milab_app.objects.Restaurant;
import com.example.milab_app.utility.DataFetcher;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private GoogleMap mMap;
    private LatLng currentDeviceLocation;
    private boolean locationPermissionGranted = false;

    private ArrayList<Restaurant> restaurants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // get location permission and current device location from main activity
        Log.e(TAG, "onCreateView: " + getArguments());
        if (getArguments() != null) {
            locationPermissionGranted = getArguments().getBoolean("locationPermissionGranted");
            currentDeviceLocation = getArguments().getParcelable("currentDeviceLocation");
            Log.e(TAG, "locationPermissionGranted: " + locationPermissionGranted);
            Log.e(TAG, "currentDeviceLocation: " + currentDeviceLocation);
        }

        // fetch restaurants and init map
        if (restaurants == null) {
            fetchRestaurants(rootView);
        } else {
            initMap();
        }

        return rootView;
    }

    private void fetchRestaurants(View rootView) {
        final DataFetcher fetcher = new DataFetcher(rootView.getContext());
        fetcher.fetchRestaurants(response -> {
            if (response.isError()) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(rootView.getContext(), "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            restaurants = response.getRestaurants();
            initMap();
        });
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            // move camera to current location
            if (currentDeviceLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentDeviceLocation, 12));
            }
            updateLocationUi();
            createRestaurantMarkers();
        });
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
            }
        } catch (SecurityException e) {
            Log.e(TAG,"Exception: %s", e);
        }
    }

    private void createRestaurantMarkers() {
        for (Restaurant restaurant : restaurants) {
            LatLng latLng = getLatLngFromAddress(restaurant);
            if (latLng != null) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(restaurant.getName()));
            }
        }
    }

    /**
     * Get the restaurant's location as a LatLng object from its address.
     * @return LatLng object of the restaurant's location
     */
    public LatLng getLatLngFromAddress(Restaurant restaurant) {
        LatLng latLng = null;
        try {
            Geocoder geocoder = new Geocoder(requireContext());
            List<Address> addresses = geocoder.getFromLocationName(restaurant.getAddress(), 1);
            double latitude= addresses.get(0).getLatitude();
            double longitude= addresses.get(0).getLongitude();
            latLng = new LatLng(latitude, longitude);
        } catch (IOException e) {
            Log.e(TAG, "getLatLngFromAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return latLng;
    }
}