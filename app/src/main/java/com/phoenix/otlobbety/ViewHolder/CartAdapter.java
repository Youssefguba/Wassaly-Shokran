package com.phoenix.otlobbety.ViewHolder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.R;
import com.phoenix.otlobbety.UI.Cart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Cart cart;
    MenuItem item;

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
        holder.deleteItemImg.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        holder.deleteItemImg.setOnClickListener(view -> cart.deleteCart(position));

        holder.quantityButton.setOnValueChangeListener((view, oldValue, newValue) -> {
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

                int costOfDelivery = Paper.book().read(Common.DELIVERY_COST);
                totalPrice = foodPrice;
                Paper.book().write(Common.TOTAL_PRICE, totalPrice);
                Log.e("Cart", String.valueOf(totalPrice));
                Log.e("Cart", Paper.book().read(Common.TOTAL_PRICE, totalPrice) + " This is number of Adapter");

                int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
                holder.totalOfOneItem.setText(price + " ج.م ");
                Cart.totalOfAllItems.setText(totalPrice + costOfDelivery + " ج.م ");

            }
        });

        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.totalOfOneItem.setText(price + " ج.م ");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public Order getItem(int position) {
        return listData.get(position);
    }

    public void removeItem(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }
}
