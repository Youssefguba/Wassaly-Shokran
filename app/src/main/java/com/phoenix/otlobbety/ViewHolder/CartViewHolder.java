package com.phoenix.otlobbety.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phoenix.otlobbety.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static ImageView deleteItemIcon;
    public TextView txt_cart_name, itemPrice, totalOfAllItems, totalOfOneItem;
    public ImageView cartImage, deleteItemImg;
    ElegantNumberButton quantityButton;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        cartImage = itemView.findViewById(R.id.cart_image);
        itemPrice = itemView.findViewById(R.id.item_price);
        totalOfAllItems = itemView.findViewById(R.id.total_of_all_items);
        totalOfOneItem = itemView.findViewById(R.id.total_of_item_price);
        quantityButton = itemView.findViewById(R.id.quantity_cart_button);
        deleteItemImg = itemView.findViewById(R.id.delete_item_cart);

    }

    @Override
    public void onClick(View v) {
    }

}
