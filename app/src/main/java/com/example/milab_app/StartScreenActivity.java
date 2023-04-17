package com.example.milab_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

public class StartScreenActivity extends AppCompatActivity {

    private static final String TAG = "StartScreenActivity";

    private boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentDeviceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // set up location permission and get current device location
        getLocationPermission();
        if (locationPermissionGranted) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getCurrentDeviceLocation();
        }
    }

    /**
     * Get location permissions from user.
     * Will pop up a dialog box asking for permission.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            // if location permission is not granted request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Callback for the result from requesting permissions.
     * This method is invoked for every call on requestPermissions(android.app.Activity, String[], int).
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int).
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        getCurrentDeviceLocation();
    }

    /**
     * Get current device location, and store it in currentDeviceLocation. Will start the app.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location lastKnownLocation = task.getResult();
                        Log.d(TAG, "getCurrentDeviceLocation: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
                        currentDeviceLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults - tel aviv.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        currentDeviceLocation = new LatLng(32.0853, 34.7818);
                    }
                    startApp();
                });
            } else {
                Log.d(TAG, "Device location not permitted. Using defaults - tel aviv.");
                currentDeviceLocation = new LatLng(32.0853, 34.7818);
                startApp();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: %s", e);
        }
    }

    private void startApp() {
        Intent intent = new Intent(this, OnBoardingActivity.class);
        intent.putExtra("locationPermissionGranted", locationPermissionGranted);
        intent.putExtra("currentDeviceLocation", currentDeviceLocation);
        startActivity(intent);
        finish();
    }
}