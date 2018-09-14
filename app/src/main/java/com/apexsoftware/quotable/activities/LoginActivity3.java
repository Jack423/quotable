package com.apexsoftware.quotable.activities;
// Created by Jack Butler on 9/10/2018.

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.util.ConfigurationUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

public class LoginActivity3 extends AppCompatActivity{
    private static final String TAG = LoginActivity3.class.getSimpleName();

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";

    private static final int RC_SIGN_IN = 100;

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
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);

        //Successfully signed in
        if(resultCode == RESULT_OK) {
            startActivity(MainActivity.createIntent(this));
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
}
