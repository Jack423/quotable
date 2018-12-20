package com.apexsoftware.quotable.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.editProfile.createProfile.CreateProfileActivity;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    public static final int LOGIN_REQUEST_CODE = 10001;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setLogo(R.drawable.quotable_text_logo_white)
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        ProfileManager.getInstance(this).isProfileExist(userId, new OnObjectExistListener<Profile>() {
            @Override
            public void onDataChanged(boolean exist) {
                if (!exist) {
                    startCreateProfileActivity();
                } else {
                    PreferencesUtil.setProfileCreated(LoginActivity.this, true);
                    DatabaseHelper.getInstance(LoginActivity.this.getApplicationContext())
                            .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), userId);
                }
                finish();
            }
        });
    }

    private void startCreateProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
        //intent.putExtra(CreateProfileActivity.LARGE_IMAGE_URL_KEY, profilePhotoUrlLarge);
    }
}
