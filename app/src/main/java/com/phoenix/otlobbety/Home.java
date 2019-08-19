package com.phoenix.otlobbety;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonSyntaxException;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Category;
import com.phoenix.otlobbety.Model.Token;
import com.phoenix.otlobbety.Retrofit.IMyApi;
import com.phoenix.otlobbety.Retrofit.RetrofitClient;
import com.phoenix.otlobbety.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String TAG = "HOME";


    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    TextView txtFullName;
    RecyclerView recyclermenu;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    IMyApi myApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ShimmerLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("منطقة الشيخ زايد");
        toolbar.setNavigationIcon(R.drawable.ic_menu_vector);

        //To change the direction of activity - navigation drawer
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setSupportActionBar(toolbar);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        Paper.init(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        overrideFonts(getBaseContext(), navigationView);

        Retrofit retrofit = RetrofitClient.getInstance();
        myApi = retrofit.create(IMyApi.class);
        shimmerLayout = findViewById(R.id.shimmer_layout);

        //set Name for user
        View headerView = navigationView.getHeaderView(0);
//        txtFullName = headerView.findViewById(R.id.txtFullName);

        //Load menu
        recyclermenu = findViewById(R.id.menu_recyclerview);
        recyclermenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclermenu.setLayoutManager(layoutManager);
        recyclermenu.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Common.isConnectedToInternet(Home.this)) {
                fetchData();
            } else {
                Toast.makeText(Home.this, "Please check your Internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        swipeRefreshLayout.post(() -> {
            if (Common.isConnectedToInternet(Home.this)) {
                fetchData();
            } else {
                Toast.makeText(Home.this, "Please check your Internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        if (Common.isConnectedToInternet(this)) {
            fetchData();
        } else {
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }

//        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Token");
        Token data = new Token(token, false); //false because this token from Client App
//        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void fetchData() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
        Call<Category> call = myApi.getData();
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                try {
                    loadMenu();
                } catch (IllegalStateException | JsonSyntaxException ignored) {
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

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
                overrideFonts(getBaseContext(), menuViewHolder.txtMenuName);

                menuViewHolder.setItemClickListener((view, position, isLongClick) -> {

                    //Get CategoryId and send to new Activity
                    Intent foodList = new Intent(Home.this, SubCategory.class);
                    //Because Category is Key , So We get key of this item
                    foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                    Common.nameOfCategory = adapter.getRef(position).child(category.getName()).getKey();

                    startActivity(foodList);
                });
            }
        };
        adapter.startListening();
        recyclermenu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclermenu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contact) {
            goToFaceBookPage("otlob.Bety2018");

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_discount) {
            showHomeDiscountDialog();
        } else if (id == R.id.nav_log_out) {
            //Delete Remember user & password
            Paper.book().destroy();
            //Logout
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        overrideFonts(getBaseContext(), drawer);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void showHomeDiscountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discount News");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.setting_layout, null);
        CheckBox checkBox = view.findViewById(R.id.subscribeCheckBox);

        //Add Code to remember the state of CheckBox
        Paper.init(this);
        String isSubsribe = Paper.book().read("sub_news");

        if (isSubsribe == null || TextUtils.isEmpty(isSubsribe) || isSubsribe.equals("false")) {
            checkBox.setChecked(false);

        } else {
            checkBox.setChecked(true);
        }

        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();

            if (checkBox.isChecked()) {
                FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                //Write Value
                Paper.book().write("sub_news", "true");

            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                //Write Value
                Paper.book().write("sub_news", "false");
            }

        });

        builder.show();

    }

    private void goToFaceBookPage(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + id));
            startActivity(intent);
        }
    }

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
