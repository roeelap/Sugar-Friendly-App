package com.example.milab_app.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.DishRecyclerViewAdapter;
import com.example.milab_app.R;
import com.example.milab_app.objects.User;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        // setup greeting text
        String greeting = "Good evening " + user.getName() + "!";
        TextView greetingText = rootView.findViewById(R.id.greetingText);
        greetingText.setText(greeting);

        // setup recycler views
        if (recommendedDishes == null || topRatedDishes == null || newestDishes == null) {
            fetchDishes(rootView);
        } else {
            initHomePageLayout(rootView);
        }

        return rootView;
    }

    private void fetchDishes(View rootView) {
        Log.d(TAG, "fetchDishes");
        // show progress bar
        ((MainActivity) requireActivity()).showProgressBar();

        final DataFetcher fetcher = new DataFetcher(rootView.getContext());
        fetcher.fetchDishes(response -> {
            // hide progress bar
            ((MainActivity) requireActivity()).hideProgressBar();
            if (response.isError()) {
                Log.e(TAG, "Error fetching dishes");
                Toast.makeText(rootView.getContext(), "Error fetching dishes", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Fetched dishes successfully");
            recommendedDishes = response.getRecommendedDishes();
            calcDistancesToAddresses(recommendedDishes, userLatLng);
            topRatedDishes = response.getTopRatedDishes();
            calcDistancesToAddresses(topRatedDishes, userLatLng);
            newestDishes = response.getNewestDishes();
            calcDistancesToAddresses(newestDishes, userLatLng);
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
            if (dish.getSugarRating() >= sugarLevel) {
                filteredDishes.add(dish);
            }
        }
        DishRecyclerViewAdapter adapter = new DishRecyclerViewAdapter(getContext(), filteredDishes);
        recyclerView.setAdapter(adapter);
    }

    private void initUpSeekBar(View rootView) {
        rootView.findViewById(R.id.HomePagePromptQuestion).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.seekBarContainer).setVisibility(View.VISIBLE);
        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int sugarLevel, boolean fromUser) {
                Log.d(TAG, "updateRecyclerViewsToSugarLevel - " + sugarLevel);
                sugarLevel++; // increment sugarLevel by 1 to avoid 0 sugar rating
                // update recycler views to show dishes with sugarRating >= sugarLevel
                updateRecyclerViewToSugarLevel(recommendedDishes, sugarLevel, rootView.findViewById(R.id.recommendations));
                updateRecyclerViewToSugarLevel(topRatedDishes, sugarLevel, rootView.findViewById(R.id.topRated));
                updateRecyclerViewToSugarLevel(newestDishes, sugarLevel, rootView.findViewById(R.id.newest));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public LatLng getLatLngFromAddress(String address) {
        LatLng latLng = null;
        try {
            Geocoder geocoder = new Geocoder(requireContext());
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            double latitude= addresses.get(0).getLatitude();
            double longitude= addresses.get(0).getLongitude();
            latLng = new LatLng(latitude, longitude);
        } catch (IOException e) {
            Log.e(TAG, "getLatLngFromAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return latLng;
    }

    public double calcDistanceToAddress(String address, LatLng userLatLng) {
        final int R = 6371; // Radius of the earth
        LatLng addressLatLng = getLatLngFromAddress(address);
        if (addressLatLng == null || userLatLng == null) {
            return -1;
        }

        double latDistance = Math.toRadians(addressLatLng.latitude - userLatLng.latitude);
        double lonDistance = Math.toRadians(addressLatLng.longitude - userLatLng.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLatLng.latitude)) * Math.cos(Math.toRadians(addressLatLng.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public void calcDistancesToAddresses(ArrayList<Dish> dishes, LatLng userLatLng) {
        for (Dish dish : dishes) {
            dish.setDistanceToUser(calcDistanceToAddress(dish.getAddress(), userLatLng));
        }
    }
}