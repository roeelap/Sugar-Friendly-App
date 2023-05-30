package com.example.milab_app.utility;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;


public class LogmealAPI {

    private static final String TAG = "LogmealAPI";
    private static final String SEGMENTATION_URL = "https://api.logmeal.es/v2/image/segmentation/complete";
    private static final String NUTRITIONAL_INFO_URL = "https://api.logmeal.es/v2/recipe/nutritionalInfo";
    private static final String LOGMEAL_API_KEY = "976f1efbe142ed81fb129cf9a680f17b32f81df9";

    OkHttpClient client;

    public LogmealAPI() {
        client = new OkHttpClient();
    }

    public void sendImage(Bitmap image, final LogmealAPI.LogMealCallback callback) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                .build();

        // Create the API request
        Request request = new Request.Builder()
                .url(SEGMENTATION_URL)
                .addHeader("Authorization", "Bearer " + LOGMEAL_API_KEY)
                .post(requestBody)
                .build();

        // Make the API call
        Log.e(TAG, "Sent image");
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onLogMealError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONObject responseJson = new JSONObject(responseString);
                        int imageId = responseJson.getInt("imageId");
                        Log.e(TAG, "Image ID: " + imageId);
                        getNutritionalInfo(imageId, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onLogMealError(e.getMessage());
                    }
                } else {
                    // Handle non-successful response
                    callback.onLogMealError(response.message());
                }
            }
        });
    }

    private void getNutritionalInfo(int imageId, final LogmealAPI.LogMealCallback callback) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("imageId", imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(requestBodyJson.toString(),
                MediaType.parse("application/json"));

        // Create the API request
        Request request = new Request.Builder()
                .url(NUTRITIONAL_INFO_URL)
                .addHeader("Authorization", "Bearer " + LOGMEAL_API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle API call failure
                e.printStackTrace();
                callback.onLogMealError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "Got response from nutritional info");
                // Handle API response
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    // parse the response string
                    LogmealResponse logmealResponse = new LogmealResponse(responseString);
                    // responseData contains the data received from the Logmeal API
                    callback.onLogMealSuccess(logmealResponse);
                } else {
                    // Handle non-successful response
                    callback.onLogMealError(response.message());
                }
            }
        });
    }


    public static class LogmealResponse {
        public HashSet<String> detectedDishes;
        public TreeMap<String, String> nutritionalValues;
        public String sugarLevel;

        @SuppressLint("DefaultLocale")
        public LogmealResponse(String response) {
            detectedDishes = new HashSet<>();
            nutritionalValues = new TreeMap<>();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                // if there is only one dish, then foodName is a string, else it is an array
                Object foodName = jsonResponse.get("foodName");
                if (foodName instanceof String) {
                    detectedDishes = new HashSet<>();
                    detectedDishes.add((String) foodName);
                } else if (foodName instanceof JSONArray) {
                    detectedDishes = new HashSet<>();
                    JSONArray foodNameArray = (JSONArray) foodName;
                    for (int i = 0; i < foodNameArray.length(); i++) {
                        detectedDishes.add(foodNameArray.getString(i));
                    }
                }

                // check if there exists nutritional info
                if (!jsonResponse.getBoolean("hasNutritionalInfo")) {
                    return;
                }

                JSONObject nutritionalInfo = jsonResponse.getJSONObject("nutritional_info");

                JSONObject dailyIntakeReference = nutritionalInfo.getJSONObject("dailyIntakeReference");
                JSONObject sugarDailyIntakeReference = dailyIntakeReference.getJSONObject("SUGAR");
                sugarLevel = sugarDailyIntakeReference.getString("level");

                nutritionalValues.put("Calories", String.format("%.2f", nutritionalInfo.getDouble("calories"))); // calories

                JSONObject totalNutrients = nutritionalInfo.getJSONObject("totalNutrients");

                parseValue(totalNutrients.getJSONObject("CHOCDF")); // carbs
                parseValue(totalNutrients.getJSONObject("FAT")); // fat
                parseValue(totalNutrients.getJSONObject("PROCNT")); // protein
                parseValue(totalNutrients.getJSONObject("SUGAR")); // sugar


            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        @SuppressLint("DefaultLocale")
        private void parseValue(JSONObject value) throws Exception {
            double quantity = value.getDouble("quantity");
            String quantityString = String.format("%.2f", quantity) + value.getString("unit");
            nutritionalValues.put(value.getString("label"), quantityString);
        }
    }



    public interface LogMealCallback {
        void onLogMealSuccess(LogmealResponse logmealResponse);
        void onLogMealError(String message);
    }
}
