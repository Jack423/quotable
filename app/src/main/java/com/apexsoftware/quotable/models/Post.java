package com.apexsoftware.quotable.models;

import java.util.Date;
import java.util.UUID;

//Created By: Jack Butler
//Date: 7/22/2018

public class Post {
    User user;
    String tweet;
    Date createdAt;
    String id;

    public Post(User user, String tweet, Date createdAt) {
        this.user = user;
        this.tweet = tweet;
        this.createdAt = createdAt;
        id = UUID.randomUUID().toString();
    }

    public User getUser() {
        return user;
    }

    public String getTweet() {
        return tweet;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    //Tells us weather a Yak is valid or not
    //In this example we simply check the length.
    public static boolean isValid(String text)
    {
        return text.length() > 0 && text.length() < 144;
    }
}
