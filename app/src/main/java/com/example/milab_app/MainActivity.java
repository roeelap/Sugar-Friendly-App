package com.example.milab_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.milab_app.fragments.CameraFragment;
import com.example.milab_app.fragments.DishDetailsFragment;
import com.example.milab_app.fragments.HomeFragment;
import com.example.milab_app.fragments.SearchFragment;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.User;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.LogmealAPI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements LogmealAPI.LogMealCallback {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    // fragments
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    CameraFragment cameraFragment = new CameraFragment();

    // user
    private User user;
    private LatLng currentDeviceLocation;

    // camera
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up location
        currentDeviceLocation = getIntent().getParcelableExtra("currentDeviceLocation");
        Log.e(TAG, "currentDeviceLocation: " + currentDeviceLocation);

        String fragment = getIntent().getStringExtra("fragment");

        // set up camera launcher for taking pictures
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            capturedImage = (Bitmap) data.getExtras().get("data");
                            if (capturedImage == null) {
                                Log.e(TAG, "capturedImage is null");
                                return;
                            }
                            //LogmealAPI.sendImage(capturedImage, this);
                            onLogMealSuccess("test");
                        }
                    } else {
                        Log.e(TAG, "camera result code: " + result.getResultCode());
                    }
                });

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
    public void showProgressBar() { findViewById(R.id.progress_bar).setVisibility(View.VISIBLE); }
    public void hideProgressBar() { findViewById(R.id.progress_bar).setVisibility(View.GONE); }
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fetch user from server and update user object.
     * @param callback callback to be called when fetch is done
     */
    private void fetchUser(Callback callback) {
        Log.d(TAG, "fetchDishes");
        // show progress bar
        showProgressBar();

        final DataFetcher fetcher = DataFetcher.getInstance(this);
        String defaultUserName = "roee";
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
                    // check if camera permission is granted
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        openCamera();
                    }
            }
            return false;
        });
    }

    /**
     * Show a restaurant in google maps.
     * @param address restaurant address
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

    public void openCamera() {
        Log.e(TAG, "openCamera");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLogMealSuccess(String response) {
        // open camera fragment with the response
        Log.e(TAG, "onLogMealSuccess: " + response);
        cameraFragment = new CameraFragment(capturedImage, response);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cameraFragment).commit();
    }

    @Override
    public void onLogMealError(String message) {
        Log.e(TAG, "onLogMealError: " + message);
        toast(message);
    }

    public interface Callback {
        void onSuccess();
        void onError(String message);
    }
}