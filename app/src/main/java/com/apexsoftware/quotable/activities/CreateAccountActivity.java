package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.apexsoftware.quotable.util.AuthHelper;
import com.apexsoftware.quotable.R;

public class CreateAccountActivity extends BaseActivity{

    ViewGroup baseView;

    EditText etName, etEmail, etPass, etConfirmPass;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        baseView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        //Setup widgets
        btnRegister = (Button) findViewById(R.id.btn_register);
        etName = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_password);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_pass);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        if (formIsValid()) {
            //Get all the inputs
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String pass = etPass.getText().toString();

            //Later you can make a loading bar instead!
            AuthHelper.registerNewUser(name, email, pass);
            //DatabaseHelper.getInstance(this).createOrUpdateUser();
            finishLogin();
        }
    }

    private void finishLogin() {
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    //Helper function for the form
    private boolean formIsValid() {
        //We add all the EditTexts so we can loop though them
        EditText forms[] = {etName, etEmail, etPass, etConfirmPass};
        boolean valid = true;

        for (EditText et : forms) {
            if (et.getText().toString().isEmpty()) {
                et.setError("Form Required");

                valid = false;
            }
        }

        //Premature return for required forms
        if (!valid) {
            Snackbar.make(baseView, "All forms required", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        //Make sure passwords are equal
        if (!etPass.getText().toString().equals(etConfirmPass.getText().toString())) {
            etPass.setError("Passwords must match");
            etConfirmPass.setError("Passwords must match");

            Snackbar.make(baseView, "Passwords must match", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
