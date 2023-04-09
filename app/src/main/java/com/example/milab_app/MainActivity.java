package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.milab_app.fragments.AddFragment;
import com.example.milab_app.fragments.CameraFragment;
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
    AddFragment addFragment = new AddFragment();
    CameraFragment cameraFragment = new CameraFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    // user
    private final User user = User.getInstance("Roee", "roee", null, null, null, null);

    private boolean locationPermissionGranted;
    private LatLng currentDeviceLocation;
    private String focusRestaurantName;

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
    public boolean getLocationPermissionGranted() { return locationPermissionGranted; }
    public LatLng getCurrentDeviceLocation() { return currentDeviceLocation; }
    public String getFocusRestaurantName() { return focusRestaurantName; }
    public void setFocusRestaurantName(String focusRestaurantName) { this.focusRestaurantName = focusRestaurantName; }

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

    /**
     * Opens a popup window with dish details.
     * @param dish dish object
     */
    public void showDishDetailsPopup(Dish dish) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_window, null);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        PopupWindow popupWindow = new PopupWindow(popupView);
        popupWindow.setWidth(width - 100);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        setUpPopupView(popupView, popupWindow, dish);
    }

    private void setUpPopupView(View popupView, PopupWindow popupWindow, Dish dish) {
        TextView dishName = popupView.findViewById(R.id.dishNamePopup);
        dishName.setText(dish.getName());

        TextView restaurantName = popupView.findViewById(R.id.restaurantNamePopup);
        restaurantName.setText(dish.getRestaurantName());

        RatingBar dishRating = popupView.findViewById(R.id.ratingBarPopup);
        dishRating.setRating((float) dish.getRating());

        ImageView dishImage = popupView.findViewById(R.id.dishImagePopup);
        dishImage.setImageResource(R.drawable.sushi);

        popupView.findViewById(R.id.btnClosePopup).setOnClickListener(v -> popupWindow.dismiss());
        popupView.findViewById(R.id.showOnMapBtn).setOnClickListener(v -> {
            popupWindow.dismiss();
            showAddressInGoogleMaps(dish.getAddress(), dish.getRestaurantName());
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
}