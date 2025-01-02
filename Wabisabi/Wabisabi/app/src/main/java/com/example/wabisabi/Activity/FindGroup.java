package com.example.wabisabi.Activity;

import static com.example.wabisabi.Adapter.GroupsAdapter.CHATS_ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wabisabi.Adapter.GroupsAdapter;
import com.example.wabisabi.Domain.GroupsDomain;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityFindGroupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindGroup extends BaseActivity implements SearchView.OnQueryTextListener, GroupsAdapter.OnGroupClickListener  {
     ActivityFindGroupBinding binding;
     ArrayList<GroupsDomain> groups = new ArrayList<>();
     ArrayList<GroupsDomain> filterGroups = new ArrayList<>();
     GroupsAdapter groupsAdapter;
    private static final String TAG = "FindGroup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onCreateDrawer();

        binding.emptyMessageGroup.setVisibility(View.VISIBLE);

        binding.searchBarGroup.setOnQueryTextListener(this);

        displayGroups(new ArrayList<>());

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(FindGroup.this, ChatsPage.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };

        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void addCurrentUserToGroup(String groupId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "Current user ID: " + userId);
            Log.d(TAG, "Group ID: " + groupId);

            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference()
                    .child("Groups")
                    .child(groupId)
                    .child("Participants")
                    .child(userId);

            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("role", "participant"); // Set the user's role
                        userData.put("timestamp", ServerValue.TIMESTAMP); // Set the timestamp

                        groupRef.setValue(userData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User added successfully to group");
                                    } else {
                                        Log.e(TAG, "Failed to add user to group: " + task.getException().getMessage());
                                    }
                                });
                    } else {
                        Log.d(TAG, "User is already a participant in this group");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else {
            Log.e(TAG, "Current user is null");
        }
    }


    @Override
    public void onGroupClick(String groupId) {
        addCurrentUserToGroup(groupId);
    }

    private void loadGroups(String query) {
        groups.clear();
        FirebaseDatabase.getInstance().getReference().child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String groupId = groupSnapshot.child("groupId").getValue(String.class);
                    String groupTitle = groupSnapshot.child("groupTitle").getValue(String.class);
                    String groupDescription = groupSnapshot.child("groupDescription").getValue(String.class);
                    String groupIcon = groupSnapshot.child("groupIcon").getValue(String.class);

                    if (groupId != null && groupTitle != null && groupDescription != null && groupIcon != null &&
                            groupTitle.toLowerCase().contains(query)) {
                        groups.add(new GroupsDomain(groupId, groupTitle, groupDescription, groupIcon));
                    }
                }
                displayGroups(groups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void displayGroups(ArrayList<GroupsDomain> groups) {
        groupsAdapter = new GroupsAdapter(CHATS_ACTIVITY, this, groups, this);
        binding.groupsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.groupsRv.setAdapter(groupsAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        String query = newText.trim().toLowerCase();
        if (TextUtils.isEmpty(query)) {
            groups.clear();
            displayGroups(groups);
            binding.emptyMessageGroup.setVisibility(View.VISIBLE);
        } else {
            binding.emptyMessageGroup.setVisibility(View.GONE);
            loadGroups(query);
        }
        return true;
    }


    public void onButtonBack(View view) {
        Intent intent = new Intent(FindGroup.this, ChatsPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities above ChatsPage because of errors
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}

