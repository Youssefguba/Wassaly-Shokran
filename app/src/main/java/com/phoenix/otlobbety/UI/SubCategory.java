package com.phoenix.otlobbety.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.JsonSyntaxException;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Category;
import com.phoenix.otlobbety.R;
import com.phoenix.otlobbety.Retrofit.IMyApi;
import com.phoenix.otlobbety.Retrofit.RetrofitClient;
import com.phoenix.otlobbety.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubCategory extends AppCompatActivity {

    public final static String TAG = "SubCategory";
    FirebaseDatabase database;
    DatabaseReference subCategoryRef;
    DatabaseReference categoryRef;
    RecyclerView recyclermenu;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    ShimmerLayout shimmerLayout;
    String categoryId = "";
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    ActionBar actionBar;
    IMyApi myApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        database = FirebaseDatabase.getInstance();
        subCategoryRef = database.getReference("SubCategory");
        categoryRef = database.getReference("Category");

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

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_of_subcategory, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i, @NonNull Category category) {
                menuViewHolder.txtMenuName.setText(category.getName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    overrideFonts(getBaseContext(), menuViewHolder.txtMenuName);
                }

                menuViewHolder.deliveryCost.setText(category.getDeliveryCost() + " ج.م ");
                menuViewHolder.deliveryTime.setText(category.getDeliveryTime() + " دقيقة ");
                Picasso.with(getBaseContext()).load(category.getImage()).into(menuViewHolder.imageView);
                menuViewHolder.setItemClickListener((view, position, isLongClick) -> {

                    Intent itemListAct = new Intent(SubCategory.this, ItemsList.class);
                    itemListAct.putExtra("SubCategoryId", adapter.getRef(position).getKey());

                    //To Get name and Image of Cardview then pass it to ItemsList Activity..
                    Common.nameOfSubCategory = adapter.getRef(position).child(categoryId).child(category.getName()).getKey();
                    Common.imgOfSubCategory = category.getImage();
                    Common.subCategoryID = adapter.getRef(position).getKey();
                    Log.e(TAG, adapter.getRef(position).getKey());

                    // Save Cost of selected item in Paper to retrieve it in CART Activity ..
                    Paper.book().write(Common.DELIVERY_COST, category.getDeliveryCost());
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "stcregular.ttf"));
            }
        } catch (Exception e) {
        }
    }
}
