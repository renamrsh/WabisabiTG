package com.example.wabisabi.Domain;

public class InformationDomain {
    private String title;
    private String picAddress;
    private Integer likesNumber;
    private String likedUsers;
    private String savedByUsers;
    private String description;
    private long timestamp;

    public InformationDomain() {
    }
    public InformationDomain(String title, String picAddress, Integer likesNumber) {
        this.title = title;
        this.picAddress = picAddress;
        this.likesNumber = likesNumber;
    }
    public InformationDomain(String title, String picAddress, String description, Integer likesNumber, String savedByUsers) {
        this.title = title;
        this.picAddress = picAddress;
        this.likesNumber = likesNumber;
        this.description = description;
        this.savedByUsers = savedByUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public int getLikesNumber() {
        return likesNumber;
    }

    public void setLikesNumber(int likesNumber) {
        this.likesNumber = likesNumber;
    }

    public String getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(String likedUsers) {
        this.likedUsers = likedUsers;
    }

    public String getSavedByUsers() {
        return savedByUsers;
    }

    public void setSavedByUsers(String savedByUsers) {
        this.savedByUsers = savedByUsers;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
