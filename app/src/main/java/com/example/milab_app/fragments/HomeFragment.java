package com.example.milab_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.DishRecyclerViewAdapter;
import com.example.milab_app.R;
import com.example.milab_app.objects.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomePageFragment";

    private ArrayList<Dish> recommendedDishes;
    private ArrayList<Dish> topRatedDishes;
    private ArrayList<Dish> newestDishes;

    private LatLng userLatLng;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "created view");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        User user = ((MainActivity) requireActivity()).getUser();
        userLatLng = ((MainActivity) requireActivity()).getCurrentDeviceLocation();

        // update greeting text
        TextView greeting = rootView.findViewById(R.id.greetingText);
        greeting.setText(String.format("Hello %s!", user.getName()));

        // setup recycler views
        if (recommendedDishes == null || topRatedDishes == null || newestDishes == null) {
            fetchDishes(rootView);
        } else {
            initHomePageLayout(rootView);
        }

        Button sugarLevelPopupButton = rootView.findViewById(R.id.sugarLevelPopupButton);
        sugarLevelPopupButton.setOnClickListener(v -> toggleSugarLevelPopUp(rootView));

        return rootView;
    }

    private void fetchDishes(View rootView) {
        Log.d(TAG, "fetchDishes");
        // show progress bar
        ((MainActivity) requireActivity()).showProgressBar();

        final DataFetcher fetcher = DataFetcher.getInstance(rootView.getContext());
        fetcher.fetchDishes(userLatLng, response -> {
            // hide progress bar
            ((MainActivity) requireActivity()).hideProgressBar();
            if (response.isError()) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(rootView.getContext(), "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            recommendedDishes = response.getRecommendedDishes();
            topRatedDishes = response.getTopRatedDishes();
            newestDishes = response.getNewestDishes();

            initHomePageLayout(rootView);
        });
    }

    private void initHomePageLayout(View rootView) {
        Log.d(TAG, "initDishRecyclerViews");

        // init recommendations recycler view
        final RecyclerView recommendations = rootView.findViewById(R.id.recommendations);
        initRecyclerView(recommendations, recommendedDishes);
        rootView.findViewById(R.id.recommendationsLabel).setVisibility(View.VISIBLE);

        // init top rated recycler view
        final RecyclerView topRated = rootView.findViewById(R.id.topRated);
        initRecyclerView(topRated, topRatedDishes);
        rootView.findViewById(R.id.topRatedLabel).setVisibility(View.VISIBLE);

        // init newest recycler view
        final RecyclerView newest = rootView.findViewById(R.id.newest);
        initRecyclerView(newest, newestDishes);
        rootView.findViewById(R.id.newestLabel).setVisibility(View.VISIBLE);

        initUpSeekBar(rootView);
    }

    private void initRecyclerView(RecyclerView recyclerView, ArrayList<Dish> dishes) {
        Log.d(TAG, "initDishRecyclerView - " + recyclerView.getId() + "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        updateRecyclerViewToSugarLevel(dishes, 3, recyclerView);
    }

    private void updateRecyclerViewToSugarLevel(ArrayList<Dish> dishes, int sugarLevel, RecyclerView recyclerView) {
        Log.d(TAG, "updateRecyclerViewToSugarLevel - " + sugarLevel);
        ArrayList<Dish> filteredDishes = new ArrayList<>();
        for (Dish dish : dishes) {
            if (dish.getSugarRating() <= sugarLevel) {
                filteredDishes.add(dish);
            }
        }
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(getContext(), filteredDishes);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void initUpSeekBar(View rootView) {
        Button sugarLevelPopupButton = rootView.findViewById(R.id.sugarLevelPopupButton);
        TextView sugarLevelTextView = rootView.findViewById(R.id.sugar_level);
        Slider sugarLevelSlider = rootView.findViewById(R.id.sugar_level_slider);
        sugarLevelSlider.addOnChangeListener((slider, value, fromUser) -> {
            Log.d(TAG, "updateRecyclerViewsToSugarLevel - " + value);
            if (value == slider.getValueTo()) {
                sugarLevelPopupButton.setText("HI");
                sugarLevelTextView.setText("HI");
            } else if (value == slider.getValueFrom()) {
                sugarLevelPopupButton.setText("I");
                sugarLevelTextView.setText("I");
            } else {
                sugarLevelPopupButton.setText(String.valueOf((int) value));
                sugarLevelTextView.setText(String.valueOf((int) value));
            }
            // normalize sugarLevel from the range [50, 150] to [1, 5]
            int sugarLevel = (int) Math.ceil((value - 50) / 20.0);
            // reverse sugarLevel to be in the range [6, 2]
            sugarLevel = 7 - sugarLevel;
            // update recycler views to show dishes with sugarRating >= sugarLevel
            updateRecyclerViewToSugarLevel(recommendedDishes, sugarLevel, rootView.findViewById(R.id.recommendations));
            updateRecyclerViewToSugarLevel(topRatedDishes, sugarLevel, rootView.findViewById(R.id.topRated));
            updateRecyclerViewToSugarLevel(newestDishes, sugarLevel, rootView.findViewById(R.id.newest));
        });
    }

    public void toggleSugarLevelPopUp(View rootView) {
        View sugarLevelPopUp = rootView.findViewById(R.id.sugarLevelPopup);
        if (sugarLevelPopUp.getVisibility() == View.VISIBLE) {
            sugarLevelPopUp.setVisibility(View.GONE);
            return;
        }
        sugarLevelPopUp.setVisibility(View.VISIBLE);
        Button closeButton = sugarLevelPopUp.findViewById(R.id.popupClose);
        closeButton.setOnClickListener(v -> sugarLevelPopUp.setVisibility(View.GONE));
    }

}