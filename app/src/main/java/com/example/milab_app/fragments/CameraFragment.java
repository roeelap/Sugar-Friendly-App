package com.example.milab_app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.example.milab_app.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;


public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private String logmealResponse;
    private Bitmap capturedImage;

    private TextInputEditText tagsInputEditText;
    private TextInputLayout tagsInputLayout;
    private ChipGroup tagsChipGroup;

    public CameraFragment() {
        // Required empty public constructor
    }

    public CameraFragment(Bitmap capturedImage, String logmealResponse) {
        // Required empty public constructor
        Log.e(TAG, "logmealResponse: " + logmealResponse);
        this.capturedImage = capturedImage;
        this.logmealResponse = logmealResponse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        initTagsInput(rootView);

        if (logmealResponse != null && capturedImage != null) {
            //updateUI(rootView);
            updateUITest(rootView);
        }

        return rootView;
    }

    private void initTagsInput(View rootView) {
        tagsInputEditText = rootView.findViewById(R.id.tags_textInputEditText);
        tagsInputLayout = rootView.findViewById(R.id.tags_textInputLayout);
        tagsChipGroup = rootView.findViewById(R.id.chipGroup_flex_box);
        tagsInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addChip();
                return true;
            }
            return false;
        });

        tagsInputLayout.setEndIconOnClickListener(v -> addChip());
    }

    private void addChip() {
        String text = Objects.requireNonNull(tagsInputEditText.getText()).toString().trim();
        if (!TextUtils.isEmpty(text)) {
            ChipDrawable chipDrawable = ChipDrawable.createFromResource(requireContext(), R.xml.chip);
            Chip chip = new Chip(requireContext());
            chip.setChipDrawable(chipDrawable);
            chip.setText(text);
            chip.setOnCloseIconClickListener(v -> tagsChipGroup.removeView(v));
            chip.setCheckable(false);
            tagsChipGroup.addView(chip);
            tagsInputEditText.setText("");
        }
    }

    private void updateUITest(View rootView) {
        ImageView capturedImageView = rootView.findViewById(R.id.captured_image);
        capturedImageView.setImageBitmap(capturedImage);

    }

    public void updateUI(View rootView) {
        Log.e(TAG, "updateUI");
        try {
            ImageView capturedImageView = rootView.findViewById(R.id.captured_image);
            capturedImageView.setImageBitmap(capturedImage);

            StringBuilder sb = new StringBuilder();
            JSONObject jsonResponse = new JSONObject(logmealResponse);
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

//            TextView resultsTextView = rootView.findViewById(R.id.results_text_view);
//            resultsTextView.setText(sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}