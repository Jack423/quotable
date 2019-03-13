package com.apexsoftware.quotable.main.interactors;

import android.content.Context;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.model.Mention;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.linkedin.android.spyglass.mentions.Mentionable;

import java.util.List;

public class MentionInteractor {
    private static final String TAG = MentionInteractor.class.getSimpleName();
    private static MentionInteractor instance;

    private DatabaseHelper databaseHelper;
    private Context context;

    public static MentionInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new MentionInteractor(context);
        }

        return instance;
    }

    private MentionInteractor(Context context) {
        this.context = context;
        databaseHelper = ApplicationHelper.getDatabaseHelper();
    }
}
