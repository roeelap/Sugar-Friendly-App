package com.example.milab_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.milab_app.R;
import com.example.milab_app.utility.LogmealAPI;

import org.json.JSONArray;
import org.json.JSONObject;


public class CameraFragment extends Fragment implements LogmealAPI.Callback {

    private static final String TAG = "CameraFragment";

    private ActivityResultLauncher<Intent> cameraLauncher;
    private Bitmap capturedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

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
                            LogmealAPI.sendImage(capturedImage, this);
                        }
                    }
                });

        Button uploadImageButton = rootView.findViewById(R.id.upload_image_button);
        uploadImageButton.setOnClickListener(v -> {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    cameraLauncher.launch(takePictureIntent);
                }
        });

        return rootView;
    }

    public void updateUI(String response) {
        Log.e(TAG, "updateUI");
        try {
            ImageView capturedImageView = requireView().findViewById(R.id.captured_image);
            capturedImageView.setImageBitmap(capturedImage);

            StringBuilder sb = new StringBuilder();
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray foodFamilyArray = jsonResponse.getJSONArray("foodFamily");
            for (int i = 0; i < foodFamilyArray.length(); i++) {
                JSONObject foodFamily = foodFamilyArray.getJSONObject(i);
                String name = foodFamily.getString("name");
                Double prob = foodFamily.getDouble("prob");
                Log.e(TAG, "foodFamilyName: " + name);
                sb.append(name).append(": ").append(prob).append("\n");
            }

            sb.append("\n");

            JSONArray SegmentationResults = jsonResponse.getJSONArray("segmentation_results");
            for (int i = 0; i < SegmentationResults.length(); i++) {
                JSONObject segmentationResult = SegmentationResults.getJSONObject(i);
                JSONArray recognitionResults = segmentationResult.getJSONArray("recognition_results");
                for (int j = 0; j < recognitionResults.length(); j++) {
                    JSONObject recognitionResult = recognitionResults.getJSONObject(j);
                    String name = recognitionResult.getString("name");
                    Double prob = recognitionResult.getDouble("prob");
                    Log.e(TAG, "recognitionResultName: " + name);
                    sb.append(name).append(": ").append(prob).append("\n");
                }
            }


            TextView resultsTextView = requireView().findViewById(R.id.results_text_view);
            resultsTextView.setText(sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    @Override
    public void onSuccess(String response) {
        Log.e(TAG, "Success:\n" + response);
        requireActivity().runOnUiThread(() -> updateUI(response));
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error:\n" + message);
    }
}