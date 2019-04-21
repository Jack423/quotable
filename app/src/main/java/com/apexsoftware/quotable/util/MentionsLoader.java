package com.apexsoftware.quotable.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apexsoftware.quotable.model.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MentionsLoader {
    private final Context context;
    private final List<Profile> profileList;

    public MentionsLoader(Context context) {
        this.context = context;
        profileList = loadProfiles();
    }

    private List<Profile> loadProfiles() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/profiles");
        List<Profile> list = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    list.add(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return list;
    }

    private List<Profile> searchProfiles(String query) {
        final List<Profile> searchResults = new ArrayList<>();

        if (query != null) {
            query = query.toLowerCase();
            if (profileList != null && !profileList.isEmpty()) {
                for (Profile profile : profileList) {
                    final String handle = profile.getHandle().toLowerCase();
                    if (handle.startsWith(query)) {
                        searchResults.add(profile);
                    }
                }
            }
        }
    }
}
