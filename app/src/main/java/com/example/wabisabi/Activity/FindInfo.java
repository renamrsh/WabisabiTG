package com.example.wabisabi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Adapter.InfoAdapter;
import com.example.wabisabi.Domain.InformationDomain;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityFindInfoBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindInfo extends BaseActivity {
    ArrayList<InformationDomain> items_info;
    InfoAdapter adapterInfoList;
    SearchView searchView;
    ActivityFindInfoBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);
        binding = ActivityFindInfoBinding.inflate(getLayoutInflater());

        /*
        InfoDomain first = new InfoDomain("Another name", "disiese3", "Description");
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("info");
        reference.child("2").setValue(first).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FindInfo.this, "SUCCESS",Toast.LENGTH_SHORT).show();
            }
        });*/

        onCreateDrawer();
        onSearchBarClicked(R.id.search_bar_info);


        RecyclerView recyclerViewInfo = findViewById(R.id.view3);
        recyclerViewInfo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        items_info = new ArrayList<>();
        adapterInfoList = new InfoAdapter(FindInfo.this, items_info);
        recyclerViewInfo.setAdapter(adapterInfoList);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Info");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items_info.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformationDomain info = snapshot.getValue(InformationDomain.class);
                    if (info != null) {
                        items_info.add(info);
                    }
                }
                adapterInfoList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindInfo.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });




        searchView = findViewById(R.id.search_bar_info);
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

        /*items_info = new ArrayList<>();

        items_info.add(new InfoDomain("Name", "disiese2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        items_info.add(new InfoDomain("Another name", "disiese3", "Description"));
        items_info.add(new InfoDomain("Heart", "disiese6", "Description"));
        items_info.add(new InfoDomain("Knee", "disiese2", "Description"));
        items_info.add(new InfoDomain("Disease name will be here", "disiese3", "Description"));
        items_info.add(new InfoDomain("Another one disease name", "disiese6", "Description"));

        adapterInfoList = new InfoAdapter(FindInfo.this, items_info);
        recyclerViewInfo.setAdapter(adapterInfoList);
*/


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_info);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_info) {
                return true;
            } else if (itemId == R.id.bottom_expert) {
                startActivity(new Intent(FindInfo.this, FindExpert.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(FindInfo.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(FindInfo.this, ChatsPage.class ));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(FindInfo.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void searchList(String text){
        ArrayList<InformationDomain> dataSearchList = new ArrayList<>();
        for (InformationDomain data : items_info){
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
}