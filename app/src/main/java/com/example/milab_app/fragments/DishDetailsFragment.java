package com.example.milab_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.R;
import com.example.milab_app.objects.Dish;

public class DishDetailsFragment extends Fragment {

    private static final String TAG = "DishDetailsFragment";
    private final Dish dish;
    private final String previousFragmentTag;

    public DishDetailsFragment(Dish d, String prevTag) {
        dish = d;
        previousFragmentTag = prevTag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dish_details, container, false);

        // set up dish details
        TextView dishName = rootView.findViewById(R.id.dish_name_text_view);
        TextView restaurantName = rootView.findViewById(R.id.restaurant_name_text_view);
        TextView rating = rootView.findViewById(R.id.rating);
        TextView sugarRating = rootView.findViewById(R.id.sugarRating);
        dishName.setText(dish.getName());
        restaurantName.setText(dish.getRestaurantName());
        rating.setText(String.valueOf(dish.getRating()));
        sugarRating.setText(String.valueOf(dish.getSugarRating()));

        // set up map button
        Button mapButton = rootView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> ((MainActivity) requireActivity()).showAddressInGoogleMaps(dish.getAddress(), dish.getRestaurantName()));

        Button closeButton = rootView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> ((MainActivity) requireActivity()).showFragment(previousFragmentTag));

        return rootView;
    }
}