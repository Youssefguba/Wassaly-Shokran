package com.phoenix.otlobbety;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.ornach.nobobutton.NoboButton;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.DataMessage;
import com.phoenix.otlobbety.Model.MyResponse;
import com.phoenix.otlobbety.Model.Request;
import com.phoenix.otlobbety.Model.Token;
import com.phoenix.otlobbety.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderRequest extends AppCompatActivity {

    MaterialEditText nameOfCustomer, areaOfCustomer, addressOfCustomer, streetOfCustomer,
            buildingOfCustomer, floorOfCustomer, apartmentOfCustomer,
            additionalInfoAboutCustomer, phoneNumberOfCustomer;
    NoboButton confirmOrderButton;
    ActionBar actionBar;

    FirebaseDatabase database;
    DatabaseReference requestRef;

    APIService mApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("أكمل حجز الطلب");
        setSupportActionBar(toolbar);

        //FirebaseInit
        database = FirebaseDatabase.getInstance();
        requestRef = database.getReference("Requests");

        mApiService = Common.getFCMService();


        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //Init views
        nameOfCustomer = findViewById(R.id.name_of_customer);
        areaOfCustomer = findViewById(R.id.area_of_customer);
        addressOfCustomer = findViewById(R.id.address_of_customer);
        streetOfCustomer = findViewById(R.id.street_of_customer);
        buildingOfCustomer = findViewById(R.id.building_of_customer);
        floorOfCustomer = findViewById(R.id.floor_of_customer);
        apartmentOfCustomer = findViewById(R.id.apartment_of_customer);
        additionalInfoAboutCustomer = findViewById(R.id.additional_info_of_customer);
        phoneNumberOfCustomer = findViewById(R.id.phone_number_of_customer);

        confirmOrderButton = findViewById(R.id.confirm_order);
        confirmOrderButton.setOnClickListener(view -> {
            confirmOrder();
        });
    }

    private void confirmOrder() {
        final String name_Of_Customer = nameOfCustomer.getText().toString().trim();
        final String area_Of_Customer = areaOfCustomer.getText().toString().trim();
        final String address_Of_Customer = addressOfCustomer.getText().toString().trim();
        final String street_Of_Customer = streetOfCustomer.getText().toString().trim();
        final String building_Of_Customer = buildingOfCustomer.getText().toString().trim();
        final String floor_Of_Customer = floorOfCustomer.getText().toString().trim();
        final String apartment_Of_Customer = apartmentOfCustomer.getText().toString().trim();
        final String phoneNumber_Of_Customer = phoneNumberOfCustomer.getText().toString().trim();


        if (name_Of_Customer.isEmpty()) {
            nameOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();
            return;
        }

        if (area_Of_Customer.isEmpty()) {
            areaOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();

            return;
        }


        if (address_Of_Customer.isEmpty()) {
            addressOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();

            return;
        }

        if (street_Of_Customer.isEmpty()) {
            streetOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();

            return;
        }

        if (building_Of_Customer.isEmpty()) {
            buildingOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();

            return;
        }

        if (floor_Of_Customer.isEmpty()) {
            floorOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();

            return;
        }

        if (apartment_Of_Customer.isEmpty()) {
            apartmentOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();
            return;
        }
        if (phoneNumber_Of_Customer.isEmpty()) {
            phoneNumberOfCustomer.requestFocus();
            Toasty.error(this, "أكمل البيانات الناقصة ", Toast.LENGTH_LONG, false).show();
            return;
        }

        //                Create new Request
        Request request = new Request(
                nameOfCustomer.getText().toString(),
                areaOfCustomer.getText().toString(),
                addressOfCustomer.getText().toString(),
                streetOfCustomer.getText().toString(),
                buildingOfCustomer.getText().toString(),
                floorOfCustomer.getText().toString(),
                apartmentOfCustomer.getText().toString(),
                additionalInfoAboutCustomer.getText().toString(),
                phoneNumberOfCustomer.getText().toString(),
                Paper.book().read(Common.TOTAL_PRICE),
                "0", //Status
                Common.listOfCart,
                Paper.book().read(Common.DELIVERY_COST)
        );

//                Submit to Firebase
//                We will using System.CurrentMill to key
        String order_number = String.valueOf(System.currentTimeMillis());
        requestRef.child(order_number).setValue(request);
        updateToken(FirebaseInstanceId.getInstance().getToken());


        //Delete Cart
        new Database(getApplicationContext()).cleanCart();
        finish();
        Intent intent = new Intent(ConfirmOrderRequest.this, ItemsList.class);
        startActivity(intent);

        // Save number phone in Paper to show orders according to specific phone number and send
        // notification to specific user not to all
        Paper.book().write(Common.PHONE_TEXT, phoneNumberOfCustomer.getText().toString());

        sendNotificationOrder(order_number);
        Toasty.success(ConfirmOrderRequest.this, "تم إرسال طلب .. وجاري التحضير", Toast.LENGTH_SHORT).show();

    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Token");
        Token data = new Token(token, false); //false because this token from Client App
        tokens.child(phoneNumberOfCustomer.getText().toString()).setValue(data);
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query data = tokens.orderByChild("serverToken").equalTo(true); //get all node with "isServerToken"
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);
//
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
                                    Toasty.success(ConfirmOrderRequest.this, "تم إرسال طلب .. وجاري التحضير", Toast.LENGTH_SHORT).show();
                                    new Database(getBaseContext()).cleanCart();
                                    finish();

                                    if (response.body().success == 1) {
                                        Toasty.success(ConfirmOrderRequest.this, "تم إرسال طلب .. وجاري التحضير", Toast.LENGTH_SHORT).show();
                                        new Database(getBaseContext()).cleanCart();
                                        finish();
                                    } else {
                                        Toasty.error(ConfirmOrderRequest.this, "فشل إرسال الطلب .. أعد المحاولة !", Toast.LENGTH_SHORT).show();
                                    }
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

}
