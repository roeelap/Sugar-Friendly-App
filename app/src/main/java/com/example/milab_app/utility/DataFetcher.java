package com.example.milab_app.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.milab_app.objects.Dish;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class DataFetcher {

    private final String TAG = "DataFetcher";

    private static DataFetcher instance;
    private final RequestQueue queue;

    private final static String URL = "https://handsome-teal-sheath-dress.cyclic.app";
    private final static String PING_REQUEST_URL = URL + "/isup";
    private final static String DISHES_REQUEST_URL = URL + "/dishes";
    private final static String SEARCH_REQUEST_URL = URL + "/search";
    private final static String UPLOAD_IMAGE_URL = URL + "/uploadImage";

    private final DataParser parser = new DataParser();

    public interface DataResponseListener {
        void onResponse(DataResponse response);
    }

    public interface DishesResponseListener {
        void onResponse(DataResponse.DishesResponse response);
    }

    public interface SearchResponseListener {
        void onResponse(DataResponse.SearchResponse response);
    }

    private DataFetcher(Context context) {
        queue = Volley.newRequestQueue(context);
    }


    private DataResponse createDataErrorResponse() {
        return new DataResponse(true);
    }

    public static synchronized DataFetcher getInstance(Context context) {
        if (instance == null) {
            instance = new DataFetcher(context);
        }
        return instance;
    }

    private DataResponse.DishesResponse createDishesErrorResponse() {
        return new DataResponse.DishesResponse(true, null, null, null);
    }

    private DataResponse.SearchResponse createSearchErrorResponse() {
        return new DataResponse.SearchResponse(true, null);
    }

    public<T> void addToRequestQueue(Request<T> request) {
        queue.add(request);
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

        queue.add(req);
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
                        ArrayList<Dish> recommendedDishes = parser.parseDishes(recommendedDishesResponse);
                        ArrayList<Dish> topRatedDishes = parser.parseDishes(topRatedDishesResponse);
                        ArrayList<Dish> newestDishes = parser.parseDishes(newestDishesResponse);
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

        queue.add(req);
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
                        ArrayList<Dish> dishes = parser.parseDishes(results);
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
        queue.add(req);
    }
}
