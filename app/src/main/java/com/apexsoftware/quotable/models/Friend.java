package com.apexsoftware.quotable.models;
//Created by Jack Butler on 8/16/2018.

public class Friend {
    String name;
    String bio;
    String profilePictureUrl;
    String userId;

    public Friend() {
    }

    public Friend(String name, String bio, String profilePictureUrl) {
        this.name = name;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
