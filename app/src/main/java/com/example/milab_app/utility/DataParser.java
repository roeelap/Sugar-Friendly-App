package com.example.milab_app.utility;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.milab_app.objects.Dish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataParser {

    private final String TAG = "DataParser";

    public ArrayList<Dish> parseDishes(JSONArray dishesResponse) throws JSONException {
        ArrayList<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < dishesResponse.length(); i++) {
            JSONObject dish = dishesResponse.getJSONObject(i);
            Log.e(TAG, "Dish: " + dish.toString());
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
            dishes.add(new Dish(name, restaurantName, foodTags, nutritionTags, likes, rating,
                    sugarRating, address, distanceToUser, uploadDate));
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
}