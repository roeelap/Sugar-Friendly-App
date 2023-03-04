package com.example.milab_app.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.milab_app.objects.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestaurantFetcher {
    private final RequestQueue _queue;

    private final String TAG = "RestaurantFetcher";
    private final static String IP_ADDRESS = "10.0.0.38";
    private final static String REQUEST_URL = "http://" + IP_ADDRESS + ":8080/get-all-restaurants";

    public static class RestaurantResponse {
        public boolean isError;
        public ArrayList<Restaurant> restaurants;

        public RestaurantResponse(boolean isError, ArrayList<Restaurant> restaurants) {
            this.isError = isError;
            this.restaurants = restaurants;
        }
    }

    public interface RestaurantResponseListener {
        void onResponse(RestaurantResponse response);
    }

    public RestaurantFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private RestaurantResponse createErrorResponse() {
        return new RestaurantResponse(true, null);
    }

    public void dispatchRequest(final RestaurantResponseListener listener) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, null,
                response -> {
                    Log.d(TAG, "Got response: " + response.toString());
                    try {
                        JSONArray restaurantsResponse = response.getJSONArray("restaurants");
                        ArrayList<Restaurant> restaurants = parseRestaurants(restaurantsResponse);
                        RestaurantResponse res = new RestaurantResponse(false, restaurants);
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

    public ArrayList<Restaurant> parseRestaurants(JSONArray restaurantsResponse) throws JSONException {
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
