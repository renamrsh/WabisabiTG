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
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.wabisabi.Activity.InfoDetails;
import com.example.wabisabi.Domain.InformationDomain;
import com.example.wabisabi.R;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BulletpointsAdapter extends RecyclerView.Adapter<ViewholderBP> {

    public ArrayList<InformationDomain> items_bp;
    private static final String TAG = "BulletpointsAdapter";
    private Context context;
    private FirebaseStorage storage;


    public void setSearchList(ArrayList<InformationDomain> dataBPSearchList){
        this.items_bp = dataBPSearchList;
        notifyDataSetChanged();
    }

    public BulletpointsAdapter(Context context,  ArrayList<InformationDomain> items_bp) {
        this.context = context;
        this.items_bp = items_bp;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewholderBP onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_bulletpoints_list,parent,false);
        return new ViewholderBP(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderBP holder, int position) {
        InformationDomain currentItem = items_bp.get(position);
        holder.title.setText(currentItem.getTitle());

        String likes = String.valueOf(currentItem.getLikesNumber());
        holder.likesNumber.setText(likes);

        String imageUrl = currentItem.getPicAddress();
        Log.d(TAG, "Image URL: " + imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .transform(new GranularRoundedCorners(30, 30, 0, 0))
                    .into(holder.pic);
        } else {
            holder.pic.setImageResource(R.drawable.frog_color_front);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoDetails.class);
            intent.putExtra("diseaseName", currentItem.getTitle());
            intent.putExtra("description", currentItem.getDescription());
            intent.putExtra("likesNumber", currentItem.getLikesNumber());
            intent.putExtra("picAddress", currentItem.getPicAddress());
            intent.putExtra("likedUsers", currentItem.getLikedUsers());
            intent.putExtra("savedByUsers", currentItem.getSavedByUsers());
            context.startActivity(intent);

            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


    // SHOW BP WITH THE HIGHEST LIKES AMOUNT
    @Override
    public int getItemCount() {
        Collections.sort(items_bp, new Comparator<InformationDomain>() {
            @Override
            public int compare(InformationDomain o1, InformationDomain o2) {
                return Integer.compare(o2.getLikesNumber(), o1.getLikesNumber());
            }
        });
        // Return only 5 items
        return Math.min(items_bp.size(), 5);
    }
}

class ViewholderBP extends RecyclerView.ViewHolder{
    final TextView title, likesNumber;
    final ImageView pic;
    public ViewholderBP(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.titleTxt);
        likesNumber=itemView.findViewById(R.id.numberTxt);
        pic=itemView.findViewById(R.id.pic);
    }
}
