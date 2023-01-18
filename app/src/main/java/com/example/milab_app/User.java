package com.example.milab_app;

import org.json.JSONArray;

/* a singleton class that represents the current user using the app */
class User {

    private static User instance = null;
    private final String username;
    private final JSONArray favDishes;
    private final JSONArray favRestaurants;
    private final JSONArray recentFoodTags;
    private final JSONArray recentNutritionTags;

    private User(String username, JSONArray favDishes, JSONArray fav_restaurants, JSONArray recent_food_tags, JSONArray recent_nutrition_tags) {
        this.username = username;
        this.favDishes = favDishes;
        this.favRestaurants = fav_restaurants;
        this.recentFoodTags = recent_food_tags;
        this.recentNutritionTags = recent_nutrition_tags;
    }

    public static User getInstance(String username, JSONArray fav_dishes, JSONArray fav_restaurants,
                                   JSONArray recent_food_tags, JSONArray recent_nutrition_tags) {
        if (instance == null) {
            instance = new User(username, fav_dishes, fav_restaurants,
                    recent_food_tags, recent_nutrition_tags);
        }
        return instance;
    }

    /* getters */
    public String getUsername() { return username; }
    public JSONArray getFavDishes() { return favDishes; }
    public JSONArray getFavRestaurants() { return favRestaurants; }
    public JSONArray getRecentFoodTags() { return recentFoodTags; }
    public JSONArray getRecentNutritionTags() { return recentNutritionTags; }


}
