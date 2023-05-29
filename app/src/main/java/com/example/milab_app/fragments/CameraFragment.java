package com.example.milab_app.fragments;

import android.app.Activity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.milab_app.R;
import com.example.milab_app.utility.LogmealAPI;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;


public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private LogmealAPI.LogmealResponse logmealResponse;
    private Bitmap capturedImage;

    private TextInputEditText tagsInputEditText;
    private ChipGroup tagsChipGroup;

    public CameraFragment() {
        // Required empty public constructor
    }

    public CameraFragment(Bitmap capturedImage, LogmealAPI.LogmealResponse response) {
        // Required empty public constructor
        Log.e(TAG, "logmealResponse: " + logmealResponse);
        this.capturedImage = capturedImage;
        this.logmealResponse = response;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        initTagsInput(rootView);

        if (logmealResponse != null && capturedImage != null) {
            //updateUI(rootView);
            updateUI(getActivity(), rootView);
        }

        return rootView;
    }

    private void initTagsInput(View rootView) {
        tagsInputEditText = rootView.findViewById(R.id.tags_textInputEditText);
        TextInputLayout tagsInputLayout = rootView.findViewById(R.id.tags_textInputLayout);
        tagsChipGroup = rootView.findViewById(R.id.chipGroup_flex_box);
        tagsInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = Objects.requireNonNull(tagsInputEditText.getText()).toString().trim();
                addChip(tagsChipGroup, text);
                tagsInputEditText.setText("");
                return true;
            }
            return false;
        });

        tagsInputLayout.setEndIconOnClickListener(v -> {
            String text = Objects.requireNonNull(tagsInputEditText.getText()).toString().trim();
            addChip(tagsChipGroup, text);
            tagsInputEditText.setText("");
        });
    }

    private void addChip(ChipGroup chipGroup, String text) {
        if (!TextUtils.isEmpty(text)) {
            ChipDrawable chipDrawable = ChipDrawable.createFromResource(requireContext(), R.xml.chip);
            Chip chip = new Chip(requireContext());
            chip.setChipDrawable(chipDrawable);
            chip.setText(text);
            chip.setOnCloseIconClickListener(chipGroup::removeView);
            chip.setCheckable(false);
            chipGroup.addView(chip);
        }
    }

    private void addRowToTable(Activity a, TableLayout table, String key, String value) {
        TableRow row = new TableRow(a);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams();
        rowParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        rowParams.width = TableRow.LayoutParams.MATCH_PARENT;
        rowParams.setMargins(0, 10, 0, 10);

        TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
        // wrap-up content of the row
        columnParams.height = TableRow.LayoutParams.WRAP_CONTENT;
        columnParams.width = TableRow.LayoutParams.WRAP_CONTENT;

        TextView keyTextView = new TextView(a);
        TextView valueTextView = new TextView(a);
        keyTextView.setText(key);
        keyTextView.setTextSize(18);
        valueTextView.setText(value);
        valueTextView.setTextSize(18);

        row.addView(keyTextView, columnParams);
        row.addView(valueTextView, columnParams);
        table.addView(row, rowParams);
    }

    public void updateUI(Activity activity, View rootView) {
        Log.e(TAG, "updateUI");
        // update image
        ImageView capturedImageView = rootView.findViewById(R.id.captured_image);
        capturedImageView.setImageBitmap(capturedImage);

        // update predicted food items
        ChipGroup predictedFoodItemsChipGroup = rootView.findViewById(R.id.chipGroup_predicted_food_items);
        for (String foodItem : logmealResponse.detectedDishes) {
            addChip(predictedFoodItemsChipGroup, foodItem);
        }

        // update nutritional values table
        TableLayout nutritionalValuesTable = rootView.findViewById(R.id.nutritional_values_table);
        for (String key : logmealResponse.nutritionalValues.keySet()) {
            Log.e(TAG, "key: " + key + ", value: " + logmealResponse.nutritionalValues.get(key));
            addRowToTable(activity, nutritionalValuesTable, key, logmealResponse.nutritionalValues.get(key));
        }
    }

    private ArrayList<String> getTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < tagsChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) tagsChipGroup.getChildAt(i);
            tags.add(chip.getText().toString());
        }
        return tags;
    }

    private void uploadDish() {
        Log.e(TAG, "uploadDish");
        ArrayList<String> tags = getTags();
        Log.e(TAG, "tags: " + tags);
    }
}