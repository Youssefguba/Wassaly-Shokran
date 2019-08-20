package com.phoenix.otlobbety;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ornach.nobobutton.NoboButton;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.DataMessage;
import com.phoenix.otlobbety.Model.MyResponse;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.Model.Token;
import com.phoenix.otlobbety.Remote.APIService;
import com.phoenix.otlobbety.ViewHolder.CartAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cart extends AppCompatActivity {
    private static final String TAG = "Cart";
    public static TextView totalOfAllItems;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    NoboButton btnPlace;

    List<Order> cartList = new ArrayList<>();
    CartAdapter adapter;

    APIService mApiService;

    ActionBar actionBar;

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

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnPlace = findViewById(R.id.btnPlaceOrder);
        totalOfAllItems = findViewById(R.id.total_of_all_items);

        btnPlace.setOnClickListener(v -> {
            if (cartList.size() > 0) {
                showAlertDialog();
            } else {
                Toast.makeText(Cart.this, "سلة المشتريات فارغة!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final MaterialEditText edtAddress = order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create new Request
//                Request request = new Request(
//                        Common.currentUser.getPhone(),
//                        Common.currentUser.getName(),
//                        edtAddress.getText().toString(),
//                        txtTotalPrice.getText().toString(),
//                        "0", //Status
//                        edtComment.getText().toString(),
//                        cart
//                );
                //Submit to Firebase
                //We will using System.CurrentMill to key

//                String order_number = String.valueOf(System.currentTimeMillis());
//                requests.child(order_number)
//                        .setValue(request);
//

                //Delete Cart
                new Database(getBaseContext()).cleanCart();

//                sendNotificationOrder(order_number);
                Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query data = tokens.orderByChild("serverToken").equalTo(true); //get all node with "isServerToken"
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);

//                    Notification notification = new Notification("OtlobBety","You have new Order"+order_number);
//                    Sender content = new Sender(serverToken.getToken(),notification);
                    HashMap<String, String> dataSend = new HashMap<>();
                    dataSend.put("title", "Wasally Shokran");
                    dataSend.put("message", "You have new Order" + order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(), dataSend);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("Content", test);


                    mApiService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                                    new Database(getBaseContext()).cleanCart();
                                    finish();

                                   /* if (response.body().success == 1)
                                    {
                                        Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                                        new Database(getBaseContext()).cleanCart();
                                        finish();
                                    }else
                                    {
                                        Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                    }*/
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cartList = new Database(this).getCarts();
        adapter = new CartAdapter(cartList, this);
        recyclerView.setAdapter(adapter);


        //Calculate total price
        int totalPrice;
        int foodPrice = 0;

        for (Order item : cartList) {
            try {
                foodPrice += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

            } catch (NumberFormatException e) {
                Log.e(TAG, "There is an Error ");
            }

            totalPrice = foodPrice;
            totalOfAllItems.setText(totalPrice + " ج.م ");
            Log.e(TAG, String.valueOf(totalPrice));

        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        cartList.remove(position);
        new Database(this).cleanCart();
        for (Order item : cartList)
            new Database(this).addToCart(item);
        loadListFood();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ItemsList.class));
    }
}

