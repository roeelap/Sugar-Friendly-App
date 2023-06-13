package com.example.milab_app.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.R;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.LogmealAPI;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
        this.capturedImage = capturedImage;
        this.logmealResponse = response;
        Log.e(TAG, "logmealResponse: " + logmealResponse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        initTagsInput(rootView);

        // hide progress bar if it was visible
        ((MainActivity) requireActivity()).hideProgressBar();

        if (logmealResponse != null && capturedImage != null) {
            updateUI(getActivity(), rootView);
        }

        Button uploadButton = rootView.findViewById(R.id.share_button);
        uploadButton.setOnClickListener(v -> {
            String dishName = Objects.requireNonNull(((TextInputEditText) rootView.findViewById(R.id.dish_name_textInput)).getText()).toString().trim();
            String restaurantName = Objects.requireNonNull(((TextInputEditText) rootView.findViewById(R.id.restaurant_name_textInput)).getText()).toString().trim();
            if (TextUtils.isEmpty(dishName)) {
                ((TextInputEditText) rootView.findViewById(R.id.dish_name_textInput)).setError("Dish name cannot be empty");
                return;
            }
            if (TextUtils.isEmpty(restaurantName)) {
                ((TextInputEditText) rootView.findViewById(R.id.restaurant_name_textInput)).setError("Restaurant name cannot be empty");
                return;
            }

            ArrayList<String> tags = getTags();
            double sugarRating = Objects.equals(logmealResponse.sugarLevel, "High") ? 4.0 :
                    Objects.equals(logmealResponse.sugarLevel, "Medium") ? 3.0 : 2.0;
            uploadDish(dishName, restaurantName, tags, sugarRating);
        });

        return rootView;
    }

    private void initTagsInput(View rootView) {
        tagsInputEditText = rootView.findViewById(R.id.tags_textInputEditText);
        TextInputLayout tagsInputLayout = rootView.findViewById(R.id.tags_textInputLayout);
        tagsChipGroup = rootView.findViewById(R.id.chipGroup_flex_box);
        tagsInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = Objects.requireNonNull(tagsInputEditText.getText()).toString().trim();
                addChip(tagsChipGroup, text, true);
                tagsInputEditText.setText("");
                return true;
            }
            return false;
        });

        tagsInputLayout.setEndIconOnClickListener(v -> {
            String text = Objects.requireNonNull(tagsInputEditText.getText()).toString().trim();
            addChip(tagsChipGroup, text, true);
            tagsInputEditText.setText("");
        });
    }

    private void addChip(ChipGroup chipGroup, String text, boolean isCloseable) {
        if (!TextUtils.isEmpty(text)) {
            ChipDrawable chipDrawable;
            if (isCloseable) {
                chipDrawable = ChipDrawable.createFromResource(requireContext(), R.xml.chip);
            } else {
                chipDrawable = ChipDrawable.createFromResource(requireContext(), R.xml.chip_no_exit);
            }
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
            addChip(predictedFoodItemsChipGroup, foodItem, false);
        }

        // update nutritional values table
        TableLayout nutritionalValuesTable = rootView.findViewById(R.id.nutritional_values_table);
        for (String key : logmealResponse.nutritionalValues.keySet()) {
            Log.e(TAG, "key: " + key + ", value: " + logmealResponse.nutritionalValues.get(key));
            addRowToTable(activity, nutritionalValuesTable, key, logmealResponse.nutritionalValues.get(key));
        }

        // update sugar level tag
        String sugarLevel = logmealResponse.sugarLevel;
        TextView sugarLevelTextView = rootView.findViewById(R.id.sugarLevel);
        sugarLevelTextView.setPadding(30, 0, 30, 0);
        sugarLevelTextView.setText(sugarLevel);
        if (sugarLevel.equals("High")) {
            sugarLevelTextView.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_yellow));
        } else if (sugarLevel.equals("Medium")) {
            sugarLevelTextView.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_light_green));
        } else {
            sugarLevelTextView.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_dark_green));
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

    private void uploadDish(String dishName, String restaurantName, ArrayList<String> tags,
                            double sugarRating) {
        Log.e(TAG, "uploadDish");

        // show progress bar
        ((MainActivity) requireActivity()).showProgressBar();

        JSONObject dishJSON = null;
        try {
            dishJSON = new JSONObject();
            dishJSON.put("name", dishName);
            dishJSON.put("restaurant", restaurantName);
            dishJSON.put("tags", new JSONArray(tags));
            dishJSON.put("sugarRating", sugarRating);
            dishJSON.put("nutritionalValues", new JSONObject(logmealResponse.nutritionalValues));
            dishJSON.put("dishImage", encodeImage(capturedImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataFetcher dataFetcher = DataFetcher.getInstance(requireContext());
        dataFetcher.uploadDish(dishJSON,
                response -> {
                    Log.e(TAG, "uploadDish: " + response);
                    // hide progress bar
                    ((MainActivity) requireActivity()).hideProgressBar();
                    if (response.isError()) {
                        ((MainActivity) requireActivity()).toast("Failed to upload dish!");
                        return;
                    }
                    ((MainActivity) requireActivity()).toast("Dish uploaded successfully!");
                    // go to home fragment
                    ((MainActivity) requireActivity()).showFragment("home");
                });
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}