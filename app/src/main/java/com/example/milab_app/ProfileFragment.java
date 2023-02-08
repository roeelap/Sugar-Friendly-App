package com.example.milab_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private final User user;
    private final List<Dish> dishes = new ArrayList<>();
    public ProfileFragment() {
        // TODO: get user from login activity
        // User user = getIntent().getParcelableExtra("user");
        user = User.getInstance("Roee", "roee", null, null, null, null);
        // TODO: get dishes from database
        dishes.add(new Dish("Dish 1", new Restaurant("Restaurant 1")));
        dishes.add(new Dish("Dish 2", new Restaurant("Restaurant 2")));
        dishes.add(new Dish("Dish 3", new Restaurant("Restaurant 3")));
        dishes.add(new Dish("Dish 4", new Restaurant("Restaurant 4")));
        dishes.add(new Dish("Dish 5", new Restaurant("Restaurant 5")));
        dishes.add(new Dish("Dish 6", new Restaurant("Restaurant 6")));
        dishes.add(new Dish("Dish 7", new Restaurant("Restaurant 7")));
        dishes.add(new Dish("Dish 8", new Restaurant("Restaurant 8")));
        dishes.add(new Dish("Dish 9", new Restaurant("Restaurant 9")));
        dishes.add(new Dish("Dish 10", new Restaurant("Restaurant 10")));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        String greeting = "Hi " + user.getName() + "!";
        TextView greetingText = rootView.findViewById(R.id.profileGreetingMessage);
        greetingText.setText(greeting);

        final RecyclerView favorites = rootView.findViewById(R.id.favorites);
        initRecyclerView(favorites);

        return rootView;
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "initDishRecyclerView - " + recyclerView.getId() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(getContext(), dishes);
        recyclerView.setAdapter(adapter);
    }
}