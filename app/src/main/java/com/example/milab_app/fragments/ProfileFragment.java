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

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.utility.DishRecyclerViewAdapter;
import com.example.milab_app.R;
import com.example.milab_app.objects.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private final User user;
    private final ArrayList<Dish> dishes = new ArrayList<>();
    public ProfileFragment() {
        // TODO: get user from login activity
        user = ((MainActivity) requireActivity()).getUser();
        // TODO: get dishes from database
        dishes.add(new Dish("Dish 1", "Restaurant 1"));
        dishes.add(new Dish("Dish 2", "Restaurant 2"));
        dishes.add(new Dish("Dish 3", "Restaurant 3"));
        dishes.add(new Dish("Dish 4", "Restaurant 4"));
        dishes.add(new Dish("Dish 5", "Restaurant 5"));
        dishes.add(new Dish("Dish 6", "Restaurant 6"));
        dishes.add(new Dish("Dish 7", "Restaurant 7"));
        dishes.add(new Dish("Dish 8", "Restaurant 8"));
        dishes.add(new Dish("Dish 9", "Restaurant 9"));
        dishes.add(new Dish("Dish 10", "Restaurant 10"));
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