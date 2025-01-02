package com.example.wabisabi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wabisabi.Activity.GroupChatActivity;
import com.example.wabisabi.Domain.GroupsDomain;
import com.example.wabisabi.R;

import java.util.ArrayList;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderViewChatList>{
    private Context context;
    private ArrayList<GroupsDomain> groupChatLists;

    public AdapterGroupChat(Context context, ArrayList<GroupsDomain> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }


    @NonNull
    @Override
    public HolderViewChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatlist_row, parent, false);
        return new HolderViewChatList(view);
    }
    @Override
    public void onBindViewHolder(@NonNull HolderViewChatList holder, int position) {
        GroupsDomain model = groupChatLists.get(position);
        String groupId = model.getGroupId();
        String groupTitle = model.getGroupTitle();

        holder.groupTitleTv.setText(groupTitle);
        holder.nameTv.setText("Sender Name");

        String groupIcon = model.getGroupIcon();
        if (groupIcon != null && !groupIcon.isEmpty()) {
            Glide.with(context)
                    .load(groupIcon)
                    .into(holder.groupIconIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    static class HolderViewChatList extends RecyclerView.ViewHolder {
        private ImageView groupIconIv;
        private TextView groupTitleTv, nameTv, messagetV;

        public HolderViewChatList(@NonNull View itemView) {
            super(itemView);

            groupIconIv = itemView.findViewById(R.id.groupPic);
            groupTitleTv = itemView.findViewById(R.id.textViewGroup);
            nameTv = itemView.findViewById(R.id.nameTv);
            messagetV = itemView.findViewById(R.id.messageGroupTv);
            messagetV = itemView.findViewById(R.id.messageGroupTv);
        }
    }
}