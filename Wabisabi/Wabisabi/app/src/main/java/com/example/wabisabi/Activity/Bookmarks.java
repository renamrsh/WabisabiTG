package com.example.wabisabi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Adapter.InfoAdapter;
import com.example.wabisabi.Domain.InformationDomain;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityBookmarksBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Bookmarks extends BaseActivity {
    ArrayList<InformationDomain> items_saved;// the same as bookmarked
    InfoAdapter adapterInfoList;
    SearchView searchView;
    ActivityBookmarksBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        binding = ActivityBookmarksBinding.inflate(getLayoutInflater());


        onCreateDrawer();
        //onSearchBarClicked(R.id.search_bar_bookmarks);

        RecyclerView recyclerViewInfo = findViewById(R.id.view4);
        recyclerViewInfo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));





        items_saved = new ArrayList<>();
        adapterInfoList = new InfoAdapter(Bookmarks.this, items_saved);
        recyclerViewInfo.setAdapter(adapterInfoList);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Info");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items_saved.clear();
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformationDomain bookmark = snapshot.getValue(InformationDomain.class);
                    if (bookmark != null && bookmark.getSavedByUsers() != null && bookmark.getSavedByUsers().contains(currentUserId)) {
                        items_saved.add(bookmark);
                        Log.d("BookmarkAdded", "ID: " + snapshot.getKey());
                    }
                }
                adapterInfoList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Bookmarks.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });




/*

        searchView = findViewById(R.id.search_bar_bookmarks);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });
*/

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_expert) {
                startActivity(new Intent(Bookmarks.this, FindExpert.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(Bookmarks.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_info) {
                startActivity(new Intent(Bookmarks.this, FindInfo.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(Bookmarks.this, ChatsPage.class ));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });



        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(Bookmarks.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }
/*
    private void searchList(String text){
        ArrayList<InformationDomain> dataSearchList = new ArrayList<>();
        for (InformationDomain data : items_saved){
            if(data.getTitle().toLowerCase().contains(text.toLowerCase())){
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()){
            // Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        }else{
            adapterInfoList.setInfoSearchList(dataSearchList);
        }
    }

*/

}
