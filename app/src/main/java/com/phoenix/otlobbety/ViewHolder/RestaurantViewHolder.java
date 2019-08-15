package com.phoenix.otlobbety.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenix.otlobbety.Interface.ItemClickListener;
import com.phoenix.otlobbety.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView imageViewOfRestaurant;
    public TextView deliveryTime;
    public TextView nameOfRestaurant;
    private ItemClickListener itemClickListener;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewOfRestaurant = itemView.findViewById(R.id.restaurant_menu_image);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
        nameOfRestaurant = itemView.findViewById(R.id.restaurant_menu_name);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}