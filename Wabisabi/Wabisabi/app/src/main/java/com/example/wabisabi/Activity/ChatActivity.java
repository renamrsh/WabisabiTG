package com.example.wabisabi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wabisabi.Activity.message.Message;
import com.example.wabisabi.Activity.message.MessageAdapter;
import com.example.wabisabi.R;
import com.example.wabisabi.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private String chatId, notCurrentUserId;
    DatabaseReference userRefForSeen;
    ValueEventListener seenListener;

    private TextView userTitleTv;
    private ImageView userIconIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userIconIv = findViewById(R.id.profile_iv);
        userTitleTv = findViewById(R.id.usernameChatTv);

        Intent intent = getIntent();

        String profileImageUrl = intent.getStringExtra("chatImage");
        String chatName = intent.getStringExtra("chatName");
        notCurrentUserId = intent.getStringExtra("notCurrentUserId");
        Log.d("DeleteChat", "Not Current User ID: " + notCurrentUserId); // Add this log

        userTitleTv.setText(chatName);

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).placeholder(R.drawable.frog_color_front).into(userIconIv);
        } else {
            userIconIv.setImageResource(R.drawable.frog_color_front);
        }

        chatId = getIntent().getStringExtra("chatId");
        if(chatId==null)return;

        loadMessages(chatId);

        binding.messageBtn.setOnClickListener(v -> {
            String message = binding.messageEt.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = simpleDateFormat.format(new Date());
            binding.messageEt.setText("");
            sendMessage(chatId, message, date);
            loadMessages(chatId);
        });



        binding.btnDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ChatActivity.this, ChatsPage.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void sendMessage(String chatId, String message, String date) {
        if (chatId == null) return;
        HashMap<String, String> messageInfo = new HashMap<>();
        messageInfo.put("text", message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        messageInfo.put("date", date);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .child("messages").push().setValue(messageInfo);
    }
    private void loadMessages(String chatId) {
        if (chatId == null) return;

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        List<Message> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            String ownerId = messageSnapshot.child("ownerId").getValue() != null ? messageSnapshot.child("ownerId").getValue().toString() : "";
                            String text = messageSnapshot.child("text").getValue() != null ? messageSnapshot.child("text").getValue().toString() : "";
                            String date = messageSnapshot.child("date").getValue() != null ? messageSnapshot.child("date").getValue().toString() : "";

                            messages.add(new Message(messageId, ownerId, text, date));
                        }

                        binding.messageRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        binding.messageRv.setAdapter(new MessageAdapter(messages));
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue() instanceof Map) {
                                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    String status = (String) data.get("status");
                                    if (status != null) {
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Обработка ошибки
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибки
                    }
                });
    }



    public void onDeleteClick(View view) {

        if (chatId != null) {
            deleteChatFromDatabase(chatId);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedChatId", chatId);
            setResult(Activity.RESULT_OK, resultIntent);
            startActivity(new Intent(ChatActivity.this, ChatsPage.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

    private void deleteChatFromDatabase(String chatId) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);

        // Удаление чата из "Chats" узла
        chatRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Удаление чата из списков чатов пользователей

                        if (notCurrentUserId != null) {
                            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(notCurrentUserId).child("chats");
                            userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String chatsStr = snapshot.getValue(String.class);
                                        if (chatsStr != null && !chatsStr.isEmpty()) {
                                            String[] chatsIds = chatsStr.split(",");
                                            StringBuilder newChatsStr = new StringBuilder();
                                            for (String id : chatsIds) {
                                                if (!id.equals(chatId)) {
                                                    newChatsStr.append(id).append(",");
                                                }
                                            }
                                            if (newChatsStr.length() > 0) {
                                                newChatsStr.setLength(newChatsStr.length() - 1);  // Remove the last comma
                                            }
                                            snapshot.getRef().setValue(newChatsStr.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatActivity.this, "Failed to delete chat from user's chats list", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        // Получаем текущего пользователя
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String currentUserId = currentUser.getUid();

                            // Удаление чата из "chats" списка пользователя
                            DatabaseReference userChatsRefC = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("chats");
                            userChatsRefC.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String chatsStr = snapshot.getValue(String.class);
                                        if (chatsStr != null && !chatsStr.isEmpty()) {
                                            String[] chatsIds = chatsStr.split(",");
                                            StringBuilder newChatsStr = new StringBuilder();
                                            for (String id : chatsIds) {
                                                if (!id.equals(chatId)) {
                                                    newChatsStr.append(id).append(",");
                                                }
                                            }
                                            if (newChatsStr.length() > 0) {
                                                newChatsStr.setLength(newChatsStr.length() - 1);  // Remove the last comma
                                            }
                                            snapshot.getRef().setValue(newChatsStr.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatActivity.this, "Failed to delete chat from user's chats list", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onButtonBack(View view) {
        startActivity(new Intent(ChatActivity.this, ChatsPage.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}