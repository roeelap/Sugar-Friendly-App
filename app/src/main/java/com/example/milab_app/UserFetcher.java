package com.example.milab_app;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserFetcher {
    private RequestQueue _queue;
    private final static String REQUEST_URL = "http://10.0.0.2:8080/users/login";

    public class UserResponse {
        public boolean isError;
        public String username;
        public JSONArray favDishes;
        public JSONArray favRestaurants;
        public JSONArray recentFoodTags;
        public JSONArray recentNutritionTags;

        public UserResponse(boolean isError, String username, JSONArray favDishes, JSONArray favRestaurants,
                            JSONArray recentFoodTags, JSONArray recentNutritionTags) {
            this.isError = isError;
            this.username = username;
            this.favDishes = favDishes;
            this.favRestaurants = favRestaurants;
            this.recentFoodTags = recentFoodTags;
            this.recentNutritionTags = recentNutritionTags;
        }
    }

    public interface UserResponseListener {
        void onResponse(UserResponse response);
    }

    public UserFetcher(Context context) {
        _queue = Volley.newRequestQueue(context);
    }

    private UserResponse createErrorResponse() {
        return new UserResponse(true, null, null,
                null, null, null);
    }

    public void dispatchRequest(final String username, final String password, final UserResponseListener listener) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            listener.onResponse(createErrorResponse());
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, REQUEST_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("StockGetter", "Got response: " + response.toString());
                        try {
                            String username = response.getString("username");
                            JSONArray favDishes = response.getJSONArray("favDishes");
                            JSONArray favRestaurants = response.getJSONArray("favRestaurants");
                            JSONArray recentFoodTags = response.getJSONArray("recentFoodTags");
                            JSONArray recentNutritionTags = response.getJSONArray("recentNutritionTags");
                            UserResponse res = new UserResponse(false, username, favDishes, favRestaurants,
                                    recentFoodTags, recentNutritionTags);
                            listener.onResponse(res);
                        }
                        catch (JSONException e) {
                            Log.e("userFetcher", "Error while parsing response: " + e.getMessage());
                            listener.onResponse(createErrorResponse());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("StockGetter", "Error while fetching user: " + error.getMessage());
                listener.onResponse(createErrorResponse());
            }
        });

        _queue.add(req);
    }
}
