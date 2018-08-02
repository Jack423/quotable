package com.apexsoftware.quotable;

import com.apexsoftware.quotable.managers.DatabaseHelper;

//Created by Jack Butler on 8/2/2018.

public class Application extends android.app.Application {
    public static final String TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationHelper.initDatabaseHelper(this);
        //DatabaseHelper.getInstance(this).subscribeToNewPosts();
    }
}
