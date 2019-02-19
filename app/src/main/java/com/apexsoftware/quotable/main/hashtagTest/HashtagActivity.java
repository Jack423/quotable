package com.apexsoftware.quotable.main.hashtagTest;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import java.util.Arrays;
import java.util.List;

public class HashtagActivity extends BaseActivity<HashtagView, HastagPresenter> implements HashtagView {

    private static final List<String> DATA = Arrays.asList("bob", "allice", "dan", "joe", "margret", "daniel", "danna");

    //private com.greenfrvr.hashtagview.HashtagView hashtagView;
    private NachoTextView nachoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);

        //hashtagView = findViewById(R.id.hastags);
        //hashtagView.setData(DATA);

        nachoTextView = findViewById(R.id.tagsView);
        nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
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
