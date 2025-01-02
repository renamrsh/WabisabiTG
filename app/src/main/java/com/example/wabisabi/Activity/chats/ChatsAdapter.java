package com.example.wabisabi.Activity.chats;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wabisabi.Activity.ChatActivity;
import com.example.wabisabi.Activity.FindPerson;
import com.example.wabisabi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ADD_FRIEND = 0;
    private static final int VIEW_TYPE_CHAT = 1;
    private ArrayList<ChatsDomain> chatsDomains;
    private AdapterView.OnItemClickListener mListener;

    private OnItemClickListener listener;
    public ChatsAdapter(ArrayList<ChatsDomain> chatsDomains) {
        this.chatsDomains = chatsDomains;
    }
    public ChatsAdapter(ArrayList<ChatsDomain> chatsDomains, AdapterView.OnItemClickListener OnItemClickListener) {
        this.chatsDomains = chatsDomains;
        this.mListener = OnItemClickListener; // Присвоение слушателя
    }
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_ADD_FRIEND : VIEW_TYPE_CHAT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ADD_FRIEND) {
            View view = inflater.inflate(R.layout.add_new_friend, parent, false);
            return new AddFriendViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.viewholder_friends, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            ChatsDomain chat = chatsDomains.get(position - 1);
            chatViewHolder.chat_name_tv.setText(chat.getChat_name());

            String userId;
            if (!chat.getUserId1().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                userId = chat.getUserId1();
            } else {
                userId = chat.getUserId2();
            }

            // Load profile image
            FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("profileImage").get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            try {
                                String profileImageUrl = task.getResult().getValue().toString();
                                if (!profileImageUrl.isEmpty()) {
                                    Glide.with(chatViewHolder.itemView.getContext()).load(profileImageUrl).into(chatViewHolder.chat_iv);
                                }
                            } catch (Exception e) {
                                Toast.makeText(chatViewHolder.itemView.getContext(), "Failed to get profile image link", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Set click listener for opening chat activity
            chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("chatId", chat.getChat());
                    intent.putExtra("chatName", chat.getChat_name());
                    intent.putExtra("chatImage", chat.getChat_image());
                    intent.putExtra("notCurrentUserId", chat.getNotCurrentUserId());
                    v.getContext().startActivity(intent);
                    Activity activity = (Activity) v.getContext();
                    activity.finish();
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        } else if (holder instanceof AddFriendViewHolder) {
            AddFriendViewHolder addFriendViewHolder = (AddFriendViewHolder) holder;
            addFriendViewHolder.addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click for adding friend
                    v.getContext().startActivity(new Intent(v.getContext(), FindPerson.class));
                }
            });
        }
    }
    public interface OnChatDeleteClickListener {
        void onChatDeleteClick(int position);
    }
    @Override
    public int getItemCount() {
        return chatsDomains.size() + 1;
    }

    static class AddFriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView addFriendButton;

        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            addFriendButton = itemView.findViewById(R.id.add_friend);
        }
    }
}
