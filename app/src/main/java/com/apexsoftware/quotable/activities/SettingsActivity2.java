package com.apexsoftware.quotable.activities;

import android.content.Intent;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import io.grpc.Context;

public class SettingsActivity2 extends AppCompatPreferenceActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView profilePhoto;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings3);
        addPreferencesFromResource(R.xml.settings);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.icons));

        profilePhoto = findViewById(R.id.iv_profile_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindPreferenceSummaryToValue(findPreference("setting_name"));
        bindPreferenceSummaryToValue(findPreference("setting_bio"));

        //updatePreferenceValues();
        //initUiValues();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if(preference instanceof EditTextPreference) {
                if (preference.getKey().equals("setting_name")) {
                    // update the changed name to summary filed
                    preference.setSummary(stringValue);
                    Toast.makeText(SettingsActivity2.this, "onPreferenceChangedListener Called", Toast.LENGTH_LONG).show();
                } else {
                    preference.setSummary(stringValue);
                    Toast.makeText(SettingsActivity2.this, "onPreferenceChangedListener 2 Called", Toast.LENGTH_LONG).show();
                }
            }
            //preference.setSummary(stringValue);
            return false;
        }
    };

    private void initUiValues() {
        final DatabaseReference reference = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    collapsingToolbarLayout.setTitle(user.getName());
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(user.getPictureUrl());

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(SettingsActivity2.this).load(uri).into(profilePhoto);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(collapsingToolbarLayout, "Database read canceled, please log in", Snackbar.LENGTH_LONG);
            }
        });
    }

    private void updatePreferenceValues(String newValue) {
        DatabaseReference reference = firebaseDatabase.getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }


}
