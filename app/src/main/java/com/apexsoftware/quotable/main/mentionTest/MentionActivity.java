package com.apexsoftware.quotable.main.mentionTest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MultiAutoCompleteTextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.main.main.MainPresenter;
import com.apexsoftware.quotable.main.main.MainView;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> implements MentionView {

    private MultiAutoCompleteTextView testQuoteBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);

        testQuoteBox = findViewById(R.id.testQuoteBox);
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
