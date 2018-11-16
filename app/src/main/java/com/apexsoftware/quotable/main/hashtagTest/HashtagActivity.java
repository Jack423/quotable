package com.apexsoftware.quotable.main.hashtagTest;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;

import java.util.Arrays;
import java.util.List;

public class HashtagActivity extends BaseActivity<HashtagView, HastagPresenter> implements HashtagView {

    private static final List<String> DATA = Arrays.asList("bob", "allice", "dan", "joe", "margret", "daniel", "danna");

    private com.greenfrvr.hashtagview.HashtagView hashtagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        hashtagView = findViewById(R.id.hastags);

        hashtagView.setData(DATA);
    }

    @NonNull
    @Override
    public HastagPresenter createPresenter() {
        if (presenter == null) {
            return new HastagPresenter(this);
        }
        return presenter;
    }
}
