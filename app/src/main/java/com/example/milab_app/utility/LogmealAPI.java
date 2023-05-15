package com.example.milab_app.utility;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class LogmealAPI {

    private static final String TAG = "LogmealAPI";
    private static final String LOGMEAL_API_URL = "https://api.logmeal.es/v2/image/segmentation/complete";
    private static final String LOGMEAL_API_KEY = "976f1efbe142ed81fb129cf9a680f17b32f81df9";

    public static void sendImage(Bitmap image, final LogmealAPI.Callback callback) {
        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Create OkHttp client and request body
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                .build();

        // Create the API request
        Request request = new Request.Builder()
                .url(LOGMEAL_API_URL)
                .addHeader("Authorization", "Bearer " + LOGMEAL_API_KEY)
                .post(requestBody)
                .build();

        // Make the API call asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle API call failure
                e.printStackTrace();
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle API response
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    // Process the response data as needed
                    // responseData contains the data received from the Logmeal API
                    callback.onSuccess(responseString);
                } else {
                    // Handle non-successful response
                    callback.onError(response.message());
                }
            }
        });
        Log.e(TAG, "Sent image");
    }

    public interface Callback {
        void onSuccess(String response);
        void onError(String message);
    }
}
