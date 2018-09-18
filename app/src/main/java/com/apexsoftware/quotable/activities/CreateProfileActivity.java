package com.apexsoftware.quotable.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.apexsoftware.quotable.util.ValidationUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class CreateProfileActivity extends PickImageActivity implements OnProfileCreatedListener {
    private static final String TAG = CreateProfileActivity.class.getSimpleName();
    public static final String LARGE_IMAGE_URL_EXTRA_KEY = "CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY";

    private EditText nameEditText;
    private ImageView imageView;
    private EditText bioEditText;
    private ProgressBar progressBar;

    private User user;
    private String largeAvatarUrl;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        toolbar = findViewById(R.id.create_profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create Profile");
        toolbar.setTitleTextColor(Color.WHITE);

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        nameEditText = findViewById(R.id.et_name);
        bioEditText = findViewById(R.id.et_bio);

        largeAvatarUrl = getIntent().getStringExtra(LARGE_IMAGE_URL_EXTRA_KEY);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = ProfileManager.getInstance(this).buildProfile(firebaseUser, largeAvatarUrl);

        if (user.getPictureUrl() != null) {
            Glide.with(this)
                    .load(user.getPictureUrl())
                    .into(imageView);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_stub);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });
    }

    @Override
    public ProgressBar getProgressView() {
        return progressBar;
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void onImagePikedAction() {
        startCropImageActivity();
    }

    private void attemptCreateProfile() {

        // Reset errors.
        nameEditText.setError(null);

        // Store values at the time of the login attempt.
        String name = nameEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bio)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        } else if (!ValidationUtil.isNameValid(name)) {
            nameEditText.setError(getString(R.string.error_profile_name_length));
            focusView = nameEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress();
            user.setName(name);
            user.setBio(bio);
            ProfileManager.getInstance(this).createOrUpdateProfile(user, imageUri, this);
            //startActivity(new Intent(CreateProfileActivity.this, MainActivity.class));
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        handleCropImageResult(requestCode, resultCode, data);
    }

    @Override
    public void onProfileCreated(boolean success) {
        hideProgress();

        if (success) {
            finish();
            PreferencesUtil.setProfileCreated(this, success);
            DatabaseHelper.getInstance(CreateProfileActivity.this.getApplicationContext())
                    .addRegistrationToken(FirebaseInstanceId.getInstance().getId(), user.getId());
        } else {
            showSnackBar(R.string.error_fail_create_profile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.continueButton:
                if (hasInternetConnection()) {
                    attemptCreateProfile();
                } else {
                    showSnackBar(R.string.internet_connection_failed);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
