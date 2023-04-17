package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class OnBoardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        boolean locationPermissionGranted = getIntent().getBooleanExtra("locationPermissionGranted", false);
        LatLng currentDeviceLocation = getIntent().getParcelableExtra("currentDeviceLocation");

        TextView sugarLevel = findViewById(R.id.sugar_level);

        // update sugar level text dynamically according to the seekbar
        SeekBar sugarLevelSeekBar = findViewById(R.id.sugar_level_seekBar);
        sugarLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sugarLevel.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button letsStartButton = findViewById(R.id.lets_start);
        letsStartButton.setOnClickListener(v -> {
            // pass sugar level to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("sugarLevel", sugarLevelSeekBar.getProgress());
            intent.putExtra("locationPermissionGranted", locationPermissionGranted);
            intent.putExtra("currentDeviceLocation", currentDeviceLocation);
            startActivity(intent);
            finish();
        });
    }
}