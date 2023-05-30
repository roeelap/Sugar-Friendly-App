package com.example.milab_app.utility;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;

public class DishRecyclerViewAdapter extends RecyclerView.Adapter<DishRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "DishRecyclerViewAdapter";
    private final ArrayList<Dish> dishes;
    private final Context context;

    public DishRecyclerViewAdapter(Context c, ArrayList<Dish> d) {
        context = c;
        dishes = d;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_dish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.restaurant.setText(dish.getRestaurantName());
        holder.dishImage.setImageBitmap(dish.getImage());
        holder.rating.setText(String.valueOf(dish.getRating()));
        holder.sugarRating.setText(String.valueOf(dish.getSugarRating()));
        holder.distanceToDish.setText(format("%.1f km", dish.getDistanceToUser()));

        holder.dishImage.setOnClickListener(v -> {
            Log.d(TAG, "onClick: " + holder.dishName.getText());
            ((MainActivity) context).showDishDetailsFragment(dish, "home");
        });

        holder.toggleLikeButton(context, ((MainActivity) context).getFavDishes().contains(dish.getId()));
        holder.likeButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: " + holder.dishName.getText());
            HashSet<String> favDishes = ((MainActivity) context).getFavDishes();
            boolean isLikedAlready = favDishes.contains(dish.getId());
            holder.toggleLikeButton(context, !isLikedAlready);
            ((MainActivity) context).toggleDishLikability(dish.getId(), new MainActivity.Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: " + holder.dishName.getText());
                }

                @Override
                public void onError(String message) {
                    Log.d(TAG, "onError: " + holder.dishName.getText());
                    holder.toggleLikeButton(context, isLikedAlready); // revert the button to its previous state
                }
            });
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dishName;
        public final TextView restaurant;
        public final TextView rating;
        public final ImageView dishImage;
        public final TextView sugarRating;
        public final TextView distanceToDish;
        public final MaterialButton likeButton;

        public ViewHolder(View view) {
            super(view);
            dishName = view.findViewById(R.id.dishName);
            restaurant = view.findViewById(R.id.restaurantName);
            dishImage = view.findViewById(R.id.dishImage);
            distanceToDish = view.findViewById(R.id.distanceToDish);
            rating = view.findViewById(R.id.rating);
            sugarRating = view.findViewById(R.id.sugarRating);
            likeButton = view.findViewById(R.id.likeButton);
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

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + dishName.getText() + "'";
        }
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
