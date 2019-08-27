package com.phoenix.otlobbety;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ornach.nobobutton.NoboButton;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.Remote.APIService;
import com.phoenix.otlobbety.ViewHolder.CartAdapter;

import io.paperdb.Paper;


public class Cart extends AppCompatActivity {
    private static final String TAG = "Cart";
    public static TextView totalOfAllItems;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    NoboButton btnPlace;
    CartAdapter adapter;
    APIService mApiService;
    ImageView deleteItemIcon;
    View actionView;
    ActionBar actionBar;

    TextView deliveryCost;
    private MenuItem editCart;
    private MenuItem cancelCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbarOfSubCategory);
        toolbar.setTitle("سلة المشتريات");
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //Init Service
        mApiService = Common.getFCMService();

        Paper.init(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        deliveryCost = findViewById(R.id.ShippingPrice);
        btnPlace = findViewById(R.id.btnPlaceOrder);
        totalOfAllItems = findViewById(R.id.total_of_all_items);
        deleteItemIcon = findViewById(R.id.delete_item_cart);

        btnPlace.setOnClickListener(v -> {
            if (Common.listOfCart.size() > 0) {
                Intent orderIntent = new Intent(Cart.this, ConfirmOrderRequest.class);
                startActivity(orderIntent);

            } else {
                Toast.makeText(Cart.this, "سلة المشتريات فارغة!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListFoodOfCart();
    }

    public void loadListFoodOfCart() {
        Common.listOfCart = new Database(this).getCarts();
        adapter = new CartAdapter(Common.listOfCart, this);
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int totalPrice;
        int foodPrice = 0;

        for (Order item : Common.listOfCart) {
            try {
                foodPrice += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

            } catch (NumberFormatException e) {
                Log.e(TAG, "There is an Error ");
            }


            //Delivery Cost
            int costOfDelivery = Paper.book().read(Common.DELIVERY_COST);
            deliveryCost.setText(costOfDelivery + " ج.م ");

            //Total Price
            totalPrice = foodPrice;
            totalOfAllItems.setText(totalPrice + costOfDelivery + " ج.م ");
            Paper.book().write(Common.TOTAL_PRICE, totalPrice);

            Log.d(TAG, String.valueOf(totalPrice));
            Log.d("Total Price", Paper.book().read(Common.TOTAL_PRICE) + "this is total price");


        }

        adapter.notifyDataSetChanged();
    }


    public void deleteCart(int position) {
        Common.listOfCart.remove(position);
        new Database(this).cleanCart();
        for (Order item : Common.listOfCart)
            new Database(this).addToCart(item);
        loadListFoodOfCart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ItemsList.class));
    }
}

