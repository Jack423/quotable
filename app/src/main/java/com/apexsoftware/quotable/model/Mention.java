package com.apexsoftware.quotable.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.linkedin.android.spyglass.mentions.Mentionable;

import androidx.annotation.NonNull;

public class Mention implements Mentionable {
    private String name;
    private String handle;
    private String imageUrl;
    private String authorId;

    public Mention() {
        //Default constructor for firebase
    }

    public Mention(String name) {
        this.name = name;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return name;
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    public Mention(Parcel in) {
        this.name = in.readString();
        this.handle = in.readString();
        this.imageUrl = in.readString();
        this.authorId = in.readString();
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return name.hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Mention> CREATOR = new Parcelable.Creator<Mention>() {
        public Mention createFromParcel(Parcel in) {
            return new Mention(in);
        }

        @Override
        public Mention[] newArray(int size) {
            return new Mention[size];
        }
    };
}
