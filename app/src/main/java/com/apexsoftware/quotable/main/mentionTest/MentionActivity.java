package com.apexsoftware.quotable.main.mentionTest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> {

    private static final String TAG = MentionActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);

    }

    @NonNull
    @Override
    public MentionPresenter createPresenter() {
        if (presenter == null) {
            return new MentionPresenter(this);
        }
        return presenter;
    }
}
