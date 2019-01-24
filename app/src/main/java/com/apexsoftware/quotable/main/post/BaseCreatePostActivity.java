package com.apexsoftware.quotable.main.post;
// Created by Jack Butler on 10/8/2018.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.pickImageBase.PickImageActivity;
import com.hootsuite.nachos.NachoTextView;

public abstract class BaseCreatePostActivity<V extends BaseCreatePostView, P extends BaseCreatePostPresenter<V>> extends PickImageActivity<V, P> implements BaseCreatePostView {

    protected ProgressBar progressBar;
    protected EditText quoteEditText;
    protected EditText contextEditText;
    //protected EditText namesEditText;
    protected NachoTextView tags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_create_post_activity_new);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        quoteEditText = findViewById(R.id.et_quote);
        contextEditText = findViewById(R.id.et_description);
        //namesEditText = findViewById(R.id.et_names);
        tags = findViewById(R.id.et_tag);
    }

    @Override
    protected ProgressBar getProgressView() {
        return null;
    }

    @Override
    protected ImageView getImageView() {
        return null;
    }

    @Override
    protected void onImagePikedAction() {

    }

    @Override
    public void setQuoteError(String error) {
        quoteEditText.setError(error);
        quoteEditText.requestFocus();
    }

    @Override
    public void setContextError(String error) {
        contextEditText.setError(error);
        contextEditText.requestFocus();
    }

    @Override
    public void setNamesError(String error) {
        //namesEditText.setError(error);
        //namesEditText.requestFocus();
    }

    @Override
    public String getQuoteText() {
        return quoteEditText.getText().toString();
    }

    @Override
    public String getContextText() {
        return contextEditText.getText().toString();
    }

    @Override
    public String getNamesText() {
        //return namesEditText.getText().toString();
    }

    @Override
    public void onPostSavedSuccess() {
        setResult(RESULT_OK);
        this.finish();
    }
}
