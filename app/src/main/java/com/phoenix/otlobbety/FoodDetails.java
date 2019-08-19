package com.phoenix.otlobbety;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Food;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodDetails extends AppCompatActivity {
    public static final String TAG = "FoodDetails";
    TextView foodname, foodprice, fooddescription, fooddiscount;
    ImageView foodimage;
    CollapsingToolbarLayout cooCollapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    List<String> listDataHeader;
    HashMap<String, ArrayList> listDataChild;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference myRef;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase
        //Firebase Initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ExpandableList");

        //InitView
        numberButton = findViewById(R.id.number_button);

//        btnCart.setOnClickListener(v -> {
//            new Database(getBaseContext()).addToCart(new Order(
//                    foodId,
//                    currentFood.getName(),
//                    numberButton.getNumber(),
//                    currentFood.getPrice(),
//                    currentFood.getDiscount()
////            ));
//            Toast.makeText(FoodDetails.this, "Added to Cart", Toast.LENGTH_SHORT).show();
//            finish();
//
//        });

        fooddescription = findViewById(R.id.food_description);
        foodname = findViewById(R.id.food_name);
        foodprice = findViewById(R.id.food_price);
        fooddiscount = findViewById(R.id.food_discount);
        foodimage = findViewById(R.id.img_food);

        cooCollapsingToolbarLayout = findViewById(R.id.collapsing);
        cooCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsAppbar);
        cooCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsAppbar);

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
                fooddiscount.setPaintFlags(fooddiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                fooddiscount.setText(currentFood.getDiscount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
