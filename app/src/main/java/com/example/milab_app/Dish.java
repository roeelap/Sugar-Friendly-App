package com.example.milab_app;

public class Dish {
    private final String name;
    private final Restaurant restaurant;
    private final String[] foodTags;
    private final String[] nutritionTags;
    private final int likes;
    private final float rating;

    public Dish(String name, Restaurant restaurant, String[] foodTags, String[] nutritionTags,
                int likes, float rating) {
        this.name = name;
        this.restaurant = restaurant;
        this.foodTags = foodTags;
        this.nutritionTags = nutritionTags;
        this.likes = likes;
        this.rating = rating;
    }

    public Dish(String name, Restaurant restaurant) {
        this.name = name;
        this.restaurant = restaurant;
        this.foodTags = new String[0];
        this.nutritionTags = new String[0];
        this.likes = 0;
        this.rating = 0;
    }

    /* getters */
    public String getName() { return name; }
    public Restaurant getRestaurant() { return restaurant; }
    public String[] getFoodTags() { return foodTags; }
    public String[] getNutritionTags() { return nutritionTags; }
    public int getLikes() { return likes; }
    public float getRating() { return rating; }
}
