package com.apexsoftware.quotable.models;
//Created by Jack Butler on 8/20/2018.

public class Friend {
    String name;
    String profilePhotoUrl;
    String friendSince;
    String id;

    public Friend() {
    }

    public Friend(String name, String profilePhotoUrl, String friendSince, String id) {
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.friendSince = friendSince;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getFriendSince() {
        return friendSince;
    }

    public void setFriendSince(String bio) {
        this.friendSince = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
