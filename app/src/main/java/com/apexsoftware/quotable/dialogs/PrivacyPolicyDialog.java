package com.apexsoftware.quotable.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.managers.listeners.OnDialogClickListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;

// Created by Jack Butler on 11/15/2018

public class PrivacyPolicyDialog extends DialogFragment {
    private static final String TAG = PrivacyPolicyDialog.class.getSimpleName();

    private OnDialogClickListener onDialogClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onDialogClickListener = (OnDialogClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnDialogClickListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView)this.getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String msg = getResources().getString(R.string.dialog_privacy_policy);
        SpannableString m = new SpannableString(msg);
        Linkify.addLinks(m, Linkify.ALL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(m)
                .setPositiveButton("Accept", (dialog, which) -> {
                    onDialogClickListener.onDialogPositiveClick(PrivacyPolicyDialog.this);
                }).setNegativeButton("Decline", (dialog, which) -> {
                    onDialogClickListener.onDialogNegativeClick(PrivacyPolicyDialog.this);
                }).setNeutralButton("Privacy Policy", (dialog, which) -> {
                    onDialogClickListener.onDialogNeutralClick(PrivacyPolicyDialog.this);
        }).setCancelable(false).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return builder.create();
    }
}
