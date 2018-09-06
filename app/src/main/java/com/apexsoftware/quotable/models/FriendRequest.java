package com.apexsoftware.quotable.models;
// Created by Jack Butler on 9/5/2018.

public class FriendRequest {
    private String id;
    private String requestType;

    private String name;
    private String bio;
    private String profilePhotoUrl;

    public FriendRequest() {
    }

    public FriendRequest(String id, String requestType) {
        this.id = id;
        this.requestType = requestType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
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

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
