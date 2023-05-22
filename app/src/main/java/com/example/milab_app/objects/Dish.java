package com.example.milab_app.objects;

import org.json.JSONArray;

import java.util.Date;

public class Dish {
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
    protected boolean isLiked;

    public Dish(String name, String restaurantName, JSONArray foodTags, JSONArray nutritionTags,
                int likes, double rating, double sugarRating, String address, double distanceToUser, Date uploadDate) {
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
        this.isLiked = false;
    }

    public Dish(String name, String restaurantName) {
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
        this.isLiked = false;
    }

    /* getters */
    public String getName() { return name; }
    public String getRestaurantName() { return restaurantName; }
    public double getRating() { return rating; }
    public double getSugarRating() { return sugarRating; }
    public String getAddress() { return address; }
    public double getDistanceToUser() { return distanceToUser; }
    public boolean getIsLiked() { return isLiked; }

    public void setIsLiked(boolean isLiked) { this.isLiked = isLiked; }
}
