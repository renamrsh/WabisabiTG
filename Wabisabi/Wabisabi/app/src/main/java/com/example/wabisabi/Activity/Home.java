package com.example.wabisabi.Activity;

import static com.example.wabisabi.Adapter.GroupsAdapter.HOME_ACTIVITY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Adapter.BulletpointsAdapter;
import com.example.wabisabi.Adapter.GroupsAdapter;
import com.example.wabisabi.Domain.GroupsDomain;
import com.example.wabisabi.Domain.InformationDomain;
import com.example.wabisabi.Domain.QuoteDomain;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Home extends BaseActivity {

    private RecyclerView.Adapter adapterBulletpointsList, adapterGroupsList;

    ArrayList<InformationDomain> items_bp;
    ArrayList<GroupsDomain> items_groups;
    ActivityHomeBinding binding;
    FirebaseDatabase db;
    DatabaseReference reference;
    private TextView quoteTextView;
    private TextView quoteAuthorTextView;
    private static final String SHARED_PREFS_KEY = "quote_last_loaded";
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000; // 24 hours


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(Home.this,Login.class));
            finish();
            return;
        }

        onCreateDrawer();
        //onSearchBarClicked(R.id.search_bar_home);

        quoteTextView = findViewById(R.id.quoteTxt);
        quoteAuthorTextView = findViewById(R.id.quoteAuthorTxt);

        fetchRandomQuote();

        initRecyclerViewBP();
        initRecyclerViewG();
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (t.getName().equals("FinalizerWatchdogDaemon") && e instanceof TimeoutException) {
                } else {
                    defaultUncaughtExceptionHandler.uncaughtException(t, e);
                }
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_expert) {
                startActivity(new Intent(Home.this, FindExpert.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_info) {
                startActivity(new Intent(Home.this, FindInfo.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_chat) {
                startActivity(new Intent(Home.this, ChatsPage.class ));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(Home.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    public void onSeemoreBPClicked(View view) {
        startActivity(new Intent(Home.this, FindInfo.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void onSeemoreGClicked(View view) {

        startActivity(new Intent(Home.this, FindGroup.class ));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initRecyclerViewBP() {
        RecyclerView recyclerViewBulletpoints = findViewById(R.id.view1);
        recyclerViewBulletpoints.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        items_bp = new ArrayList<>();
        adapterBulletpointsList = new BulletpointsAdapter(Home.this, items_bp);
        recyclerViewBulletpoints.setAdapter(adapterBulletpointsList);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Info");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items_bp.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformationDomain info = snapshot.getValue(InformationDomain.class);
                    if (info != null) {
                        //long timestamp = info.getTimestamp();
                        //info.setTimestamp(timestamp);
                        items_bp.add(info);

                    }
                }
                adapterBulletpointsList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initRecyclerViewG() {
        RecyclerView recyclerViewGroups = findViewById(R.id.view2);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items_groups = new ArrayList<>();
        // Initialize the adapter with activity type
        adapterGroupsList = new GroupsAdapter(HOME_ACTIVITY, Home.this, items_groups);
        recyclerViewGroups.setAdapter(adapterGroupsList);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Groups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items_groups.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupsDomain info = snapshot.getValue(GroupsDomain.class);
                    if (info != null) {
                        List<String> participantIds = new ArrayList<>();
                        DataSnapshot participantsSnapshot = snapshot.child("Participants");
                        for (DataSnapshot participantSnapshot : participantsSnapshot.getChildren()) {
                            String participantId = participantSnapshot.getKey();
                            participantIds.add(participantId);
                        }
                        info.setParticipantCount(participantIds.size());
                        items_groups.add(info);
                    }
                }
                adapterGroupsList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void loadQuote() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        long lastLoadedTime = sharedPreferences.getLong(SHARED_PREFS_KEY, 0);

        if (System.currentTimeMillis() - lastLoadedTime > DAY_IN_MILLIS) {
            fetchRandomQuote();
        }
    }

    private void fetchRandomQuote() {
        FirebaseDatabase.getInstance().getReference("Quotes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<QuoteDomain> quotes = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            QuoteDomain quote = snapshot.getValue(QuoteDomain.class);
                            if (quote != null) {
                                quotes.add(quote);
                            }
                        }

                        if (!quotes.isEmpty()) {
                            Random random = new Random();
                            QuoteDomain randomQuote = quotes.get(random.nextInt(quotes.size()));

                            quoteTextView.setText(randomQuote.getQuote());
                            quoteAuthorTextView.setText(randomQuote.getAuthor());

                            saveQuoteLoadTime();
                        } else {
                            quoteTextView.setText("No quotes available");
                            quoteAuthorTextView.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                        quoteTextView.setText("Failed to retrieve quote");
                        quoteAuthorTextView.setText("");
                    }
                });
    }

    private void saveQuoteLoadTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SHARED_PREFS_KEY, System.currentTimeMillis());
        editor.apply();
    }

    private void sortBulletpoints() {
        Collections.sort(items_bp, new Comparator<InformationDomain>() {
            @Override
            public int compare(InformationDomain o1, InformationDomain o2) {
                return Integer.compare(o2.getLikesNumber(), o1.getLikesNumber());
            }
        });
        adapterBulletpointsList.notifyDataSetChanged();
    }
    private void Status(String status){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        FirebaseDatabase.getInstance().getReference().updateChildren(hashMap);
    }
}
