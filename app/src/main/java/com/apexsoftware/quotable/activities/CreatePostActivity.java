package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.listeners.OnPostCreatedListener;

public class CreatePostActivity extends BaseActivity implements OnPostCreatedListener{
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    public static final String TEXT = "text";
    public static final int CREATE_NEW_POST_REQUEST = 11;

    private boolean creatingPost = true;

    /*
     * Our fields to be used in the class
     */
    EditText mText;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_post);

        /*
         * Get our edit text by id
         * and a text changed listener so we can track when
         * text has changed
         */
        mText = (EditText) findViewById(R.id.et_post);

        //This is how we watch for when text has changed
        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
    }

    @Override
    public void onPostSaved(boolean success) {
        hideProgress();

        if (success) {
            setResult(RESULT_OK);
            CreatePostActivity.this.finish();
            Log.d(TAG, "Post was created");
        } else {
            creatingPost = false;
            showSnackBar(R.string.error_fail_create_post);
            Log.d(TAG, "Failed to create a post");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.post) {
            sendPost();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * We set the results to be the text post
     * so that we can add it to the adapter in the main activity
     * and even send it to the server.
     */
    private void sendPost() {
        Intent data = new Intent();
        data.putExtra(TEXT, mText.getText().toString());
        setResult(RESULT_OK, data);

        finish();
    }
}
