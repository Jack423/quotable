package com.apexsoftware.quotable.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.managers.UserManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnPostCreatedListener;
import com.apexsoftware.quotable.models.Post;
import com.apexsoftware.quotable.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class CreatePostActivity extends BaseActivity implements OnPostCreatedListener{
    private static final String TAG = CreatePostActivity.class.getSimpleName();
    public static final String TEXT = "text";
    public static final int CREATE_NEW_POST_REQUEST = 11;

    private boolean creatingPost = true;
    private UserManager userManager;
    private String userID;
    private User user = new User();
    private final Calendar calendar = Calendar.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    EditText mText;

    protected PostManager postManager;
    protected boolean isCreatingPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mText = (EditText) findViewById(R.id.et_post);
        userID = firebaseUser.getUid();
        postManager = PostManager.getInstance(CreatePostActivity.this);
        loadProfile();
    }

    private void loadProfile() {
        userManager = UserManager.getInstance(this);
        userManager.getProfileValue(this, userID, createOnUserChangedListener());
    }

    private OnObjectChangedListener<User> createOnUserChangedListener() {
        return new OnObjectChangedListener<User>() {
            @Override
            public void onObjectChanged(User obj) {
                user = obj;
            }
        };
    }

    private void sendPost(User user, String quote) {
        showProgress("Creating post");
        Post post = new Post();
        post.setUser(user.getName());
        post.setText(quote);
        post.setUserId(firebaseUser.getUid());
        post.setUserImagePath(user.getPictureUrl());
        postManager.createOrUpdatePost(CreatePostActivity.this, post);
        userManager.incrementPostCount(user);
    }

    @Override
    public void onPostSaved(boolean success) {
        hideProgress();

        if (success) {
            setResult(RESULT_OK);
            CreatePostActivity.this.finish();
            Log.d(TAG, "Post was created");
            Toast.makeText(this, "Quote was created", Toast.LENGTH_SHORT).show();
        } else {
            creatingPost = false;
            showSnackBar(R.string.error_fail_create_post);
            Log.d(TAG, "Failed to create a post");
            Toast.makeText(this, "Failed to create a quote", Toast.LENGTH_SHORT).show();
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
            sendPost(user, mText.getText().toString());
        }

        return super.onOptionsItemSelected(item);
    }
}
