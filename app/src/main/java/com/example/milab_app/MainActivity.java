package com.example.milab_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // fragments
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    MapFragment mapFragment = new MapFragment();
    AddFragment addFragment = new AddFragment();
    CameraFragment cameraFragment = new CameraFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    // user
    private User user;
    private final List<Dish> dishes = new ArrayList<>();

    // location
    private boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentDeviceLocation;
    private final Bundle locationBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserAndDishes();

        // set up location permission and get current device location
        getLocationPermission();
        if (locationPermissionGranted) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getCurrentDeviceLocation();
        }

        // load home fragment when app starts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        // initialize bottom navigation
        initBottomNavigation();
    }


    private void getUserAndDishes() {
        // TODO: get user from login activity
        // User user = getIntent().getParcelableExtra("user");
        user = User.getInstance("Roee", "roee", null, null, null, null);
        // TODO: get dishes from database
        dishes.add(new Dish("Dish 1", new Restaurant("Restaurant 1")));
        dishes.add(new Dish("Dish 2", new Restaurant("Restaurant 2")));
        dishes.add(new Dish("Dish 3", new Restaurant("Restaurant 3")));
        dishes.add(new Dish("Dish 4", new Restaurant("Restaurant 4")));
        dishes.add(new Dish("Dish 5", new Restaurant("Restaurant 5")));
        dishes.add(new Dish("Dish 6", new Restaurant("Restaurant 6")));
        dishes.add(new Dish("Dish 7", new Restaurant("Restaurant 7")));
        dishes.add(new Dish("Dish 8", new Restaurant("Restaurant 8")));
        dishes.add(new Dish("Dish 9", new Restaurant("Restaurant 9")));
        dishes.add(new Dish("Dish 10", new Restaurant("Restaurant 10")));
    }


    /**
     * Initialize bottom navigation buttons
    **/
    @SuppressLint("NonConstantResourceId")
    private void initBottomNavigation() {
        Log.d(TAG, "initializing bottom navigation");
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    return true;
                case R.id.navigation_map:
                    mapFragment.setArguments(locationBundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
                    return true;
                case R.id.navigation_add:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addFragment).commit();
                    return true;
                case R.id.navigation_camera:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cameraFragment).commit();
                    return true;
                case R.id.navigation_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                    return true;
            }
            return false;
        });
    }


    /* Methods related to location permission, and getting current device location */

    /**
     * Get location permissions from user.
     * Will pop up a dialog box asking for permission.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            // if location permission is not granted request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getCurrentDeviceLocation();
        }
    }

    /**
     * Get current device location, and store it in currentDeviceLocation and the location bundle.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            Log.d(TAG, "getCurrentDeviceLocation: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
                            currentDeviceLocation = lastKnownLocation;
                            locationBundle.putBoolean("locationPermissionGranted", locationPermissionGranted);
                            locationBundle.putParcelable("currentDeviceLocation", currentDeviceLocation);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: %s", e);
        }
    }
}