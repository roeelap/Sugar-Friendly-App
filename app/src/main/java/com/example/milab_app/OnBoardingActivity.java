package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;

public class OnBoardingActivity extends AppCompatActivity {

    private static final String TAG = "OnBoardingActivity";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        boolean locationPermissionGranted = getIntent().getBooleanExtra("locationPermissionGranted", false);
        LatLng currentDeviceLocation = getIntent().getParcelableExtra("currentDeviceLocation");

        TextView sugarLevelTextView = findViewById(R.id.sugar_level);

        Slider sugarLevelSlider = findViewById(R.id.sugar_level_slider);
        sugarLevelSlider.addOnChangeListener((slider, value, fromUser) -> {
            Log.d(TAG, "updateRecyclerViewsToSugarLevel - " + value);
            // update recycler views to show dishes with sugarRating >= sugarLevel
            if (value == slider.getValueTo()) {
                sugarLevelTextView.setText("HI");
            } else if (value == slider.getValueFrom()) {
                sugarLevelTextView.setText("I");
            } else {
                sugarLevelTextView.setText(String.valueOf((int) value));
            }
        });

        Button letsStartButton = findViewById(R.id.lets_find_you_a_dish);
        letsStartButton.setOnClickListener(v -> {
            // pass sugar level to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment", "home");
            intent.putExtra("sugarLevel", sugarLevelSlider.getValue());
            intent.putExtra("locationPermissionGranted", locationPermissionGranted);
            intent.putExtra("currentDeviceLocation", currentDeviceLocation);
            startActivity(intent);
            finish();
        });

        Button shareNewDishButton = findViewById(R.id.lets_share_a_new_dish);
        shareNewDishButton.setOnClickListener(v -> {
            // pass sugar level to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment", "camera");
            intent.putExtra("sugarLevel", sugarLevelSlider.getValue());
            intent.putExtra("locationPermissionGranted", locationPermissionGranted);
            intent.putExtra("currentDeviceLocation", currentDeviceLocation);
            startActivity(intent);
            finish();
        });
    }
}