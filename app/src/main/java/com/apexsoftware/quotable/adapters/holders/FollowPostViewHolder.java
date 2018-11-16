package com.apexsoftware.quotable.adapters.holders;

import android.view.View;

import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.managers.listeners.OnPostChangedListener;
import com.apexsoftware.quotable.model.FollowingPost;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.util.LogUtil;

public class FollowPostViewHolder extends PostViewHolder {


    public FollowPostViewHolder(View view, OnClickListener onClickListener, BaseActivity activity) {
        super(view, onClickListener, activity);
    }

    public FollowPostViewHolder(View view, OnClickListener onClickListener, BaseActivity activity, boolean isAuthorNeeded) {
        super(view, onClickListener, activity, isAuthorNeeded);
    }

    public void bindData(FollowingPost followingPost) {
        postManager.getSinglePostValue(followingPost.getPostId(), new OnPostChangedListener() {
            @Override
            public void onObjectChanged(Post obj) {
                bindData(obj);
            }

            @Override
            public void onError(String errorText) {
                LogUtil.logError(TAG, "bindData", new RuntimeException(errorText));
            }
        });
    }

}
