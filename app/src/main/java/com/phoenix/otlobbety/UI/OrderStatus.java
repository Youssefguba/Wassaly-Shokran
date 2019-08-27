package com.phoenix.otlobbety.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Request;
import com.phoenix.otlobbety.R;
import com.phoenix.otlobbety.ViewHolder.OrderViewHolder;

import io.paperdb.Paper;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    CardView cardView;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requestsRef;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("الطلبات السابقة");
        setSupportActionBar(toolbar);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        //Firebase
        database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String phoneNumber = Paper.book().read(Common.PHONE_TEXT);
        loadOrders(phoneNumber);

    }

    private void loadOrders(String phone) {
        Query retrieveData = requestsRef.orderByChild("phoneNumberOfCustomer").equalTo(phone);

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(retrieveData, Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int position, @NonNull Request request) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderAddress.setText(request.getAddressOfCustomer());
                orderViewHolder.txtOrderPhone.setText(request.getPhoneNumberOfCustomer());


            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}