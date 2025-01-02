package com.example.wabisabi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wabisabi.Domain.ModelCroupChat;
import com.example.wabisabi.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    private Context context;
    private ArrayList<ModelCroupChat> modelGroupChatLists;
    private FirebaseAuth firebaseAuth;
    public AdapterChat(Context context, ArrayList<ModelCroupChat> modelCroupChats){
        this.context=context;
        this.modelGroupChatLists=modelCroupChats;
        firebaseAuth=FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_chats_page, parent, false);
        return new HolderGroupChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        ModelCroupChat currentItem = modelGroupChatLists.get(position);
    }

    @Override
    public int getItemCount() {
        return modelGroupChatLists.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {
        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
        }
    }

}
