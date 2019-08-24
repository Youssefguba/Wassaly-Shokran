package com.phoenix.otlobbety;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
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
import com.phoenix.otlobbety.ViewHolder.CartViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import io.paperdb.Paper;
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
//                Create new Request
//                Request request = new Request(
//                        Common.currentUser.getPhone(),
//                        Common.currentUser.getName(),
//                        edtAddress.getText().toString(),
//                        txtTotalPrice.getText().toString(),
//                        "0", //Status
//                        edtComment.getText().toString(),
//                        cart
//                );
//                Submit to Firebase
//                We will using System.CurrentMill to key
//
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.edit_cart: {
                    editCart.setVisible(false);
                    cancelCart.setVisible(true);
                    CartViewHolder.deleteItemIcon.setVisibility(View.VISIBLE);
                    return true;

                }
                case R.id.cancel_cart: {
                    editCart.setVisible(true);
                    cancelCart.setVisible(false);
                    CartViewHolder.deleteItemIcon.setVisibility(View.GONE);
                    return false;
                }
            }
        } catch (NullPointerException ignored) {
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_edit, menu);

        editCart = menu.findItem(R.id.edit_cart);
        cancelCart = menu.findItem(R.id.cancel_cart);
        actionView = MenuItemCompat.getActionView(editCart);

        return true;
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

