package com.apexsoftware.quotable.main.mentionTest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseActivity;
import com.apexsoftware.quotable.main.main.MainPresenter;
import com.apexsoftware.quotable.main.main.MainView;
import com.apexsoftware.quotable.model.Mention;
import com.apexsoftware.quotable.util.LogUtil;

import java.util.List;

public class MentionActivity extends BaseActivity<MentionView, MentionPresenter> implements MentionView {

    private MultiAutoCompleteTextView testQuoteBox;
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

        testQuoteBox.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {
            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                int i = cursor;

                if (i > 0) {
                    LogUtil.logDebug(TAG, "textchar " + text.charAt(i - 1));
                }

                while (i > 0 && text.charAt(i - 1) != ' ') {
                    i--;
                }
                while (i < cursor && text.charAt(i) == ' ' || text.charAt(i - 1) == '\n') {
                    i++;
                }

                return i;
            }

            @Override
            public int findTokenEnd(CharSequence text, int cursor) {
                int i = cursor;
                int len = text.length();

                while (i < len) {
                    if (text.charAt(i) == ' ' || text.charAt(i - 1) == '\n') {
                        return i;
                    } else {
                        i++;
                    }
                }

                return len;
            }

            @Override
            public CharSequence terminateToken(CharSequence text) {
                int i = text.length();
                while (i > 0 && text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n') {
                    i--;
                }

                if (i > 0 && text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n') {
                    return text;
                } else {
                    if (text instanceof Spanned) {
                        SpannableString sp = new SpannableString(text + " ");
                        TextUtils.copySpansFrom((Spanned) text, 0 , text.length(),
                                Object.class, sp, 0);
                        return sp;
                    } else {
                        return text + " ";
                    }
                }
            }
        });
    }
}
