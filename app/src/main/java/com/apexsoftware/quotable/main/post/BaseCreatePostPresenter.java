package com.apexsoftware.quotable.main.post;
// Created by Jack Butler on 10/8/2018.

import android.content.Context;
import android.text.TextUtils;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.pickImageBase.PickImagePresenter;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.listeners.OnPostCreatedListener;
import com.apexsoftware.quotable.util.LogUtil;

import androidx.annotation.StringRes;

public abstract class BaseCreatePostPresenter<V extends BaseCreatePostView> extends PickImagePresenter<V> implements OnPostCreatedListener {
    protected boolean creatingPost = false;
    protected PostManager postManager;

    public BaseCreatePostPresenter(Context context) {
        super(context);
        postManager = PostManager.getInstance(context);
    }

    @StringRes
    protected abstract int getSaveFailMessage();

    protected abstract void savePost(final String quote, final String description, final String tags);

    protected void attemptCreatePost() {
        ifViewAttached(view -> {
            view.setQuoteError(null);
            view.setContextError(null);
            //view.setNamesError(null);
            view.setTagsError(null);

            String quote = view.getQuoteText().trim();
            String context = view.getContextText().trim();
            String tags = view.getTags();
            //String names = view.getNamesText();

            boolean cancel = false;

            if (TextUtils.isEmpty(quote)) {
                view.setQuoteError("A quote is required");
                cancel = true;
            }

            if (TextUtils.isEmpty(context)) {
                view.setContextError("You need to specify a context for your quote");
                cancel = true;
            }

            if (tags.isEmpty()) {
                view.setTagsError("You need to tag the person that said the quote");
                cancel = true;
            }

            if (TextUtils.isEmpty(tags)) {
                view.setTagsError("You need to specify who made this quote");
                cancel = true;
            }

            if(!cancel) {
                creatingPost = true;
                view.hideKeyboard();
                savePost(quote, context, tags);
            }
        });
    }

    public void doSavePost() {
        if (!creatingPost) {
            if (hasInternetConnection()) {
                attemptCreatePost();
            } else {
                ifViewAttached(view -> view.showSnackBar(R.string.internet_connection_failed));
            }
        }
    }

    @Override
    public void onPostSaved(boolean success) {
        creatingPost = false;

        ifViewAttached(view -> {
            view.hideProgress();
            if (success) {
                view.onPostSavedSuccess();
                LogUtil.logDebug(TAG, "Post was saved");
            } else {
                view.showSnackBar(getSaveFailMessage());
                LogUtil.logDebug(TAG, "Failed to save post");
            }
        });
    }
}
