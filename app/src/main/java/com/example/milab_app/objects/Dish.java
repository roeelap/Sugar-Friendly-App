package com.example.milab_app.objects;

import org.json.JSONArray;

public class Dish {
    protected String name;
    protected String restaurantName;
    protected JSONArray foodTags;
    protected JSONArray nutritionTags;
    protected int likes;
    protected double rating;

    public Dish(String name, String restaurantName, JSONArray foodTags, JSONArray nutritionTags,
                int likes, double rating) {
        this.name = name;
        this.restaurantName = restaurantName;
        this.foodTags = foodTags;
        this.nutritionTags = nutritionTags;
        this.likes = likes;
        this.rating = rating;
    }

    public Dish(String name, String restaurantName) {
        this.name = name;
        this.restaurantName = restaurantName;
        this.foodTags = new JSONArray();
        this.nutritionTags = new JSONArray();
        this.likes = 0;
        this.rating = 0.0;
    }

    /* getters */
    public String getName() { return name; }
    public String getRestaurantName() { return restaurantName; }
    public JSONArray getFoodTags() { return foodTags; }
    public JSONArray getNutritionTags() { return nutritionTags; }
    public int getLikes() { return likes; }
    public double getRating() { return rating; }
}
