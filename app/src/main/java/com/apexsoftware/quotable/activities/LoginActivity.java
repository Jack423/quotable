package com.apexsoftware.quotable.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.AuthHelper;
import com.apexsoftware.quotable.R;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText etEmail, etPass;
    TextView textRegister;
    Button btnLogin;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    //ProgressBar progressBar;
    ProgressDialog progressDialog;
    ViewGroup baseView;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //See SignUpActivity for more info about this
        baseView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.textview_no_account);
        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_password);
        //progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(this);
        textRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(LoginActivity.this);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_login:
                login();
                break;

            case R.id.textview_no_account:
                navigateToRegister();
                break;
        }
    }

    /*
     * Intent's are used to navigate in android
     * you need to pass in a "Context" then let the
     * intent know which activity you want to go to
     *
     * Remember to call "startActivity"
     * */
    private void navigateToRegister() {
        Intent registerIntent = new Intent(this, CreateAccountActivity.class);
        startActivity(registerIntent);
    }

    private void login() {
        //Stop if form isn't valid
        if(!formIsValid()) return;

        //progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        //Get important fields
        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        //You should implement a progress dialog...
        btnLogin.setEnabled(false);

        AuthHelper.login(email, password);
        //progressBar.setActivated(true);
        finishLogin();
    }

    /*
     * This one is actually a really cool snippet you might
     * want to hang on to
     *
     * Adding the flag "NEW_TASK" and "CLEAR_TASK" will
     * stop users from pressing back and coming back to
     * the login page
     * */
    private void finishLogin() {
        hideProgress();
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        progressDialog.dismiss();
        startActivity(loginIntent);
    }

    private boolean formIsValid() {
        if(!etEmail.getText().toString().isEmpty()
                && !etPass.getText().toString().isEmpty()) return true;

        Snackbar.make(baseView, "All fields are required", Snackbar.LENGTH_SHORT).show();
        return false;
    }
}
