package com.example.milab_app.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.R;
import com.example.milab_app.objects.Dish;
import com.google.android.material.button.MaterialButton;

import java.util.HashSet;

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

        MainActivity mainActivity = (MainActivity) requireActivity();
        Context context = getContext();
        setUpUI(rootView, dish);

        Button mapButton = rootView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> mainActivity.showAddressInGoogleMaps(dish.getAddress(), dish.getRestaurantName()));

        Button closeButton = rootView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> mainActivity.showFragment(previousFragmentTag));

        likeButton = rootView.findViewById(R.id.like_button);
        toggleLikeButton(context, mainActivity.getFavDishes().contains(dish.getId()));
        likeButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: likeButton" + dish.getName());
            HashSet<String> favDishes = mainActivity.getFavDishes();
            boolean isLikedAlready = favDishes.contains(dish.getId());
            toggleLikeButton(context, !isLikedAlready);
            mainActivity.toggleDishLikability(dish.getId(), new MainActivity.Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: likeButton " + dish.getName());
                }

                @Override
                public void onError(String message) {
                    Log.d(TAG, "onError: likeButton " + dish.getName());
                    toggleLikeButton(context, isLikedAlready); // revert the button to its previous state
                }
            });
        });

        return rootView;
    }

    private void setUpUI(View rootView, Dish dish) {
        TextView dishName = rootView.findViewById(R.id.dish_name_text_view);
        TextView restaurantName = rootView.findViewById(R.id.restaurant_name_text_view);
        TextView rating = rootView.findViewById(R.id.rating);
        TextView sugarRating = rootView.findViewById(R.id.sugarRating);
        TextView ratingLiteral = rootView.findViewById(R.id.ratingLiteral);
        TextView sugarRatingLiteral = rootView.findViewById(R.id.sugarRatingLiteral);
        ImageView dishImage = rootView.findViewById(R.id.dish_image_view);

        dishName.setText(dish.getName());
        restaurantName.setText(dish.getRestaurantName());
        rating.setText(String.valueOf(dish.getRating()));
        setUpRatingLiteral(rootView, ratingLiteral);
        sugarRating.setText(String.valueOf(dish.getSugarRating()));
        dishImage.setImageBitmap(dish.getImage());
        setUpSugarRatingLiteral(rootView, sugarRatingLiteral);
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

    public void toggleLikeButton(Context context, boolean isLiked) {
        if (isLiked) {
            // set the icon to a red filled heart
            likeButton.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_favorite_24));
            likeButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
        } else {
            // set the icon to a black unfilled heart
            likeButton.setIcon(ContextCompat.getDrawable(context, R.drawable.baseline_favorite_border_24));
            likeButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
        }
    }
}