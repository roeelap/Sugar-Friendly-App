package com.example.milab_app.objects;

import org.json.JSONArray;

/* a singleton class that represents the current user using the app */
public class User {

    private static User instance = null;
    private final String name;
    private final String username;
    private final JSONArray favDishes;
    private final JSONArray recentFoodTags;
    private final JSONArray recentNutritionTags;

    private User(String name, String username, JSONArray favDishes, JSONArray recent_food_tags, JSONArray recent_nutrition_tags) {
        this.name = name;
        this.username = username;
        this.favDishes = favDishes;
        this.recentFoodTags = recent_food_tags;
        this.recentNutritionTags = recent_nutrition_tags;
    }

    public static User getInstance(String name, String username, JSONArray fav_dishes,
                                   JSONArray recent_food_tags, JSONArray recent_nutrition_tags) {
        if (instance == null) {
            instance = new User(name, username, fav_dishes,
                    recent_food_tags, recent_nutrition_tags);
        }
        return instance;
    }

    /* getters */
    public String getName() { return name; }
    public String getUsername() { return username; }
    public JSONArray getFavDishes() { return favDishes; }
    public JSONArray getRecentFoodTags() { return recentFoodTags; }
    public JSONArray getRecentNutritionTags() { return recentNutritionTags; }
}
