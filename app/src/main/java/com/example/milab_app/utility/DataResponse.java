package com.example.milab_app.utility;

import com.example.milab_app.objects.Dish;

import java.util.ArrayList;

public class DataResponse {
    private final boolean isError;

    public DataResponse(boolean isError) {
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    public static class DishesResponse extends DataResponse {
        private final ArrayList<Dish> recommendedDishes;
        private final ArrayList<Dish> topRatedDishes;
        private final ArrayList<Dish> newestDishes;

        public DishesResponse(boolean isError,
                              ArrayList<Dish> recommendedDishes, ArrayList<Dish> topRatedDishes ,
                              ArrayList<Dish> newestDishes) {
            super(isError);
            this.recommendedDishes = recommendedDishes;
            this.topRatedDishes = topRatedDishes;
            this.newestDishes = newestDishes;
        }

        public ArrayList<Dish> getRecommendedDishes() {
            return recommendedDishes;
        }

        public ArrayList<Dish> getTopRatedDishes() {
            return topRatedDishes;
        }

        public ArrayList<Dish> getNewestDishes() {
            return newestDishes;
        }
    }

    public static class SearchResponse extends DataResponse {
        private final ArrayList<Dish> searchResults;

        public SearchResponse(boolean isError, ArrayList<Dish> searchResults) {
            super(isError);
            this.searchResults = searchResults;
        }

        public ArrayList<Dish> getSearchResults() {
            return searchResults;
        }
    }
}
