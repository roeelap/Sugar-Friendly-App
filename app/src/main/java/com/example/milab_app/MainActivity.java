package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.milab_app.fragments.AddFragment;
import com.example.milab_app.fragments.CameraFragment;
import com.example.milab_app.fragments.HomeFragment;
import com.example.milab_app.fragments.MapFragment;
import com.example.milab_app.fragments.ProfileFragment;
import com.example.milab_app.objects.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private final User user = User.getInstance("Roee", "roee", null, null, null, null);

    private final Bundle locationBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up location
        boolean locationPermissionGranted = getIntent().getBooleanExtra("locationPermissionGranted", false);
        LatLng currentDeviceLocation = getIntent().getParcelableExtra("currentDeviceLocation");
        Log.e(TAG, "locationPermissionGranted: " + locationPermissionGranted);
        Log.e(TAG, "currentDeviceLocation: " + currentDeviceLocation);
        locationBundle.putBoolean("locationPermissionGranted", locationPermissionGranted);
        locationBundle.putParcelable("currentDeviceLocation", currentDeviceLocation);

        // initialize bottom navigation
        initBottomNavigation();

        // set home fragment as default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
    }

    public User getUser() { return user; }

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
}