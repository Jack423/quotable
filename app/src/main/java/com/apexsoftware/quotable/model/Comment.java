package com.apexsoftware.quotable.model;
// Created by Jack Butler on 10/2/2018.

import java.util.Calendar;

public class Comment {
    private String id;
    private String text;
    private String authorId;
    private long createdDate;

    public Comment() {
        //Default constructor requried for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment (String text) {
        this.text = text;
        this.createdDate = Calendar.getInstance().getTimeInMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }
}
