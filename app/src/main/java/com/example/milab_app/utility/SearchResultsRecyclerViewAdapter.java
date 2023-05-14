package com.example.milab_app.utility;

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

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "SearchResultsRecycleViewAdapter";
    private final ArrayList<Dish> dishes;
    private final Context context;

    public SearchResultsRecyclerViewAdapter(Context c, ArrayList<Dish> d) {
        context = c;
        dishes = d;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(context)
                .inflate(R.layout.search_result_dish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.restaurant.setText(dish.getRestaurantName());
        holder.dishImage.setImageResource(R.drawable.sushi);

        holder.dishName.setOnClickListener(v -> {
            Log.d(TAG, "onClick: " + holder.dishName.getText());
            ((MainActivity) context).showDishDetailsFragment(dish, "search");
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dishName;
        public final TextView restaurant;
        public final ImageView dishImage;

        public ViewHolder(View view) {
            super(view);
            dishName = view.findViewById(R.id.dishName);
            restaurant = view.findViewById(R.id.restaurantName);
            dishImage = view.findViewById(R.id.dishImage);
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
