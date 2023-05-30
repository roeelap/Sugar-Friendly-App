package com.example.milab_app.objects;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.TreeMap;

public class Dish {

    protected String id;
    protected String name;
    protected String restaurantName;
    protected JSONArray foodTags;
    protected JSONArray nutritionTags;
    protected int likes;
    protected double rating;
    protected double sugarRating;
    protected String address;
    protected double distanceToUser;
    protected Date uploadDate;
    protected TreeMap<String, String> nutritionalValues;
    protected Bitmap image;

    public Dish(String id, String name, String restaurantName, JSONArray foodTags, JSONArray nutritionTags,
                int likes, double rating, double sugarRating, String address, double distanceToUser,
                Date uploadDate, JSONObject nutritionalValues, Bitmap image) {
        this.id = id;
        this.name = name;
        this.restaurantName = restaurantName;
        this.foodTags = foodTags;
        this.nutritionTags = nutritionTags;
        this.likes = likes;
        this.rating = rating;
        this.sugarRating = sugarRating;
        this.address = address;
        this.distanceToUser = distanceToUser;
        this.uploadDate = uploadDate;
        this.nutritionalValues = new TreeMap<>();
        this.image = image;
    }

    public Dish(String name, String restaurantName) {
        this.id = null;
        this.name = name;
        this.restaurantName = restaurantName;
        this.foodTags = new JSONArray();
        this.nutritionTags = new JSONArray();
        this.likes = 0;
        this.rating = 0.0;
        this.sugarRating = 0.0;
        this.address = null;
        this.distanceToUser = 0.0;
        this.uploadDate = new Date();
    }

    /* getters */
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRestaurantName() { return restaurantName; }
    public double getRating() { return rating; }
    public double getSugarRating() { return sugarRating; }
    public String getAddress() { return address; }
    public double getDistanceToUser() { return distanceToUser; }
    public Bitmap getImage() { return image; }
    public TreeMap<String, String> getNutritionalValues() { return nutritionalValues; }

}
