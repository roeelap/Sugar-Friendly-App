package com.example.milab_app.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;

/* a singleton class that represents the current user using the app */
public class User {

    private static User instance = null;
    private final String name;
    private final String userName;
    private final HashSet<String> favDishes;
    private final LatLng location;

    private User(String name, String username, HashSet<String> favDishes, LatLng location) {
        this.name = name;
        this.userName = username;
        this.favDishes = favDishes;
        this.location = location;
    }

    public static User getInstance(String name, String username, HashSet<String> fav_dishes, LatLng location) {
        if (instance == null) {
            instance = new User(name, username, fav_dishes, location);
        }
        return instance;
    }

    /* getters */
    public String getName() { return name; }
    public String getUserName() { return userName; }
    public HashSet<String> getFavDishes() { return favDishes; }
    public LatLng getLocation() { return location; }

    public void toggleDishLikability(String dishId) {
        if (!favDishes.contains(dishId)) {
            favDishes.add(dishId);
        } else {
            favDishes.remove(dishId);
        }
    }
}
