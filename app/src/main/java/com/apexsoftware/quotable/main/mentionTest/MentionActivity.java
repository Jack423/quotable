package com.apexsoftware.quotable.main.mentionTest;

import android.os.Bundle;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.LogUtil;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> implements QueryTokenReceiver {

    private static final String TAG = MentionActivity.class.getSimpleName();
    private static final String BUCKET = "handle";
    private ProfileManager profileManager = ProfileManager.getInstance(this);
    private RichEditorView richEditorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mention);
        richEditorView = findViewById(R.id.testQuoteBox);
        richEditorView.setQueryTokenReceiver(this);
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
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        LogUtil.logDebug(TAG, "onQueryRecieved");
        List<String> buckets = Collections.singletonList(BUCKET);
        List<Profile> suggestions = profileManager.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        richEditorView.onReceiveSuggestionsResult(result, BUCKET);
        return buckets;
    }
}
