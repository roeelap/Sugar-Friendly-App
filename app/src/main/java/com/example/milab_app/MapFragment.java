package com.example.milab_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLocationPermission();

        // init map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {

            // move camera to current location
            if (lastKnownLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15
                ));
            }

            googleMap.setOnMapClickListener(latLng -> {
                // initialize marker options
                MarkerOptions markerOptions = new MarkerOptions();
                // set position of marker
                markerOptions.position(latLng);
                // set title of marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                // remove all markers
                googleMap.clear();
                // zooming
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 15
                ));
                // adding marker on map
                googleMap.addMarker(markerOptions);
            });
        });

        return rootView;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getCurrentLocation();
        } else {
            // if location permission is not granted request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION &&
            grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getCurrentLocation();
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) requireActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                && mLocationPermissionGranted) {
            // When location service is enabled, get last location
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                // Initialize location
                Location location = task.getResult();

                // When location result is null initialize location request=
                if (location != null) {
                    lastKnownLocation = location;
                    return;
                }

                LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(1000)
                        .setMaxUpdateDelayMillis(1)
                        .build();

                // Initialize location call back
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void
                    onLocationResult(LocationResult locationResult) {
                        // Initialize location
                        lastKnownLocation = locationResult.getLastLocation();
                    }
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            });
        } else {
            // if location services is not enabled open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}