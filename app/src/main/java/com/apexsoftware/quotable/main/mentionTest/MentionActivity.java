package com.apexsoftware.quotable.main.mentionTest;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.model.Mention;
import com.apexsoftware.quotable.util.LogUtil;
import com.linkedin.android.spyglass.ui.RichEditorView;

import java.util.List;

import androidx.annotation.NonNull;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> implements MentionView {

    private RichEditorView testQuoteBox;
    private ArrayAdapter<Mention> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        initContentView();
    }

    @NonNull
    @Override
    public MentionPresenter createPresenter() {
        if (presenter == null) {
            return new MentionPresenter(this);
        }

        return presenter;
    }

    @Override
    public void onHandleListReady(List<Mention> mentions) {

    }

    @Override
    public void showEmptyListLayout() {

    }

    private void initContentView() {
        testQuoteBox = findViewById(R.id.testQuoteBox);
        //testQuoteBox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
}
