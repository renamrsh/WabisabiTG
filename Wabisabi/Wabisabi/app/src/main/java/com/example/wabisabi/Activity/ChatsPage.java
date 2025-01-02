package com.example.wabisabi.Activity;


import static com.example.wabisabi.Adapter.GroupsAdapter.CHATS_ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Activity.chats.ChatsAdapter;
import com.example.wabisabi.Activity.chats.ChatsDomain;
import com.example.wabisabi.Adapter.GroupsAdapter;
import com.example.wabisabi.Domain.GroupsDomain;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityChatsPageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsPage extends BaseActivity {
    private RecyclerView.Adapter adapterGroupsList;
    private ActivityChatsPageBinding binding;
    ArrayList<GroupsDomain> items_groups;
    FirebaseDatabase db;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadChats();

        onCreateDrawer();
        initRecyclerViewG();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.chatsRv.setLayoutManager(layoutManager);

        View addFriendLayout = getLayoutInflater().inflate(R.layout.add_new_friend, null);
        CircleImageView addFriend = addFriendLayout.findViewById(R.id.add_friend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsPage.this, FindPerson.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);           }
        });
        CircleImageView findGroup = findViewById(R.id.find_group);
        findGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsPage.this, FindGroup.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        CircleImageView createGroup = findViewById(R.id.create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsPage.this, CreateGroup.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //CircleImageView createGroup = addFriendLayout.findViewById(R.id.create_group);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_chat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_chat) {
                return true;
            } else if (itemId == R.id.bottom_expert) {
                startActivity(new Intent(ChatsPage.this, FindExpert.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_info) {
                startActivity(new Intent(ChatsPage.this, FindInfo.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(ChatsPage.this, Home.class ));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ChatsPage.this, Home.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    private void loadChats() {
        ArrayList<ChatsDomain> chats = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot userSnapshot = snapshot.child("Users").child(uid);
                    if (userSnapshot.exists()) {
                        DataSnapshot chatsSnapshot = userSnapshot.child("chats");
                        if (chatsSnapshot.exists()) {
                            String chatsStr = chatsSnapshot.getValue(String.class);
                            if (chatsStr != null && !chatsStr.isEmpty()) {
                                String[] chatsIds = chatsStr.split(",");
                                for (String chatId : chatsIds) {
                                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                                    if (chatSnapshot.exists()) {
                                        String userId1 = Objects.requireNonNull(chatSnapshot.child("user1").getValue(String.class));
                                        String userId2 = Objects.requireNonNull(chatSnapshot.child("user2").getValue(String.class));
                                        String chatUserId = (uid.equals(userId1)) ? userId2 : userId1;
                                        DataSnapshot chatUserSnapshot = snapshot.child("Users").child(chatUserId);
                                        String notCurrentUserId = Objects.requireNonNull(chatUserId);
                                        if (chatUserSnapshot.exists()) {
                                            String chatName = Objects.requireNonNull(chatUserSnapshot.child("username").getValue(String.class));
                                            String chatImage = Objects.requireNonNull(chatUserSnapshot.child("profileImage").getValue(String.class));
                                            ChatsDomain chat = new ChatsDomain(chatId, chatName, userId1, userId2, chatImage, notCurrentUserId);
                                            chats.add(chat);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Set the adapter after loading the data
                    binding.chatsRv.setAdapter(new ChatsAdapter(chats));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatsPage.this, "Failed to get user chats", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initRecyclerViewG() {
        RecyclerView recyclerViewGroups = findViewById(R.id.groups_rv);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        items_groups = new ArrayList<>();
        adapterGroupsList = new GroupsAdapter(CHATS_ACTIVITY, ChatsPage.this, items_groups);
        recyclerViewGroups.setAdapter(adapterGroupsList);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Groups");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items_groups.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupsDomain info = snapshot.getValue(GroupsDomain.class);
                    if (info != null) {
                        long timestamp = Long.parseLong(String.valueOf(info.getTimestamp()));
                        info.setTimestamp(timestamp); // Set the converted value

                        // Retrieve participant IDs from the database
                        List<String> participantIds = new ArrayList<>();
                        DataSnapshot participantsSnapshot = snapshot.child("Participants");
                        for (DataSnapshot participantSnapshot : participantsSnapshot.getChildren()) {
                            String participantId = participantSnapshot.getKey();
                            participantIds.add(participantId);
                        }
                        info.setParticipantIds(participantIds);
                        if (participantIds.contains(currentUser.getUid())) {

                            items_groups.add(info);
                        }
                    }
                }
                adapterGroupsList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatsPage.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

    }

}