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
import android.widget.Toast;

import com.example.milab_app.fragments.CameraFragment;
import com.example.milab_app.fragments.DishDetailsFragment;
import com.example.milab_app.fragments.HomeFragment;
import com.example.milab_app.fragments.SearchFragment;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.User;
import com.example.milab_app.utility.DataFetcher;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // fragments
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    CameraFragment cameraFragment = new CameraFragment();
    // ProfileFragment profileFragment = new ProfileFragment();

    // user
    private User user;
    private boolean locationPermissionGranted;
    private LatLng currentDeviceLocation;
    private final String defaultUserName = "roee";

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

        String fragment = getIntent().getStringExtra("fragment");

        // fetch user if this is the first time main activity is opened
        if (user == null) {
            fetchUser(new Callback() {
                @Override
                public void onSuccess() {
                    // initialize bottom navigation and open fragment
                    initBottomNavigation();
                    showFragment(fragment);
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, message);
                    toast(message);
                }
            });
        } else {
            // initialize bottom navigation and open fragment
            initBottomNavigation();
            showFragment(fragment);
        }
    }

    public User getUser() { return user; }
    public HashSet<String> getFavDishes() { return user.getFavDishes(); }
    public LatLng getCurrentDeviceLocation() { return currentDeviceLocation; }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchUser(Callback callback) {
        Log.d(TAG, "fetchDishes");
        // show progress bar
        showProgressBar();

        final DataFetcher fetcher = DataFetcher.getInstance(this);
        fetcher.fetchUser(defaultUserName, response -> {
            // hide progress bar
            hideProgressBar();
            if (response.isError()) {
                callback.onError("Error fetching user");
                return;
            }

            // update user
            user = response.getUser();
            Log.d(TAG, "Fetched user successfully");
            callback.onSuccess();
        });
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

    public void showFragment(String FragmentName) {
        if (FragmentName == null) {
            Log.e(TAG, "openFragment: FragmentName is null, opening home fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            return;
        }
        switch (FragmentName) {
            case "home":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                break;
            case "search":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
                break;
            case "camera":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cameraFragment).commit();
                break;
        }
    }

    public void showDishDetailsFragment(Dish dish, String previousFragmentName) {
        DishDetailsFragment dishDetailsFragment = new DishDetailsFragment(dish, previousFragmentName);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dishDetailsFragment).commit();
    }

    public void toggleDishLikability(String dishId, Callback callback) {
        user.toggleDishLikability(dishId);
        Log.e(TAG, "toggleDishLikability: " + user.getFavDishes());
        final DataFetcher fetcher = DataFetcher.getInstance(this);
        fetcher.updateUser(user, response -> {
            if (response.isError()) {
                toast("Server Error");
                user.toggleDishLikability(dishId); // revert changes if server error
                callback.onError("Error liking dish");
            } else {
                homeFragment.refreshRecyclerViews();
                callback.onSuccess();
            }
        });
    }

    public interface Callback {
        void onSuccess();
        void onError(String message);
    }
}