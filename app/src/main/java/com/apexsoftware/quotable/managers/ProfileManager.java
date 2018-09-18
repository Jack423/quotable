package com.apexsoftware.quotable.managers;
// Created by Jack Butler on 8/3/2018.

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.activities.MainActivity;
import com.apexsoftware.quotable.enums.ProfileStatus;
import com.apexsoftware.quotable.enums.UploadImagePrefix;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.ImageUtil;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileManager extends FirebaseListenersManager {
    private static final String TAG = ProfileManager.class.getSimpleName();
    private static ProfileManager instance;

    private Context context;
    private DatabaseHelper databaseHelper;
    private Map<Context, List<ValueEventListener>> activeListeners = new HashMap<>();

    public static ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context);
        }

        return instance;
    }

    private ProfileManager(Context context) {
        this.context = context;
        databaseHelper = ApplicationHelper.getDatabaseHelper();
    }

    public void getProfileValue(Context activityContext, String id, final OnObjectChangedListener<User> listener) {
        ValueEventListener valueEventListener = databaseHelper.getProfile(id, listener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void getProfileSingleValue(String id, final OnObjectChangedListener<User> listener) {
        databaseHelper.getProfileSingleValue(id, listener);
    }

    public void isProfileExist(String id, final OnObjectExistListener<User> onObjectExistListener) {
        DatabaseReference databaseReference = databaseHelper.getDatabaseReference().child("users").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onObjectExistListener.onDataChanged(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createOrUpdateProfile(final User user, Uri imageUri, final OnProfileCreatedListener onProfileCreatedListener) {
        String imageTitle = ImageUtil.generateImageTitle(UploadImagePrefix.PROFILE, user.getId());
        databaseHelper.uploadImage(imageUri, imageTitle, user);

        /*if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onProfileCreatedListener.onProfileCreated(false);
                    Log.d(TAG, "Failed to upload image: " + e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    Log.d(TAG, "successful upload image, image url: " + String.valueOf(downloadUrl));

                    user.setPictureUrl(downloadUrl.toString());
                    databaseHelper.createOrUpdateUser(user, onProfileCreatedListener);
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            });
        } else {
            onProfileCreatedListener.onProfileCreated(false);
            Log.d(TAG, "fail to upload image");
        }*/

        databaseHelper.createOrUpdateUser(user, onProfileCreatedListener);
    }

    public User buildProfile(FirebaseUser firebaseUser, String largeAvatarURL) {
        User profile = new User();
        profile.setId(firebaseUser.getUid());
        profile.setEmail(firebaseUser.getEmail());
        profile.setName(firebaseUser.getDisplayName());
        if (largeAvatarURL != null) {
            profile.setPictureUrl(largeAvatarURL != null ? largeAvatarURL : firebaseUser.getPhotoUrl().toString());
        } else {
            profile.setPictureUrl("https://firebasestorage.googleapis.com/v0/b/quotable-c70b9.appspot.com/o/ic_profile.png?alt=media&token=28e3f4fb-811a-49c7-b2ba-34b6c4f2d1cd");
        }

        return profile;
    }

    public void incrementPostCount(User user) {
        int val = user.getPostCount() + 1;
        databaseHelper.updatePostCount(user, val);
    }

    public void incrementFollowingCount(User user) {
        int val = user.getFollowing() + 1;
        databaseHelper.updateFollowingCount(user, val);
    }

    public void incrementFollowersCount(User user) {
        int val = user.getFollowers() + 1;
        databaseHelper.updateFollowerCount(user, val);
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
}
