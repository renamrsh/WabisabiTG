package com.example.wabisabi.Activity.users;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class User {

    public String uid;
    public String username;
    public String profileImage;
    private ArrayList<String> chats;
    private String status;


    public User(String uid, String username, String profileImage) {
        this.uid = uid;
        this.username = username;
        this.profileImage = profileImage;
        this.chats = new ArrayList<>();
    }
    public User() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        updateStatusInFirebase(status);
    }
    private void updateStatusInFirebase(String status) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.child("status").setValue(status);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    public ArrayList<String> getChats() {
        return chats;
    }

    public void setChats(ArrayList<String> chats) {
        this.chats = chats; // Update the entire chats list
    }


    public void addChat(String chatId) {
        this.chats.add(chatId);
    }


    public void removeChat(String chatId) {
        this.chats.remove(chatId);
    }
}
