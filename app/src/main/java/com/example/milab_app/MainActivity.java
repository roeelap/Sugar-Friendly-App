package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.milab_app.objects.Dish;
import com.example.milab_app.fragments.AddFragment;
import com.example.milab_app.fragments.CameraFragment;
import com.example.milab_app.fragments.HomeFragment;
import com.example.milab_app.fragments.MapFragment;
import com.example.milab_app.fragments.ProfileFragment;
import com.example.milab_app.objects.Restaurant;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.objects.User;
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
    private ArrayList<Dish> recommendedDishes;
    private ArrayList<Dish> topRatedDishes;
    private ArrayList<Dish> newestDishes;
    private ArrayList<Restaurant> restaurants;

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
        fetchData();

        // initialize bottom navigation
        initBottomNavigation();
    }

    public User getUser() { return user; }
    public ArrayList<Dish> getRecommendedDishes() { return recommendedDishes; }
    public ArrayList<Dish> getTopRatedDishes() { return topRatedDishes; }
    public ArrayList<Dish> getNewestDishes() { return newestDishes; }
    public ArrayList<Restaurant> getRestaurants() { return restaurants; }

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

    private void fetchData() {
        final DataFetcher fetcher = new DataFetcher(MainActivity.this);
        fetcher.dispatchRequest(response -> {
            if (response.isError) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(MainActivity.this, "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            recommendedDishes = response.recommendedDishes;
            topRatedDishes = response.topRatedDishes;
            newestDishes = response.newestDishes;
            restaurants = response.restaurants;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        });
    }
}