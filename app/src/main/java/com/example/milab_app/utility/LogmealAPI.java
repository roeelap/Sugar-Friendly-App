package com.example.milab_app.utility;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;


public class LogmealAPI {

    private static final String TAG = "LogmealAPI";
    private static final String LOGMEAL_API_URL = "https://api.logmeal.es/v2/image/segmentation/complete";
    private static final String LOGMEAL_API_KEY = "976f1efbe142ed81fb129cf9a680f17b32f81df9";

    public static void sendImage(File imageFile, final Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String encodedImage = convertImageToBase64(imageFile);

        // Build request body with image file
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(),
                        RequestBody.Companion.create(imageFile, MediaType.parse("image/jpeg")))
                .build();

        // Build HTTP request with API key and request body
        Request request = new Request.Builder()
                .url(LOGMEAL_API_URL)
                .addHeader("Authorization", "Bearer " + LOGMEAL_API_KEY)
                .post(requestBody)
                .build();

        Log.e(TAG, "Request body: " + imageFile);

        // Send HTTP request and handle response
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public static String convertImageToBase64(File imageFile) throws IOException {
        FileInputStream fis = new FileInputStream(imageFile);
        byte[] bytes = new byte[(int) imageFile.length()];
        fis.read(bytes);
        fis.close();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public interface Callback {
        void onSuccess(String responseBody);
        void onError(String message);
    }
}
