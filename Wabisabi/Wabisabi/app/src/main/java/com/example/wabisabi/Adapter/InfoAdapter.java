package com.example.wabisabi.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StyleSpan;
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

public class InfoAdapter extends RecyclerView.Adapter<ViewholderInfo> {
    private static final String TAG = "InfoAdapter";

    private ArrayList<InformationDomain> items_info;
    private Context context;
    private FirebaseStorage storage;

    public void setInfoSearchList(ArrayList<InformationDomain> dataInfoSearchList){
        this.items_info = dataInfoSearchList;
        notifyDataSetChanged();
    }

    public InfoAdapter(Context context, ArrayList<InformationDomain> items_info){
        this.context = context;
        this.items_info = items_info;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewholderInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_info_list,parent,false);
        return new ViewholderInfo(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderInfo holder, int position) {
        InformationDomain currentItem = items_info.get(position);
        holder.title.setText(currentItem.getTitle());

        String formattedDescription = currentItem.getDescription();
        if (formattedDescription != null) {
            formattedDescription = formattedDescription.replace("\\n", "\n");
            Spannable spannable = (Spannable) Html.fromHtml(formattedDescription);
            StyleSpan[] styleSpans = spannable.getSpans(0, spannable.length(), StyleSpan.class);
            for (StyleSpan styleSpan : styleSpans) {
                if (styleSpan.getStyle() == Typeface.BOLD) {
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), spannable.getSpanStart(styleSpan),
                            spannable.getSpanEnd(styleSpan), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            holder.description.setText(spannable);
        } else {
            holder.description.setText("");
        }

        /*int drawableResourceId = holder.itemView.getResources().getIdentifier(currentItem.getPicAddress(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.pic);*/


        // Load image from Firebase Storage using Glide
        // Assuming you have a FirebaseStorage instance named "storage"
        // and a StorageReference pointing to your image
        String imageUrl = currentItem.getPicAddress();
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
            intent.putExtra("likesNumber", currentItem.getLikesNumber());
            intent.putExtra("description", currentItem.getDescription());
            intent.putExtra("picAddress", currentItem.getPicAddress());
            intent.putExtra("likedUsers", currentItem.getLikedUsers());
            intent.putExtra("savedByUsers", currentItem.getSavedByUsers());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }



    @Override
    public int getItemCount() {
        return items_info.size();
    }
}

class ViewholderInfo extends RecyclerView.ViewHolder {
    TextView title, description;
    ImageView pic;

    public ViewholderInfo(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.titleTxt);
        description = itemView.findViewById(R.id.descriptionTxt);
        pic = itemView.findViewById(R.id.pic);
    }
}
