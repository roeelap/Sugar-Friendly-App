package com.example.milab_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.DishRecyclerViewAdapter;
import com.example.milab_app.R;
import com.example.milab_app.objects.User;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomePageFragment";

    private ArrayList<Dish> recommendedDishes;
    private ArrayList<Dish> topRatedDishes;
    private ArrayList<Dish> newestDishes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "created view");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        User user = ((MainActivity) requireActivity()).getUser();

        // setup greeting text
        String greeting = "Good evening " + user.getName() + "!";
        TextView greetingText = rootView.findViewById(R.id.greetingText);
        greetingText.setText(greeting);

        // setup recycler views
        if (recommendedDishes == null || topRatedDishes == null || newestDishes == null) {
            fetchDishes(rootView);
        } else {
            initDishRecyclerViews(rootView);
        }

        return rootView;
    }


    private void initDishRecyclerViews(View view) {
        Log.d(TAG, "initDishRecyclerViews");

        // init recommendations recycler view
        final RecyclerView recommendations = view.findViewById(R.id.recommendations);
        initRecyclerView(recommendations, recommendedDishes);

        // init top rated recycler view
        final RecyclerView topRated = view.findViewById(R.id.topRated);
        initRecyclerView(topRated, topRatedDishes);

        // init newest recycler view
        final RecyclerView newest = view.findViewById(R.id.newest);
        initRecyclerView(newest, newestDishes);
    }

    private void initRecyclerView(RecyclerView recyclerView, ArrayList<Dish> dishes) {
        Log.d(TAG, "initDishRecyclerView - " + recyclerView.getId() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(getContext(), dishes);
        recyclerView.setAdapter(adapter);
    }

    private void fetchDishes(View rootView) {
        final DataFetcher fetcher = new DataFetcher(rootView.getContext());
        fetcher.fetchDishes(response -> {
            if (response.isError()) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(rootView.getContext(), "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            recommendedDishes = response.getRecommendedDishes();
            topRatedDishes = response.getTopRatedDishes();
            newestDishes = response.getNewestDishes();
            initDishRecyclerViews(rootView);
        });
    }
}