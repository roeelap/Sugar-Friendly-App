package com.example.milab_app;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DishFetcher {
    private final RequestQueue _queue;

    private final String TAG = "DishFetcher";
    private final static String IP_ADDRESS = "10.0.0.38";
    private final static String REQUEST_URL = "http://" + IP_ADDRESS + ":8080/get-all-dishes";

    public static class DishResponse {
        public boolean isError;
        public ArrayList<Dish> dishes;

        public DishResponse(boolean isError, ArrayList<Dish> dishes) {
            this.isError = isError;
            this.dishes = dishes;
        }
    }

    public interface DishResponseListener {
        void onResponse(DishResponse response);
    }

    public DishFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private DishResponse createErrorResponse() {
        return new DishResponse(true, null);
    }

    public void dispatchRequest(final DishResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null,
                response -> {
                    Log.d(TAG, "Got response: " + response.toString());
                    try {
                        JSONArray dishesResponse = response.getJSONArray("dishes");
                        ArrayList<Dish> dishes = parseDishes(dishesResponse);
                        DishResponse res = new DishResponse(false, dishes);
                        listener.onResponse(res);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Error while parsing response: " + e.getMessage());
                        listener.onResponse(createErrorResponse());
                    }
                }, error -> {
                    Log.e(TAG, "Error while fetching user: " + error.getMessage());
                    listener.onResponse(createErrorResponse());
                });

        _queue.add(req);
    }

    public ArrayList<Dish> parseDishes(JSONArray dishesResponse) throws JSONException {
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
}
