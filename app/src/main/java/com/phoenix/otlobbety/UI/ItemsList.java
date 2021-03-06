package com.phoenix.otlobbety.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phoenix.otlobbety.Adapter.ExpandableAdapter;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.Food;
import com.phoenix.otlobbety.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsList extends AppCompatActivity {
    public static final String TAG = "ItemsListTest";
    View actionView;
    TextView textCartItemCount;
    ImageView subCategoryImage;
    List<String> listDataHeader;
    HashMap<String, List> listDataChild;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ActionBar actionBar;
    String itemId;
    Food childNames;

    ExpandableAdapter expandableAdapter;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.nameOfSubCategory);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);

        // show The Image in a ImageView
        new DownloadImageTask(findViewById(R.id.restaurant_img))
                .execute(Common.imgOfSubCategory);

        //Firebase Initialize
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ExpandableList");
        subCategoryImage = findViewById(R.id.restaurant_img);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_black_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        expandableListView = findViewById(R.id.exp_list);
        SetStandardGroups();
        expandableAdapter = new ExpandableAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(expandableAdapter);
        expandableListView.setOnGroupClickListener((expandableListView, view, groupPosition, childPosition) -> {
            setListViewHeight(expandableListView, groupPosition);
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_list, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();
        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (new Database(this).getCountCarts() == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(new Database(this).getCountCarts()));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                Intent cartIntent = new Intent(this, Cart.class);
                startActivity(cartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
    }

    private void SetStandardGroups() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        myRef.child(Common.subCategoryID).addChildEventListener(new ChildEventListener() {
            int counter = 0;
            List<String> childItem;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                listDataHeader.add(dataSnapshot.getKey());
                childItem = new ArrayList();

                //This " For each " loop to search for values in node and get the value of key that called " name "
                // and put it in child of parent.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    childNames =  ds.getValue(Food.class);
                    childItem.add(String.valueOf(childNames.getName()));
                }

                listDataChild.put(listDataHeader.get(counter), childItem);
                counter++;

                // Make child item clickable..
                expandableListView.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, id) -> {
                    Common.childItemId = listDataHeader.get(groupPosition);

                    //To Intent for Food Details Activity when click on Child item ..
                    Intent childIntent = new Intent(ItemsList.this, FoodDetails.class);
                    itemId = (String) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                    /*
                     * Store the name of Array List in Common.childItemId to retrieve all of List.
                     * Store the index of List in Common.indexOfItemInArray to show the correct ,
                     * data in Food details activity when clicked on child item.
                     *
                     */
                    Common.indexOfItemInArray = itemId;
                    childIntent.putExtra("childItemID", itemId);
                    startActivity(childIntent);
                    return false;
                });

                expandableAdapter.notifyDataSetChanged();
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
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        if (new Database(this).getCountCarts() > 0) {
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);

            View view = inflater.inflate(R.layout.dialog_item_list, null);
            builder.setView(view);

            builder.setPositiveButton("موافق", (dialogInterface, i) -> {
                new Database(getBaseContext()).cleanCart();
                finish();
            });

            builder.setNegativeButton("لا", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();

        } else {
            finish();
            Intent intent = new Intent(ItemsList.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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

    // To make Expandable List Scroll in NestedScrollView
    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
                //Add Divider Height
                totalHeight += listView.getDividerHeight() * (listAdapter.getChildrenCount(i) - 1);
            }
        }
        //Add Divider Height
        totalHeight += listView.getDividerHeight() * (listAdapter.getGroupCount() - 1);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}