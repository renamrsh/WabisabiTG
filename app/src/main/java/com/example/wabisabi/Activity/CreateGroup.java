package com.example.wabisabi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.example.wabisabi.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class CreateGroup extends BaseActivity {

    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private ProgressDialog progressDialog;

    private Uri image_uri = null;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private ImageView groupIconIv;
    private EditText groupDescriptionEt;
    private EditText groupTitleEt;
    private Button createGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        onCreateDrawer();
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Create Group");
        }

        groupIconIv = findViewById(R.id.imageViewGroup);
        groupTitleEt = findViewById(R.id.groupTitleId);
        groupDescriptionEt = findViewById(R.id.groupDescriptionEt);
        createGroupBtn = findViewById(R.id.createGroupBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(CreateGroup.this, ChatsPage.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void startCreatingGroup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");

        String groupTitle = groupTitleEt.getText().toString().trim();
        String groupDescription = groupDescriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(groupTitle)) {
            Toast.makeText(this, "Please enter group title...", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            createGroup(timestamp, groupTitle, groupDescription, "");
        } else {
            String filePathAndName = "groups/" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

            storageReference.putFile(image_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {
                            createGroup(timestamp, groupTitle, groupDescription, downloadUri.toString());
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(CreateGroup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void createGroup(String timestamp, String groupTitle, String groupDescription, String groupIcon) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", timestamp);
        hashMap.put("groupTitle", groupTitle);
        hashMap.put("groupDescription", groupDescription);
        hashMap.put("groupIcon", groupIcon);
        hashMap.put("createdBy", firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("uid", firebaseAuth.getUid());
                    hashMap1.put("role", "creator");

                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                    ref1.child(timestamp).child("Participants").child(firebaseAuth.getUid()).setValue(hashMap1)
                            .addOnSuccessListener(aVoid1 -> {
                                progressDialog.dismiss();
                                Toast.makeText(CreateGroup.this, "Group Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CreateGroup.this, ChatsPage.class)//AddParticipantsToGroup
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // Clear all activities above ChatsPage
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(CreateGroup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreateGroup.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && actionBar != null) {
            actionBar.setSubtitle(user.getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_GALLERY_CODE) {
            image_uri = data.getData();
            groupIconIv.setImageURI(image_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onButtonBack(View view) {
        startActivity(new Intent(CreateGroup.this, ChatsPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); // Clear all activities above ChatsPage
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
