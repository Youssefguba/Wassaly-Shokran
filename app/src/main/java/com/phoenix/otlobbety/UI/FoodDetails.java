package com.phoenix.otlobbety.UI;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ornach.nobobutton.NoboButton;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.Food;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.R;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {
    public static final String TAG = "FoodDetails";
    TextView foodname, foodprice, fooddescription;
    ImageView foodimage;
    CollapsingToolbarLayout cooCollapsingToolbarLayout;
    ElegantNumberButton counterButton;
    String foodId = "";
    NoboButton submitOrder;

    FirebaseDatabase database;
    DatabaseReference myRef;
    Food currentFood;

    int newValueOfCounter;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase Initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ExpandableList");

        //InitView
        counterButton = findViewById(R.id.number_button);
        submitOrder = findViewById(R.id.submit_order);

        fooddescription = findViewById(R.id.food_description);
        foodname = findViewById(R.id.food_name);
        foodprice = findViewById(R.id.food_price);
        foodimage = findViewById(R.id.img_food);
        cooCollapsingToolbarLayout = findViewById(R.id.collapsing);

        counterButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            newValueOfCounter = newValue;
            total = (Integer.parseInt(currentFood.getPrice()) * newValueOfCounter);
            String s = String.valueOf(total);
            foodprice.setText(s);
        });

        submitOrder.setOnClickListener(v -> {
            new Database(getBaseContext()).addToCart(new Order(
                    foodId,
                    currentFood.getName(),
                    counterButton.getNumber(),
                    currentFood.getPrice(),
                    currentFood.getDiscount(),
                    currentFood.getImage()
            ));

            Toast.makeText(FoodDetails.this, "تمت إضافته إلى السلة", Toast.LENGTH_SHORT).show();
            finish();

        });

        //Get Food Id Intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("childItemID");
        if (!foodId.isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext()))
                getDetailsOfFood();
            else {
                Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getDetailsOfFood() {
        myRef.child(Common.subCategoryID).child(Common.childItemId).child(Common.indexOfItemInArray).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                try {
                    Picasso.with(getBaseContext()).load(currentFood.getImage())
                            .into(foodimage);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                cooCollapsingToolbarLayout.setTitle(currentFood.getName());
                foodprice.setText(currentFood.getPrice());
                foodname.setText(currentFood.getName());
                fooddescription.setText(" الوصف : " + currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        int x = Integer.parseInt(counterButton.getNumber());
        if (x > 1) {
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.caution_dialog, null);

            builder.setView(view);

            builder.setPositiveButton("موافق", (dialogInterface, i) -> finish());
            builder.setNegativeButton("لا", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.show();

        } else {
            super.onBackPressed();
        }
    }
}
