package com.apexsoftware.quotable.models;

import com.apexsoftware.quotable.enums.ItemType;
import com.apexsoftware.quotable.util.FormatterUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//Created By: Jack Butler
//Date: 7/22/2018

public class Post implements Serializable, LazyLoading{
    private String user;
    private String userId;
    private String text;
    private String userImagePath;
    private long createdAt;
    private long likesCount;
    private String postId;
    private ItemType itemType;
    private Calendar calendar = Calendar.getInstance();

    //We need an empty constructor for Firebase
    public Post() {
        this.createdAt = calendar.getTimeInMillis();
        itemType = ItemType.ITEM;
    }

    public Post(String user, String userId, String text) {
        this.user = user;
        this.userId = userId;
        this.text = text;
        this.postId = UUID.randomUUID().toString();
        itemType = ItemType.ITEM;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("user", user);
        result.put("text", text);
        result.put("createdAt", createdAt);
        result.put("postId", postId);
        result.put("userId", userId);
        result.put("userImagePath", userImagePath);
        result.put("likesCount", likesCount);

        return result;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public void setItemType(ItemType itemType) {

    }
}
