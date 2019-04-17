package com.apexsoftware.quotable.main.mentionTest;

import android.app.Activity;
import android.content.Context;

import com.apexsoftware.quotable.main.base.BasePresenter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.firebase.auth.FirebaseAuth;

public class MentionPresenter extends BasePresenter<MentionView> {

    private Activity activity;

    public MentionPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }
}
