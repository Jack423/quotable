package com.apexsoftware.quotable.activities;
// Created by Jack Butler on 9/10/2018.

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

public class LoginActivity3 extends AppCompatActivity{
    private static final String TAG = LoginActivity3.class.getSimpleName();

    private static final int RC_SIGN_IN = 100;

    private String profilePhotoUrlLarge;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);

        if(auth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.drawable.quotable_text_logo_white)
                            .build(), RC_SIGN_IN);
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);

        //Successfully signed in
        if(resultCode == RESULT_OK) {
            checkIsProfileExist(auth.getCurrentUser().getUid());
            finish();
        } else {
            if (response == null) {
                Snackbar.make(getCurrentFocus(), "Sign in canceled", Snackbar.LENGTH_SHORT);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Snackbar.make(getCurrentFocus(), "Not connected to the internet", Snackbar.LENGTH_SHORT);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.EMAIL_MISMATCH_ERROR) {
                Snackbar.make(getCurrentFocus(), "Email or password are incorrect", Snackbar.LENGTH_SHORT);
                return;
            }

            Snackbar.make(getCurrentFocus(), "Unknown error ocurred", Snackbar.LENGTH_SHORT);
            Log.e(TAG, "Sign in error: ", response.getError());
        }
    }

    private void checkIsProfileExist(final String userId) {
        ProfileManager.getInstance(this).isProfileExist(userId, new OnObjectExistListener<User>() {
            @Override
            public void onDataChanged(boolean exist) {
                if (!exist) {
                    startCreateProfileActivity();
                } else {
                    PreferencesUtil.setProfileCreated(LoginActivity3.this, true);
                    DatabaseHelper.getInstance(LoginActivity3.this.getApplicationContext())
                            .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), userId);
                }
                finish();
            }
        });
    }

    private void startCreateProfileActivity() {
        Intent intent = new Intent(LoginActivity3.this, CreateProfileActivity.class);
        intent.putExtra(CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY, profilePhotoUrlLarge);
        startActivity(intent);
    }
}
