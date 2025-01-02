package com.example.wabisabi.Activity.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wabisabi.R;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private ArrayList<User> users = new ArrayList<>();
    private OnItemClickListener listener;


    public UsersAdapter(ArrayList<User> users) {
        this.users = users;
    }

    public UsersAdapter(ArrayList<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.person_item_rv, parent, false);
        return new UserViewHolder(itemView);
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.username_tv.setText(user.getUsername());

        if (!user.getProfileImage().isEmpty()){
            Glide.with(holder.itemView.getContext()).load(user.getProfileImage()).into(holder.profileImage_iv);
        }


        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}