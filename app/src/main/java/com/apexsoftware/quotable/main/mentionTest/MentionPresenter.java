package com.apexsoftware.quotable.main.mentionTest;

import android.app.Activity;
import android.content.Context;

import com.apexsoftware.quotable.main.base.BasePresenter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.firebase.auth.FirebaseAuth;

public class MentionPresenter extends BasePresenter<MentionView> {

    private ProfileManager profileManager;

    public MentionPresenter(Activity activity) {
        super(activity);
        profileManager = ProfileManager.getInstance(context.getApplicationContext());
    }

    public void autoCompleteHandle(String text) {
        if (checkInternetConnection()) {
            profileManager.searchHandle(text, list -> {
                ifViewAttached(view -> {
                    view.onHandleListReady(list);

                    if (list.isEmpty()) {
                        view.showEmptyListLayout();
                    }
                });

                LogUtil.logDebug(TAG, "handle search: " + text);
                LogUtil.logDebug(TAG, "found items count: " + list.size());
            });
        } else {

        }
    }
}
