package com.apexsoftware.quotable;

import android.app.Application;

import com.firebase.client.Firebase;

//Created By: Jack Butler
//Date: 7/22/2018

public class Quotable extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
