package com.example.milab_app.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.R;
import com.example.milab_app.objects.Dish;
import com.google.android.material.button.MaterialButton;

public class DishDetailsFragment extends Fragment {

    private static final String TAG = "DishDetailsFragment";
    private final Dish dish;
    private final String previousFragmentTag;

    MaterialButton likeButton;

    public DishDetailsFragment(Dish d, String prevTag) {
        dish = d;
        previousFragmentTag = prevTag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dish_details, container, false);

        // get views
        TextView dishName = rootView.findViewById(R.id.dish_name_text_view);
        TextView restaurantName = rootView.findViewById(R.id.restaurant_name_text_view);
        TextView rating = rootView.findViewById(R.id.rating);
        TextView sugarRating = rootView.findViewById(R.id.sugarRating);
        TextView ratingLiteral = rootView.findViewById(R.id.ratingLiteral);
        TextView sugarRatingLiteral = rootView.findViewById(R.id.sugarRatingLiteral);

        // set up text
        dishName.setText(dish.getName());
        restaurantName.setText(dish.getRestaurantName());
        rating.setText(String.valueOf(dish.getRating()));
        setUpRatingLiteral(rootView, ratingLiteral);
        sugarRating.setText(String.valueOf(dish.getSugarRating()));
        setUpSugarRatingLiteral(rootView, sugarRatingLiteral);

        // set up buttons
        Button mapButton = rootView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> ((MainActivity) requireActivity()).showAddressInGoogleMaps(dish.getAddress(), dish.getRestaurantName()));

        Button closeButton = rootView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> ((MainActivity) requireActivity()).showFragment(previousFragmentTag));

        likeButton = rootView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(v -> toggleLikeButton(rootView));

        return rootView;
    }

    private void setUpRatingLiteral(View rootView, TextView ratingLiteral) {
        double rating = dish.getRating();
        if (rating < 3) {
            ratingLiteral.setText(R.string.okay);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_yellow));
        } else if (rating < 4) {
            ratingLiteral.setText(R.string.good);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_light_green));
        } else {
            ratingLiteral.setText(R.string.excellent);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_dark_green));
        }
        ratingLiteral.setPadding(30, 0, 30, 0);
    }

    private void setUpSugarRatingLiteral(View rootView, TextView ratingLiteral) {
        double sugarRating = dish.getSugarRating();
        if (sugarRating > 3) {
            ratingLiteral.setText(R.string.okay);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_yellow));
        } else if (sugarRating > 2) {
            ratingLiteral.setText(R.string.good);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_light_green));
        } else {
            ratingLiteral.setText(R.string.excellent);
            ratingLiteral.setBackground(ContextCompat.getDrawable(rootView.getContext(), R.drawable.rounded_corners_dark_green));
        }
        ratingLiteral.setPadding(30, 0, 30, 0);
    }

    private void toggleLikeButton(View rootView) {
        Log.d(TAG, "toggleLikeButton: toggling like button");
        dish.setIsLiked(!dish.getIsLiked());
        if (dish.getIsLiked()) {
            // set the icon to a red filled heart
            likeButton.setIcon(ContextCompat.getDrawable(rootView.getContext(), R.drawable.baseline_favorite_24));
            likeButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(rootView.getContext(), R.color.red)));
        } else {
            // set the icon to a black unfilled heart
            likeButton.setIcon(ContextCompat.getDrawable(rootView.getContext(), R.drawable.baseline_favorite_border_24));
            likeButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(rootView.getContext(), R.color.black)));
        }

        // TODO: add dish to user's favorites
        // ((MainActivity) requireActivity()).getUser().addFavoriteDish(dish);
        // ((MainActivity) requireActivity()).updateUserInDB();
        // likeButton.setEnabled(false);
    }
}