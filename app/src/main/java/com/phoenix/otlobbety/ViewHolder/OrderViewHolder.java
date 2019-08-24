package com.phoenix.otlobbety.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenix.otlobbety.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);


    }



}





