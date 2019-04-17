package com.apexsoftware.quotable.model;

// Created by Jack Butler on 10/2/2018.

import android.os.Parcel;
import android.os.Parcelable;

import com.apexsoftware.quotable.util.FormatterUtil;
import com.linkedin.android.spyglass.mentions.Mentionable;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;

public class Profile implements Serializable, Mentionable {
    private String id;
    private String username;
    private String handle;
    private String email;
    private String bio;
    private long createdDate;
    private String dateJoined;
    private String photoUrl;
    private long likesCount;
    private String registrationToken;

    public Profile() {
        //Default constructor required for calls to DataSnapshot.getValue(Profile.class)
        this.createdDate = new Date().getTime();
        dateJoined = FormatterUtil.getMonthYear(new Date(createdDate));
    }

    public Profile(Parcel in) {
        id = in.readString();
        username = in.readString();
        email = in.readString();
        handle = in.readString();
        photoUrl = in.readString();
    }

    public Profile(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return getHandle();
            case PARTIAL:
                String[] words = getHandle().split(" ");
                return (words.length > 1) ? words[0] : "";
            case NONE:
            default:
                return "";
        }
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.FULL_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getId().hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return getHandle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(handle);
        dest.writeString(email);
        dest.writeString(bio);
        dest.writeString(photoUrl);
    }

    public static final Parcelable.Creator<Profile> CREATOR
            = new Parcelable.Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
