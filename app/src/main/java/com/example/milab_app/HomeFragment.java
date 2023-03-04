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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomePageFragment";

    private User user;
    private ArrayList<Dish> dishes;

    public HomeFragment() {
        // TODO: get user from login activity
        // User user = getIntent().getParcelableExtra("user");
        user = User.getInstance("Roee", "roee", null, null, null, null);
        // TODO: get dishes from database
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "created view");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        user =((MainActivity) requireActivity()).getUser();
        dishes = ((MainActivity) requireActivity()).getDishes();

        // setup greeting text
        String greeting = "Good evening " + user.getName() + "!";
        TextView greetingText = rootView.findViewById(R.id.greetingText);
        greetingText.setText(greeting);

        // setup recycler views
        initDishRecyclerViews(rootView);

        return rootView;
    }


    private void initDishRecyclerViews(View view) {
        Log.d(TAG, "initDishRecyclerViews");

        // init recommendations recycler view
        final RecyclerView recommendations = view.findViewById(R.id.recommendations);
        initRecyclerView(recommendations);

        // init top rated recycler view
        final RecyclerView topRated = view.findViewById(R.id.topRated);
        initRecyclerView(topRated);

        // init newest recycler view
        final RecyclerView newest = view.findViewById(R.id.newest);
        initRecyclerView(newest);
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "initDishRecyclerView - " + recyclerView.getId() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(getContext(), dishes);
        recyclerView.setAdapter(adapter);
    }
}