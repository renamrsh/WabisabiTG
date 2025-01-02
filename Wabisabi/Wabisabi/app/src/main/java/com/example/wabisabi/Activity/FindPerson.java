package com.example.wabisabi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wabisabi.Activity.chats.ChatsDomain;
import com.example.wabisabi.Activity.users.User;
import com.example.wabisabi.Activity.users.UsersAdapter;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityFindPersonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindPerson extends BaseActivity implements SearchView.OnQueryTextListener {
    private ActivityFindPersonBinding binding;
    private UsersAdapter usersAdapter;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> filterUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onCreateDrawer();

        binding.emptyMessage.setVisibility(View.VISIBLE);

        binding.searchBarPerson.setOnQueryTextListener(this);

        displayUsers(new ArrayList<>());

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(FindPerson.this, ChatsPage.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void loadUsers(String query) {
        users.clear();
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                continue;
                            }
                            String username = userSnapshot.child("username").getValue(String.class);
                            String profileImage = userSnapshot.child("profileImage").getValue(String.class);

                            if (username != null && profileImage != null && username.toLowerCase().contains(query)) {
                                users.add(new User(uid, username, profileImage));
                            }
                        }
                        displayUsers(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FindPerson.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void displayUsers(ArrayList<User> users) {
        usersAdapter = new UsersAdapter(users, FindPerson.this::createChat);
        binding.usersRv.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRv.setAdapter(usersAdapter);
    }




    private void createChat(User user) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String chatName = user.getUsername();

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .orderByChild("user1")
                .equalTo(currentUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean chatExists = false;

                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String user2 = chatSnapshot.child("user2").getValue(String.class);
                            if (user2 != null && user2.equals(user.getUid())) {
                                // Чат уже существует между пользователями, показать сообщение и завершить метод
                                Toast.makeText(FindPerson.this, "Chat with this user already exists", Toast.LENGTH_SHORT).show();
                                chatExists = true;
                                break;
                            }
                        }

                        if (!chatExists) {
                            // Проверяем существование чата с пользователем в качестве user2 и текущим пользователем в качестве user1
                            FirebaseDatabase.getInstance().getReference().child("Chats")
                                    .orderByChild("user1")
                                    .equalTo(user.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean reverseChatExists = false;

                                            for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                                                String user2 = chatSnapshot.child("user2").getValue(String.class);
                                                if (user2 != null && user2.equals(currentUserUid)) {
                                                    // Чат уже существует между пользователями в обратном порядке, показать сообщение и завершить метод
                                                    Toast.makeText(FindPerson.this, "Chat with this user already exists", Toast.LENGTH_SHORT).show();
                                                    reverseChatExists = true;
                                                    break;
                                                }
                                            }

                                            if (!reverseChatExists) {
                                                // Чат еще не существует, создать новый
                                                String chatId = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
                                                ChatsDomain chat = new ChatsDomain(chatId, chatName, currentUserUid, user.getUid());

                                                HashMap<String, Object> chatMap = new HashMap<>();
                                                chatMap.put("user1", currentUserUid);
                                                chatMap.put("user2", user.getUid());

                                                FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).setValue(chatMap)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                updateChatsForUser(currentUserUid, chatId);
                                                                updateChatsForUser(user.getUid(), chatId);
                                                                String notCurrentUserId = user.getUid();
                                                                Intent intent = new Intent(FindPerson.this, ChatActivity.class);
                                                                intent.putExtra("chatId", chat.getChat());
                                                                intent.putExtra("chatName", chat.getChat_name());
                                                                intent.putExtra("chatImage", chat.getChat_image());
                                                                intent.putExtra("notCurrentUserId", notCurrentUserId);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(FindPerson.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(FindPerson.this, "Failed to check chat existence", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FindPerson.this, "Failed to check chat existence", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateChatsForUser(String userId, String chatId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatsStr = snapshot.getValue(String.class);

                if (chatsStr != null && !chatsStr.isEmpty()) {
                    chatsStr += "," + chatId;
                } else {
                    chatsStr = chatId;
                }

                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("chats").setValue(chatsStr)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(FindPerson.this, "Failed to update user chats", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FindPerson.this, "Failed to update user chats", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String query = newText.trim().toLowerCase();
        if (TextUtils.isEmpty(query)) {
            users.clear();
            displayUsers(users);
            binding.emptyMessage.setVisibility(View.VISIBLE);
        } else {
            binding.emptyMessage.setVisibility(View.GONE);
            loadUsers(query);
        }
        return true;
    }



    public void onButtonBack(View view) {
        Intent intent = new Intent(FindPerson.this, ChatsPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities above ChatsPage
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
