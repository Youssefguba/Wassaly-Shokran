package com.phoenix.otlobbety.ViewHolder;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phoenix.otlobbety.Cart;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Interface.ItemClickListener;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txt_cart_name, itemPrice, totalOfAllItems, totalOfOneItem;
    public ImageView cartImage;
    ElegantNumberButton quantityButton;

    private ItemClickListener itemClickListener;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        cartImage = itemView.findViewById(R.id.cart_image);
        itemPrice = itemView.findViewById(R.id.item_price);
        totalOfAllItems = itemView.findViewById(R.id.total_of_all_items);
        totalOfOneItem = itemView.findViewById(R.id.total_of_item_price);
        quantityButton = itemView.findViewById(R.id.quantity_cart_button);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Context context;
    private Cart cart;


    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        Picasso.with(cart).load(listData.get(position).getImage())
                .resize(70, 70)
                .centerCrop()
                .into(holder.cartImage);

        holder.itemPrice.setText(listData.get(position).getPrice() + " ج.م ");
        holder.txt_cart_name.setText(listData.get(position).getProductName());
        holder.quantityButton.setNumber(listData.get(position).getQuantity());

        holder.quantityButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                int foodPrice = 0;
                int totalPrice;

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //Update total text
                //Calculate total price
                List<Order> orders = new Database(cart).getCarts();
                for (Order item : orders) {
                    try {
                        foodPrice += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    totalPrice = foodPrice;
                    Log.e("Cart", String.valueOf(totalPrice));

                    int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
                    holder.totalOfOneItem.setText(price + " ج.م ");
                    Cart.totalOfAllItems.setText(totalPrice + " ج.م ");

                }
            }
        });

        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.totalOfOneItem.setText(price + " ج.م ");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
