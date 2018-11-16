package com.apexsoftware.quotable;
// Created by Jack Butler on 10/4/2018.

import com.apexsoftware.quotable.managers.DatabaseHelper;

public class ApplicationHelper {
    private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static void initDatabaseHelper(android.app.Application application) {
        databaseHelper = DatabaseHelper.getInstance(application);
        databaseHelper.init();
    }
}
