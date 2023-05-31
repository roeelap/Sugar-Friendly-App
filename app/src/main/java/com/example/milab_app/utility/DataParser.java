package com.example.milab_app.utility;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class DataParser {

    private final String TAG = "DataParser";

    public ArrayList<Dish> parseDishes(JSONArray dishesResponse) throws JSONException {
        ArrayList<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < dishesResponse.length(); i++) {
            JSONObject dish = dishesResponse.getJSONObject(i);
            Log.e(TAG, "Dish: " + dish.toString());
            String id = dish.getString("_id");
            String name = dish.getString("name");
            String restaurantName = dish.getString("restaurant");
            JSONArray foodTags = dish.getJSONArray("foodTags");
            JSONArray nutritionTags = dish.getJSONArray("nutritionTags");
            int likes = dish.getInt("likes");
            double rating = dish.getDouble("rating");
            double sugarRating = dish.getDouble("sugarRating");
            String address = dish.getString("address");
            double distanceToUser = dish.getDouble("distanceToUser");
            Date uploadDate = parseDate(dish.getString("uploadDate"));
            Bitmap image = null;
            if (dish.has("dishImage")) {
                image = decodeImage(dish.getString("dishImage"));
            }
            JSONObject nutritionalValues = null;
            if (dish.has("nutritionalValues")) {
                nutritionalValues = dish.getJSONObject("nutritionalValues");
            }
            Log.e(TAG, "Nutritional values: " + nutritionalValues);
            dishes.add(new Dish(id, name, restaurantName, foodTags, nutritionTags, likes, rating,
                    sugarRating, address, distanceToUser, uploadDate, nutritionalValues, image));
        }
        return dishes;
    }

    @SuppressLint("SimpleDateFormat")
    public Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing date: " + e.getMessage());
            return null;
        }
    }

    public User parseUser(JSONObject userResponse) throws JSONException {
        String name = userResponse.getString("name");
        String userName = userResponse.getString("userName");
        JSONArray favDishes = userResponse.getJSONArray("favoriteDishes");
        HashSet<String> favDishesSet = new HashSet<>();
        for (int i = 0; i < favDishes.length(); i++) {
            favDishesSet.add(favDishes.getString(i));
        }
        return User.getInstance(name, userName, favDishesSet, null);
    }

    public Bitmap decodeImage(String encodedImage) {
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
