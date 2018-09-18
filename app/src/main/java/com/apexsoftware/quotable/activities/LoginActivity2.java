package com.apexsoftware.quotable.activities;
// Created by Jack Butler on 8/21/2018.

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity2 extends BaseActivity {
    private static final String TAG = LoginActivity2.class.getSimpleName();

    private Toolbar toolbar;

    private EditText email;
    private EditText password;
    private TextView createAccount;
    private Button loginButton;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        createAccount = findViewById(R.id.tv_create_account);

        progressDialog = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                if(!TextUtils.isEmpty(emailString) || !TextUtils.isEmpty(passwordString)) {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Checking credentials, please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    loginUser(emailString, passwordString);
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity2.this, CreateAccountActivity2.class));
            }
        });
    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    checkIsProfileExist(firebaseAuth.getCurrentUser().getUid());
                    finishSignIn();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity2.this, "Cannot sign in, please check the form and try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkIsProfileExist(final String userId) {
        ProfileManager.getInstance(this).isProfileExist(userId, new OnObjectExistListener<User>() {
            @Override
            public void onDataChanged(boolean exist) {
                if (!exist) {
                    finishSignIn();
                } else {
                    PreferencesUtil.setProfileCreated(LoginActivity2.this, true);
                    DatabaseHelper.getInstance(LoginActivity2.this.getApplicationContext())
                            .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), userId);
                }
                progressDialog.dismiss();
                finish();
            }
        });
    }

    private void finishSignIn() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "New notification token created: " + task.getResult().getToken());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                    reference.child(firebaseAuth.getUid()).child("device_token").setValue(task.getResult().getToken()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent loginIntent = new Intent(LoginActivity2.this, MainActivity.class);
                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressDialog.dismiss();
                            startActivity(loginIntent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
