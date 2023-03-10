package com.example.milab_app.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataFetcher {
    private final RequestQueue _queue;

    private final String TAG = "DataFetcher";
    private final static String IP_ADDRESS = "10.0.0.38";
    private final static String DISHES_REQUEST_URL = "http://" + IP_ADDRESS + ":8080/dishes";
    private final static String RESTAURANTS_REQUEST_URL = "http://" + IP_ADDRESS + ":8080/restaurants";

    public interface DishesResponseListener {
        void onResponse(DataResponse.DishesResponse response);
    }

    public interface RestaurantsResponseListener {
        void onResponse(DataResponse.RestaurantResponse response);
    }

    public DataFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private DataResponse.DishesResponse createDishesErrorResponse() {
        return new DataResponse.DishesResponse(true, null, null, null);
    }

    private DataResponse.RestaurantResponse createRestaurantsErrorResponse() {
        return new DataResponse.RestaurantResponse(true, null);
    }

    public void fetchDishes(final DishesResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, DISHES_REQUEST_URL, null,
                response -> {
                    Log.d(TAG, "Got response: " + response.toString());
                    try {
                        JSONArray recommendedDishesResponse = response.getJSONArray("recommendedDishes");
                        JSONArray topRatedDishesResponse = response.getJSONArray("topRatedDishes");
                        JSONArray newestDishesResponse = response.getJSONArray("newestDishes");
                        ArrayList<Dish> recommendedDishes = parseDishes(recommendedDishesResponse);
                        ArrayList<Dish> topRatedDishes = parseDishes(topRatedDishesResponse);
                        ArrayList<Dish> newestDishes = parseDishes(newestDishesResponse);
                        DataResponse.DishesResponse res = new DataResponse.DishesResponse(false, recommendedDishes, topRatedDishes, newestDishes);
                        listener.onResponse(res);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Error while parsing response: " + e.getMessage());
                        listener.onResponse(createDishesErrorResponse());
                    }
                }, error -> {
                    Log.e(TAG, "Error while fetching user: " + error.getMessage());
                    listener.onResponse(createDishesErrorResponse());
                });

        _queue.add(req);
    }

    public void fetchRestaurants(final RestaurantsResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, RESTAURANTS_REQUEST_URL, null,
                response -> {
                    Log.d(TAG, "Got response: " + response.toString());
                    try {
                        JSONArray restaurantsResponse = response.getJSONArray("restaurants");
                        ArrayList<Restaurant> restaurants = parseRestaurants(restaurantsResponse);
                        DataResponse.RestaurantResponse res = new DataResponse.RestaurantResponse(false, restaurants);
                        listener.onResponse(res);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Error while parsing response: " + e.getMessage());
                        listener.onResponse(createRestaurantsErrorResponse());
                    }
                }, error -> {
                    Log.e(TAG, "Error while fetching user: " + error.getMessage());
                    listener.onResponse(createRestaurantsErrorResponse());
                });

        _queue.add(req);
    }

    private ArrayList<Dish> parseDishes(JSONArray dishesResponse) throws JSONException {
        ArrayList<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < dishesResponse.length(); i++) {
            JSONObject dish = dishesResponse.getJSONObject(i);
            String name = dish.getString("name");
            String restaurantName = dish.getString("restaurant");
            JSONArray foodTags = dish.getJSONArray("foodTags");
            JSONArray nutritionTags = dish.getJSONArray("nutritionTags");
            int likes = dish.getInt("likes");
            double rating = dish.getJSONObject("rating").getDouble("$numberDecimal");
            dishes.add(new Dish(name, restaurantName, foodTags, nutritionTags, likes, rating));
        }
        return dishes;
    }

    private ArrayList<Restaurant> parseRestaurants(JSONArray restaurantsResponse) throws JSONException {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < restaurantsResponse.length(); i++) {
            JSONObject restaurant = restaurantsResponse.getJSONObject(i);
            String name = restaurant.getString("name");
            String address = restaurant.getString("address");
            String phoneNumber = restaurant.getString("phoneNumber");
            String websiteURL = restaurant.getString("websiteURL");
            restaurants.add(new Restaurant(name, address, phoneNumber, websiteURL));
        }
        return restaurants;
    }
}
