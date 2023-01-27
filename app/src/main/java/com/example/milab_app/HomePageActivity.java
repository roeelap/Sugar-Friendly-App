package com.example.milab_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity";

    private List<Dish> dishes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        User user = getIntent().getParcelableExtra("user");

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

        initDishRecyclerViews();
    }

    private void initDishRecyclerViews() {
        Log.d(TAG, "initDishRecyclerViews");

        // init recommendations recycler view
        final RecyclerView recommendations = findViewById(R.id.recommendations);
        initRecyclerView(recommendations);

        // init top rated recycler view
        final RecyclerView topRated = findViewById(R.id.topRated);
        initRecyclerView(topRated);

        // init newest recycler view
        final RecyclerView newest = findViewById(R.id.newest);
        initRecyclerView(newest);
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "initDishRecyclerView - " + recyclerView.getId() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(this, dishes);
        recyclerView.setAdapter(adapter);
    }
}