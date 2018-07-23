package com.apexsoftware.quotable.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.apexsoftware.quotable_v3.R;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    public void showProgress() {
        showProgress("Loading...");
    }

    public void showProgress(String message) {
        hideProgress();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
