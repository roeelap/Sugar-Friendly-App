package com.example.milab_app;

public class Restaurant {
    private final String name;
    private final String location;
    private final String phoneNumber;
    private final String websiteURL;
    private final Dish[] dishes;
    private final int likes;
    private final float rating;
    private final String[] foodTags;
    private final String[] nutritionTags;

    public Restaurant(String name, String location, String phoneNumber, String websiteURL,
                      Dish[] dishes, int likes, float rating, String[] foodTags, String[] nutritionTags) {
        this.name = name;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.dishes = dishes;
        this.likes = likes;
        this.rating = rating;
        this.foodTags = foodTags;
        this.nutritionTags = nutritionTags;
    }

    public Restaurant(String name) {
        this.name = name;
        this.location = "";
        this.phoneNumber = "";
        this.websiteURL = "";
        this.dishes = new Dish[0];
        this.likes = 0;
        this.rating = 0;
        this.foodTags = new String[0];
        this.nutritionTags = new String[0];
    }

    /* getters */
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getWebsiteURL() { return websiteURL; }
    public Dish[] getDishes() { return dishes; }
    public int getLikes() { return likes; }
    public float getRating() { return rating; }
    public String[] getFoodTags() { return foodTags; }
    public String[] getNutritionTags() { return nutritionTags; }
}
