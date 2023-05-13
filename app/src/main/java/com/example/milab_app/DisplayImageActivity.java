package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.milab_app.utility.DataFetcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DisplayImageActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // displaying image
        imageView = findViewById(R.id.display_image_view);
        String photoPath = getIntent().getStringExtra("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        bitmap = rotateImageIfNeeded(photoPath, bitmap);
        imageView.setImageBitmap(bitmap);

        // uploading image to server
        String imageString = imageToString(bitmap);
        uploadImage(imageString);
    }

    private static Bitmap rotateImageIfNeeded(String photoPath, Bitmap img) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void uploadImage(String imageString) {
        String UPLOAD_URL = "https://handsome-teal-sheath-dress.cyclic.app/upload";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, UPLOAD_URL,
                response -> {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    String result = jsonObject.optString("result");
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(0);
                },
                error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show())
        {
            @Override
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", imageString);
                return params;
            }
        };

        DataFetcher.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
    }


}