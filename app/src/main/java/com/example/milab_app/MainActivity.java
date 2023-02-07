package com.example.milab_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    MapFragment mapFragment = new MapFragment();
    AddFragment addFragment = new AddFragment();
    CameraFragment cameraFragment = new CameraFragment();
    ProfileFragment profileFragment = new ProfileFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // load home fragment when app starts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                        return true;
                    case R.id.navigation_map:
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
            }
        });
    }
}