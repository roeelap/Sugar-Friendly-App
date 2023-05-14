package com.example.milab_app.utility;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milab_app.MainActivity;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.R;

import java.util.ArrayList;

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
        holder.dishImage.setImageResource(R.drawable.sushi);
        holder.rating.setText(String.valueOf(dish.getRating()));
        holder.sugarRating.setText(String.valueOf(dish.getSugarRating()));
        holder.distanceToDish.setText(format("%.1f km", dish.getDistanceToUser()));

        holder.dishImage.setOnClickListener(v -> {
            Log.d(TAG, "onClick: " + holder.dishName.getText());
            ((MainActivity) context).showDishDetailsFragment(dish, "home");
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dishName;
        public final TextView restaurant;
        public final TextView rating;
        public final ImageView dishImage;
        public final TextView sugarRating;
        public final TextView distanceToDish;
        public final ImageView likeButton;

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
