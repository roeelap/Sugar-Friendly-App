package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.example.milab_app.fragments.CameraFragment;
import com.example.milab_app.fragments.DishDetailsFragment;
import com.example.milab_app.fragments.HomeFragment;
import com.example.milab_app.fragments.ProfileFragment;
import com.example.milab_app.fragments.SearchFragment;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // fragments
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    CameraFragment cameraFragment = new CameraFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    // user
    private final User user = User.getInstance("Roee", "roee", null, null, null, null);

    private boolean locationPermissionGranted;
    private LatLng currentDeviceLocation;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        // set up location
        locationPermissionGranted = getIntent().getBooleanExtra("locationPermissionGranted", false);
        currentDeviceLocation = getIntent().getParcelableExtra("currentDeviceLocation");
        Log.e(TAG, "locationPermissionGranted: " + locationPermissionGranted);
        Log.e(TAG, "currentDeviceLocation: " + currentDeviceLocation);

        // initialize bottom navigation
        initBottomNavigation();

        // set home fragment as default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
    }

    public User getUser() { return user; }

    public LatLng getCurrentDeviceLocation() { return currentDeviceLocation; }

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
                case R.id.navigation_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
                    return true;
                case R.id.navigation_camera:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cameraFragment).commit();
                    return true;
            }
            return false;
        });
    }


    /**
     * Show a restaurant on map. Will open map fragment and focus on the restaurant.
     * @param restaurantName restaurant name
     */
    public void showAddressInGoogleMaps(String address, String restaurantName) {
        Log.e(TAG, "showAddressInGoogleMaps: " + address + " " + restaurantName);
        // TODO: open google maps with restaurant location
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address + " " + restaurantName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void createAddDishPopup() {
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.add_dish_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.add_dish_popup_animation);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        View container = findViewById(R.id.main_layout_container);
        popupWindow.showAtLocation(container, Gravity.BOTTOM, 0, 0);
    }

    public void showDishDetailsFragment(Dish dish, String previousFragmentName) {
        DishDetailsFragment dishDetailsFragment = new DishDetailsFragment(dish, previousFragmentName);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dishDetailsFragment).commit();
    }

    public void showFragment(String fragmentName) {
        switch (fragmentName) {
            case "home":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                break;
            case "search":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
                break;
        }
    }
}