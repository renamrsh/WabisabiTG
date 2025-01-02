package com.example.wabisabi.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<ViewholderGroups> {

    public ArrayList<GroupsDomain> items_groups;
    Context context;
    private static final String TAG = "GroupsAdapter";
    private FirebaseStorage storage;
    private List<String> participantsList;
    private OnGroupClickListener onGroupClickListener;

    public static final int HOME_ACTIVITY = 0;
    public static final int CHATS_ACTIVITY = 1;

    private int parentActivity;

    public void setParticipantsList(List<String> participantsList) {
        this.participantsList = participantsList;
        notifyDataSetChanged();
    }
    public void setGroupSearchList(ArrayList<GroupsDomain> dataGroupsSearchList){
        this.items_groups = dataGroupsSearchList;
        notifyDataSetChanged();
    }

    public GroupsAdapter(int parentActivity) {
        this.parentActivity = parentActivity;
    }
    public GroupsAdapter(int parentActivity, Context context, ArrayList<GroupsDomain> items_groups) {
        this.context = context;
        this.parentActivity = parentActivity;
        this.items_groups = items_groups;
        storage = FirebaseStorage.getInstance();
    }
    public GroupsAdapter(int parentActivity, Context context, ArrayList<GroupsDomain> items_groups, OnGroupClickListener listener) {
        this.context = context;
        this.parentActivity = parentActivity;
        this.items_groups = items_groups;
        this.onGroupClickListener = listener;
        storage = FirebaseStorage.getInstance();
    }
    /*public GroupsAdapter( Context context, ArrayList<GroupsDomain> items_groups) {
        this.context = context;
        this.items_groups = items_groups;
        // Initialize FirebaseStorage instance
        storage = FirebaseStorage.getInstance();
    }*/

    @NonNull
    @Override
    public ViewholderGroups onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (parentActivity == HOME_ACTIVITY) {
            View itemView = inflater.inflate(R.layout.viewholder_groups_list, parent, false);
            return new ViewholderGroups(itemView);
        } else if (parentActivity == CHATS_ACTIVITY) {
            View itemView = inflater.inflate(R.layout.group_item_rv, parent, false);
            return new ViewholderGroups(itemView);
        } else {
            return null;
        }
    }
    public interface OnGroupClickListener {
        void onGroupClick(String groupId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderGroups holder, int position) {
        GroupsDomain currentItem = items_groups.get(position);
        Log.d(TAG, "Current Item: " + currentItem.toString());

        // Set data to views
        holder.groupTitle.setText(currentItem.getGroupTitle());
        holder.groupDescription.setText(currentItem.getGroupDescription());

        // Check if the item view contains the groupMemberNumberTxt ID
        View groupMemberNumberView = holder.itemView.findViewById(R.id.groupMemberNumberTxt);
        if (groupMemberNumberView != null) {
            // Log the size of the participantIds list
            Log.d(TAG, "Participant count for group " + currentItem.getGroupTitle() + ": " + currentItem.getParticipantCount());
            int participantsCount = currentItem.getParticipantCount();
            holder.groupMemberNumber.setText(String.valueOf(participantsCount));
        }


        String groupIcon = currentItem.getGroupIcon();
        if (groupIcon != null && !groupIcon.isEmpty()) {
            Glide.with(context)
                    .load(groupIcon)
                    .into(holder.groupIcon);
        }
        //holder.groupsMembersNumber.setText(currentItem.getGroupsMembersNumber());


/*
        int drawableResourceId = holder.itemView.getResources().getIdentifier(currentItem.getGroupsPicAddress(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.groupsPicAddress);


        String imageUrl = currentItem.getGroupsPicAddress();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.groupsPicAddress);
        } else {
            holder.groupsPicAddress.setImageResource(R.drawable.frog_color_front);
        }
*/


        holder.itemView.setOnClickListener(v -> {
            if (onGroupClickListener != null) {
                onGroupClickListener.onGroupClick(currentItem.getGroupId());
            }
            Intent intent = new Intent(context, GroupChatActivity.class);//chat
            intent.putExtra("groupId", currentItem.getGroupId());
            intent.putExtra("groupTitle", currentItem.getGroupTitle());
            intent.putExtra("groupDescription", currentItem.getGroupDescription());
            intent.putExtra("picAddress", currentItem.getGroupsPicAddress());
            context.startActivity(intent);

            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

            /*holder.groupsName.setText(items_groups.get(position).getGroupsName());
        holder.groupsDescription.setText(items_groups.get(position).getGroupsDescription());
        holder.groupsMembersNumber.setText(items_groups.get(position).getGroupsMembersNumber());

        int drawableResourceId=holder.itemView.getResources().getIdentifier(items_groups.get(position).getGroupsPicAddress(),"drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(holder.groupsPicAddress);*/
    }

    @Override
    public int getItemCount() {
        Collections.sort(items_groups, new Comparator<GroupsDomain>() {
            @Override
            public int compare(GroupsDomain o1, GroupsDomain o2) {
                return Integer.compare(o2.getParticipantCount(), o1.getParticipantCount());
            }
        });

        return Math.min(items_groups.size(), 5);
    }


}
class ViewholderGroups extends RecyclerView.ViewHolder{
    final TextView groupTitle, groupDescription,groupMemberNumber;
    final ImageView groupIcon;

    public ViewholderGroups(@NonNull View itemView) {
        super(itemView);

        groupTitle=itemView.findViewById(R.id.groupNameTxt);
        groupDescription=itemView.findViewById(R.id.groupDescriptionTxt);

        groupMemberNumber=itemView.findViewById(R.id.groupMemberNumberTxt);
        groupIcon =itemView.findViewById(R.id.groupPic);
    }
}