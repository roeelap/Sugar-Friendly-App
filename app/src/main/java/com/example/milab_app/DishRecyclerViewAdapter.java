package com.example.milab_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DishRecyclerViewAdapter extends RecyclerView.Adapter<DishRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "DishRecyclerViewAdapter";
    private final List<Dish> dishes;
    private final Context context;

    public DishRecyclerViewAdapter(Context c, List<Dish> d) {
        context = c;
        dishes = d;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_dish_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.dishName.setText(dish.getName());
        holder.restaurant.setText(dish.getRestaurant().getName());

        holder.dishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + holder.dishName.getText());
                Toast.makeText(v.getContext(), "Clicked on " + holder.dishName.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dishName;
        public final TextView restaurant;
        public final ImageView dishImage;

        public ViewHolder(View view) {
            super(view);
            dishName = view.findViewById(R.id.dishName);
            restaurant = view.findViewById(R.id.restaurant);
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
