package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.apexsoftware.quotable.R;

public class CreatePostActivity extends AppCompatActivity {
    public static final String TEXT = "text";

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
