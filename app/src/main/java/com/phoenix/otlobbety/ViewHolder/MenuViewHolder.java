package com.phoenix.otlobbety.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenix.otlobbety.Interface.ItemClickListener;
import com.phoenix.otlobbety.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;
    public TextView deliveryTime;
    public TextView deliveryCost;
    private ItemClickListener itemClickListener;


    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.general_menu_name);
        imageView = itemView.findViewById(R.id.menu_image);
        deliveryTime = itemView.findViewById(R.id.delivery_time_sub_category);
        deliveryCost = itemView.findViewById(R.id.delivery_cost_sub_category);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }

}
