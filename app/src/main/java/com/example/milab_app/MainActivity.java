package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

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
    private ArrayList<Dish> dishes;

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

        // fetch dishes and load home fragment when app starts
        fetchAllDishes();

        // initialize bottom navigation
        initBottomNavigation();
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
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

    private void fetchAllDishes() {
        final DishFetcher fetcher = new DishFetcher(MainActivity.this);
        fetcher.dispatchRequest(response -> {
            if (response.isError) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(MainActivity.this, "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            dishes = response.dishes;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        });
    }
}