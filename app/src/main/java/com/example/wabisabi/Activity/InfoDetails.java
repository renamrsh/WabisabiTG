package com.example.wabisabi.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.wabisabi.Domain.InformationDomain;
import com.example.wabisabi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoDetails extends BaseActivity {
    private int likesNumber;
    private String likedUsers, savedByUsers;
    private String sourceActivity;
    private boolean isFavorite;
    private boolean isBookmark;
    private ImageView favoriteImageView;
    FirebaseDatabase db;
    DatabaseReference reference;
    private ImageView bookmarkImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_details);

        onCreateDrawer();

        Intent intent = getIntent();
        String diseaseName = intent.getStringExtra("diseaseName");
        String description = intent.getStringExtra("description");
        likesNumber = intent.getIntExtra("likesNumber", 0);
        likedUsers = intent.getStringExtra("likedUsers");
        savedByUsers = intent.getStringExtra("savedByUsers");

        TextView diseaseNameTextView = findViewById(R.id.titleTxt);
        diseaseNameTextView.setText(diseaseName);
        TextView descriptionTextView = findViewById(R.id.descriptionTxt);

        description = description.replace("\\n", "<br>");

        Spannable spannable = (Spannable) Html.fromHtml(description);
        StyleSpan[] styleSpans = spannable.getSpans(0, spannable.length(), StyleSpan.class);

        for (StyleSpan styleSpan : styleSpans) {
            if (styleSpan.getStyle() == Typeface.BOLD) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), spannable.getSpanStart(styleSpan),
                        spannable.getSpanEnd(styleSpan), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        descriptionTextView.setText(spannable);

        ImageView imageAddressImageView = findViewById(R.id.pic);
        String picAddress = intent.getStringExtra("picAddress");

        Glide.with(this)
                .load(picAddress)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(imageAddressImageView);
        sourceActivity = intent.getStringExtra("sourceActivity");



        if (likedUsers != null && likedUsers.contains(getCurrentUserId())) {
            isFavorite = true;
        } else {
            isFavorite = false;
        }
        favoriteImageView = findViewById(R.id.favourite);

        if (savedByUsers != null && savedByUsers.contains(getCurrentUserId())) {
            isBookmark = true;
        } else {
            isBookmark = false;
        }
        //isBookmark = getIntent().getBooleanExtra("isBookmark", false);
        bookmarkImageView = findViewById(R.id.bookmark);

        updateFavoriteIcon();
        updateBookmarkIcon();


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(InfoDetails.this, FindInfo.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    private void updateBookmarkIcon() {
        bookmarkImageView.setImageResource(isBookmark ? R.drawable.bookmark_icon : R.drawable.bookmark_outlined_icon);
    }
    public void onBookmarkClicked(View view) {
        isBookmark = !isBookmark;
        updateBookmarkIcon();
        updateSavedByUsersInDatabase(isBookmark);
    }
    private void updateSavedByUsersInDatabase(boolean isLiked) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Info");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                String diseaseName = intent.getStringExtra("diseaseName");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformationDomain info = snapshot.getValue(InformationDomain.class);
                    if (info != null && info.getTitle().equals(diseaseName)) {
                        String currentSavedUsers = info.getSavedByUsers();
                        if (currentSavedUsers == null) {
                            currentSavedUsers = "";
                        }
                        if (isBookmark) {
                            currentSavedUsers += (currentSavedUsers.isEmpty() ? "" : ",") + getCurrentUserId();
                        } else {
                            currentSavedUsers = removeCurrentUserFromLikedUsers(currentSavedUsers);
                        }
                        snapshot.getRef().child("savedByUsers").setValue(currentSavedUsers);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InfoDetails.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateFavoriteIcon() {
        favoriteImageView.setImageResource(isFavorite ? R.drawable.heart_icon : R.drawable.heart_outlined_icon);
    }
    public void onFavoriteClicked(View view) {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        if (isFavorite) {
            likesNumber++;
            Log.d("TAG", "likesNumber is: " + likesNumber);
        } else {
            likesNumber--;
            Log.d("TAG", "likesNumber is: " + likesNumber);
        }
        updateLikesCountInDatabase(likesNumber, isFavorite);
    }

    private void updateLikesCountInDatabase(int likesCount, boolean isLiked) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Info");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = getIntent();
                String diseaseName = intent.getStringExtra("diseaseName");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InformationDomain info = snapshot.getValue(InformationDomain.class);
                    if (info != null && info.getTitle().equals(diseaseName)) {
                        snapshot.getRef().child("likesNumber").setValue(likesCount);

                        String currentLikedUsers = info.getLikedUsers();
                        if (currentLikedUsers == null) {
                            currentLikedUsers = "";
                        }
                        if (isLiked) {
                            currentLikedUsers += (currentLikedUsers.isEmpty() ? "" : ",") + getCurrentUserId();
                        } else {
                            currentLikedUsers = removeCurrentUserFromLikedUsers(currentLikedUsers);
                        }
                        snapshot.getRef().child("likedUsers").setValue(currentLikedUsers);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InfoDetails.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String removeCurrentUserFromLikedUsers(String likedUsers) {
        String currentUserId = getCurrentUserId();
        if (likedUsers != null && !likedUsers.isEmpty()) {
            String[] userIds = likedUsers.split(",");
            StringBuilder newLikedUsers = new StringBuilder();
            for (String userId : userIds) {
                if (!userId.equals(currentUserId)) {
                    if (newLikedUsers.length() > 0) {
                        newLikedUsers.append(",");
                    }
                    newLikedUsers.append(userId);
                }
            }
            return newLikedUsers.toString();
        }
        return likedUsers;
    }
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return "";
        }
    }



    public void onButtonBack(View view) {
        if ("Home".equals(sourceActivity)) {
            startActivity(new Intent(InfoDetails.this, Home.class));
        } else if ("FindInfo".equals(sourceActivity)) {
            startActivity(new Intent(InfoDetails.this, FindInfo.class));
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
