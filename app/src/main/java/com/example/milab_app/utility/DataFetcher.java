package com.example.milab_app.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.objects.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataFetcher {
    private final RequestQueue _queue;

    private final String TAG = "DataFetcher";

    private final static String URL = "https://handsome-teal-sheath-dress.cyclic.app";
    private final static String PING_REQUEST_URL = URL + "/isup";
    private final static String DISHES_REQUEST_URL = URL + "/dishes";
    private final static String RESTAURANTS_REQUEST_URL = URL + "/restaurants";
    private final static String SEARCH_REQUEST_URL = URL + "/search";

    public interface DataResponseListener {
        void onResponse(DataResponse response);
    }

    public interface DishesResponseListener {
        void onResponse(DataResponse.DishesResponse response);
    }

    public interface RestaurantsResponseListener {
        void onResponse(DataResponse.RestaurantResponse response);
    }

    public interface SearchResponseListener {
        void onResponse(DataResponse.SearchResponse response);
    }

    public DataFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }


    private DataResponse createDataErrorResponse() {
        return new DataResponse(true);
    }

    private DataResponse.DishesResponse createDishesErrorResponse() {
        return new DataResponse.DishesResponse(true, null, null, null);
    }

    private DataResponse.RestaurantResponse createRestaurantsErrorResponse() {
        return new DataResponse.RestaurantResponse(true, null);
    }

    private DataResponse.SearchResponse createSearchErrorResponse() {
        return new DataResponse.SearchResponse(true, null);
    }


    public void pingServer(final DataResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, PING_REQUEST_URL, null,
                response -> {
                    Log.d(TAG, "Got response: " + response.toString());
                    try {
                        boolean success = response.has("success") && response.getBoolean("success");
                        listener.onResponse(new DataResponse(success));
                    } catch (JSONException e) {
                        listener.onResponse(createDataErrorResponse());
                    }
                }, error -> {
                    Log.e(TAG, "Error while pinging server: " + error.getMessage());
                    listener.onResponse(createDataErrorResponse());
                });

        _queue.add(req);
    }


    public void fetchDishes(final LatLng userLocation, final DishesResponseListener listener) {
        String url = DISHES_REQUEST_URL + "?lat=" + userLocation.latitude + "&lng=" + userLocation.longitude;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
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
                    Log.e(TAG, "Error while fetching dishes: " + error.getMessage());
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
                    Log.e(TAG, "Error while fetching restaurants: " + error.getMessage());
                    listener.onResponse(createRestaurantsErrorResponse());
                });

        _queue.add(req);
    }

    public void fetchSearchResults(String query, LatLng userLocation, final SearchResponseListener listener) {
        // Construct the URL for the API endpoint
        String url = SEARCH_REQUEST_URL + "?query=" + query + "&lat=" + userLocation.latitude + "&lng=" + userLocation.longitude;

        // Create a new string request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Parse the JSON response and add each result to the allResults list
                        JSONArray results = response.getJSONArray("results");
                        ArrayList<Dish> dishes = parseDishes(results);
                        DataResponse.SearchResponse res = new DataResponse.SearchResponse(false, dishes);
                        listener.onResponse(res);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error while parsing response: " + e.getMessage());
                        listener.onResponse(createSearchErrorResponse());
                    }
                }, error -> {
            Log.e(TAG, "Error while fetching search results: " + error.getMessage());
            listener.onResponse(createSearchErrorResponse());
        });

        // Add the request to the queue
        _queue.add(req);
    }

    private ArrayList<Dish> parseDishes(JSONArray dishesResponse) throws JSONException {
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

    @SuppressLint("SimpleDateFormat")
    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing date: " + e.getMessage());
            return null;
        }
    }
}
