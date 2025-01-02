package com.example.wabisabi.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Activity.users.User;
import com.example.wabisabi.Adapter.AdapterParticipantsAdd;
import com.example.wabisabi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantsActivity extends AppCompatActivity {

    private RecyclerView usersRv;

    private FirebaseAuth firebaseAuth;
    private String groupId;
    private String myGroupRole;
    private ArrayList<User> users;
    private AdapterParticipantsAdd adapterParticipantsAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants);



        firebaseAuth = FirebaseAuth.getInstance();
        usersRv = findViewById(R.id.usersRvParticipants);
        groupId = getIntent().getStringExtra("groupId");



        loadGroupInfo();
        getAllUsers();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(GroupParticipantsActivity.this, ChatsPage.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void getAllUsers() {
        users = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                adapterParticipantsAdd = new AdapterParticipantsAdd(GroupParticipantsActivity.this, users, "" + groupId, "" + myGroupRole);
                usersRv.setLayoutManager(new LinearLayoutManager(GroupParticipantsActivity.this));
                usersRv.setAdapter(adapterParticipantsAdd);
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getKey();  // Получаем UID пользователя
                    String username = "" + ds.child("username").getValue();
                    String profileImage = "" + ds.child("profileImage").getValue();

                    User user = new User();
                    user.setUid(uid);
                    user.setUsername(username);
                    user.setProfileImage(profileImage);

                    if(!firebaseAuth.getUid().equals(user.getUid())){
                        users.add(user);
                    }
                }
                adapterParticipantsAdd = new AdapterParticipantsAdd(GroupParticipantsActivity.this, users, "" + groupId, "" + myGroupRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок
            }
        });
    }
    private void loadGroupInfo() {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        // Check if groupId and firebaseAuth.getUid() are not null
        if (groupId != null && firebaseAuth.getUid() != null) {
            ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            myGroupRole = "" + snapshot.child("role").getValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    public void onButtonBack(View view) {
        startActivity(new Intent(GroupParticipantsActivity.this, ChatsPage.class));
        finish();
        ((Activity) this).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}