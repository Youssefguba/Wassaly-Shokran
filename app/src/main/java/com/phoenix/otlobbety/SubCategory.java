package com.phoenix.otlobbety;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.JsonSyntaxException;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Category;
import com.phoenix.otlobbety.Retrofit.IMyApi;
import com.phoenix.otlobbety.Retrofit.RetrofitClient;
import com.phoenix.otlobbety.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubCategory extends AppCompatActivity {

    public final static String TAG = "SUBCATEGORY";
    public String path = "";
    FirebaseDatabase database;
    DatabaseReference subCategoryRef;
    RecyclerView recyclermenu;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    ShimmerLayout shimmerLayout;
    String categoryId = "";
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    ActionBar actionBar;
    IMyApi myApi;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        database = FirebaseDatabase.getInstance();
        subCategoryRef = database.getReference("SubCategory");

        Toolbar toolbar = findViewById(R.id.toolbarOfSubCategory);
        toolbar.setTitle(Common.nameOfCategory);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        recyclermenu = findViewById(R.id.menu_recyclerview);
        recyclermenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclermenu.setLayoutManager(layoutManager);

        Retrofit retrofit = RetrofitClient.getInstance();
        myApi = retrofit.create(IMyApi.class);
        shimmerLayout = findViewById(R.id.shimmer_layout);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Common.isConnectedToInternet(SubCategory.this)) {
                fetchData(categoryId);
            } else {
                Toast.makeText(SubCategory.this, "Please check your Internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        swipeRefreshLayout.post(() -> {
            if (Common.isConnectedToInternet(SubCategory.this)) {
                fetchData(categoryId);
            } else {
                Toast.makeText(SubCategory.this, "Please check your Internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        //  Get Intent here
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                fetchData(categoryId);
            } else {
                Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    private void fetchData(String categoryId) {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();

        Call<Category> call = myApi.getSubCategoryData();
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                try {
                    loadSubCategory(categoryId);
                } catch (IllegalStateException | JsonSyntaxException ignored) {
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    private void loadSubCategory(String categoryId) {
        Query retrieveData = subCategoryRef.child(categoryId).orderByChild("categoryId").equalTo(categoryId);
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(retrieveData, Category.class)
                .build();

        subCategoryRef.child(categoryId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HashMap list = (HashMap) dataSnapshot.getValue();
                Log.e("SubCategory", String.valueOf(list.get("name")));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i, @NonNull Category category) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage()).into(menuViewHolder.imageView);
                menuViewHolder.setItemClickListener((view, position, isLongClick) -> {
                    Intent itemListAct = new Intent(SubCategory.this, ItemsList.class);
                    itemListAct.putExtra("SubCategoryId", adapter.getRef(position).getKey());

                    //to set name of subcategory in Public String to pass it between this activity and itemList Activity to
                    //get the name of Card item and set it as a toolbar title in ItemsList Activity.

                    Common.nameOfSubCategory = adapter.getRef(position).child(categoryId).child(category.getName()).getKey().toString();
//                    Uri uri = Uri.parse(adapter.getRef(position).child(categoryId).child(category.getImage()).getKey());
                    Log.e("SubCategory", String.valueOf(Uri.parse(String.valueOf(adapter.getRef(position).child(categoryId).child(category.getImage())))));
//        Log.e("SubCategory", String.valueOf(adapter.getRef(position).child(categoryId).child(String.valueOf(String.valueOf(category.getImage()))).toString()));

                    startActivity(itemListAct);
                });
            }
        };

        adapter.startListening();
        recyclermenu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);

    }
}
