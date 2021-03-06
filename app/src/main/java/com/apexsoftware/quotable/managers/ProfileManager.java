package com.apexsoftware.quotable.managers;
// Created by Jack Butler on 10/4/2018.

import android.content.Context;
import android.net.Uri;

import com.apexsoftware.quotable.enums.ProfileStatus;
import com.apexsoftware.quotable.main.interactors.ProfileInteractor;
import com.apexsoftware.quotable.managers.listeners.OnDataChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.managers.listeners.OnProfileListChangedListener;
import com.apexsoftware.quotable.model.Mention;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.model.ProfileListResult;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.linkedin.android.spyglass.tokenization.QueryToken;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager extends FirebaseListenersManager{
    private static final String TAG = ProfileManager.class.getSimpleName();
    private static ProfileManager instance;

    private Context context;
    private ProfileInteractor profileInteractor;


    public static ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context);
        }

        return instance;
    }

    private ProfileManager(Context context) {
        this.context = context;
        profileInteractor = ProfileInteractor.getInstance(context);
    }

    public Profile buildProfile(FirebaseUser firebaseUser, String largeAvatarURL) {
        Profile profile = new Profile(firebaseUser.getUid());
        profile.setEmail(firebaseUser.getEmail());
        profile.setUsername(firebaseUser.getDisplayName());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("images/").child("profile_" + firebaseUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> profile.setPhotoUrl(uri.toString()));

        return profile;
    }

    public void isProfileExist(String id, final OnObjectExistListener<Profile> onObjectExistListener) {
        profileInteractor.isProfileExist(id, onObjectExistListener);
    }

    public void isHandleExist(String handle, final OnObjectExistListener<Profile> onObjectExistListener) {
        profileInteractor.isHandleExist(handle, onObjectExistListener);
    }

    public void createOrUpdateProfile(Profile profile, OnProfileCreatedListener onProfileCreatedListener) {
        createOrUpdateProfile(profile, null, onProfileCreatedListener);
    }

    public void createOrUpdateProfile(final Profile profile, Uri imageUri, final OnProfileCreatedListener onProfileCreatedListener) {
        if (imageUri == null) {
            profileInteractor.createOrUpdateProfile(profile, onProfileCreatedListener);
        } else {
            profileInteractor.createOrUpdateProfileWithImage(profile, imageUri, onProfileCreatedListener);
        }
    }

    public void getProfileValue(Context activityContext, String id, final OnObjectChangedListener<Profile> listener) {
        ValueEventListener valueEventListener = profileInteractor.getProfile(id, listener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void getProfileSingleValue(String id, final OnObjectChangedListener<Profile> listener) {
        profileInteractor.getProfileSingleValue(id, listener);
    }

    public void getProfiles(final OnObjectChangedListener<Profile> listener) {

    }

    public ProfileStatus checkProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return ProfileStatus.NOT_AUTHORIZED;
        } else if (!PreferencesUtil.isProfileCreated(context)) {
            return ProfileStatus.NO_PROFILE;
        } else {
            return ProfileStatus.PROFILE_CREATED;
        }
    }

    public void search(String searchText, OnDataChangedListener<Profile> onDataChangedListener) {
        closeListeners(context);
        ValueEventListener valueEventListener = profileInteractor.searchProfiles(searchText, onDataChangedListener);
        addListenerToMap(context, valueEventListener);
    }

    /*public void getProfiles(OnProfileListChangedListener<Profile> onDataChangedListener) {
        profileInteractor.getProfilesList(onDataChangedListener);
    }*/

    public void searchHandle(String handleText, OnDataChangedListener<Mention> onDataChangedListener) {
        closeListeners(context);
        ValueEventListener valueEventListener = profileInteractor.searchHandles(handleText, onDataChangedListener);
        addListenerToMap(context, valueEventListener);
    }

    public void addRegistrationToken(String token, String userId) {
        profileInteractor.addRegistrationToken(token, userId);
    }

    public List<Profile> getSuggestions(QueryToken queryToken) {
        String prefix = queryToken.getKeywords().toLowerCase();
        List<Profile> suggestions = new ArrayList<>();

        profileInteractor.getProfilesList(list -> {
            if (list != null) {
                for (Profile profile : list) {
                    String handle = profile.getHandle().toLowerCase();
                    if (prefix.length() == 2) {
                        if (handle.startsWith(prefix)) {
                            suggestions.add(profile);
                        }
                    } else {
                        suggestions.addAll(list);
                    }
                }
            }
        });

        return suggestions;
    }
}
