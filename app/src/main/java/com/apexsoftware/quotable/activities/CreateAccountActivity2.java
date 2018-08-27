package com.apexsoftware.quotable.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

// Created by Jack Butler on 8/23/2018.

public class CreateAccountActivity2 extends AppCompatActivity implements OnProfileCreatedListener {
    private EditText name, email, password, passwordConfirm;
    private Button register;
    private ProgressDialog progressDialog;

    private ViewGroup baseView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        baseView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        name = findViewById(R.id.et_username);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        passwordConfirm = findViewById(R.id.et_confirm_pass);
        register = findViewById(R.id.btn_register);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseHelper = DatabaseHelper.getInstance(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        if (isFormValid()) {
            progressDialog.setTitle("Creating user");
            progressDialog.setMessage("Please wait while we create your new account");
            progressDialog.show();

            final String emailText = email.getText().toString().trim();
            final String passwordText = password.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(CreateAccountActivity2.this, "Could not create user" + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText);
                        firebaseUser = firebaseAuth.getCurrentUser();
                        buildProfile();
                        finishLogin();
                    }
                }
            });
        }
    }

    private boolean isFormValid() {
        String nameString = name.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String passwordConfirmString = passwordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            name.setError("Please enter a name");
            return false;
        }
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Please enter an email");
            return false;
        }
        if (TextUtils.isEmpty(passwordString)) {
            password.setError("Please enter a password");
            return false;
        } else if (password.length() < 6) {
            password.setError("Password must be greater than 6 characters");
            return false;
        }
        if (TextUtils.isEmpty(passwordConfirmString)) {
            passwordConfirm.setError("Please confirm your password");
            return false;
        }
        if (!TextUtils.equals(passwordString, passwordConfirmString)) {
            password.setError("Password must match");
            return false;
        }

        return true;
    }

    private void buildProfile() {
        User user = new User();

        user.setName(name.getText().toString().trim());
        user.setEmail(email.getText().toString().trim());
        user.setId(firebaseUser.getUid());
        user.setPictureUrl("gs://quotable-c70b9.appspot.com/profile_pictures/493873a2-a182-11e8-98d0-529269fb1459.png");
        user.setBio("Change me");
        user.setFollowers(0);
        user.setFollowing(0);
        user.setPostCount(0);

        databaseHelper.createOrUpdateUser(user, this);

        /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(firebaseUser.getUid()).setValue(user);*/
    }

    private void finishLogin() {
        progressDialog.dismiss();
        startActivity(new Intent(CreateAccountActivity2.this, MainActivity.class));
        finish();
    }

    @Override
    public void onProfileCreated(boolean success) {
        progressDialog.hide();

        if (success) {
            finish();
            PreferencesUtil.setProfileCreated(this, success);
            DatabaseHelper.getInstance(CreateAccountActivity2.this.getApplicationContext())
                    .addRegistrationToken(FirebaseInstanceId.getInstance().getId(), firebaseUser.getUid());
        } else {
            //showSnackBar(R.string.error_fail_create_profile);
            Snackbar.make(baseView, "Failed to create user", Snackbar.LENGTH_SHORT);
        }
    }
}
