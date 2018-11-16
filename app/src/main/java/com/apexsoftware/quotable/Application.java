package com.apexsoftware.quotable;

// Created by Jack Butler on 10/5/2018.

import com.apexsoftware.quotable.main.interactors.PostInteractor;

public class Application extends android.app.Application {
    public static final String TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationHelper.initDatabaseHelper(this);
        PostInteractor.getInstance(this).subscribeToNewPosts();
    }
}
