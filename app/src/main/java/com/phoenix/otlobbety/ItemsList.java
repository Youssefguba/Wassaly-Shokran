package com.phoenix.otlobbety;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsList extends AppCompatActivity {

    //TODO Photo and Name of Restaurant
    //TODO Get Intent - categoryId
    //TODO Make a children clickable

    View actionView;
    TextView textCartItemCount;
    ImageView subCategoryImage;
    List<String> listDataHeader;
    HashMap<String, ArrayList> listDataChild;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ActionBar actionBar;

    ExpandableAdapter expandableAdapter;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.nameOfSubCategory);
        setSupportActionBar(toolbar);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_list, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge();

//      actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

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

    private void SetStandardGroups() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();


        myRef.child("pharmacy").addChildEventListener(new ChildEventListener() {
            int counter = 0;
            ArrayList childItem;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                listDataHeader.add(dataSnapshot.getKey());
                childItem = new ArrayList();

                //This " For each " loop to search for values in node and get the value of key that called " name "
                // and put it in Child of parent.

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, Food> childNames = (HashMap<String, Food>) ds.getValue();
                    Log.e("TAG", "childNames :" + childNames.values());

                    childItem.add(childNames.get("name"));
                    Log.e("TAG", "childNames :" + childNames.get("name"));
                }

                listDataChild.put(listDataHeader.get(counter), childItem);
                counter++;

                Log.e("TAG", "counter :" + counter);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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

}
