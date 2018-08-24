package com.apexsoftware.quotable.managers;
// Created by Jack Butler on 8/3/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.enums.ProfileStatus;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.models.User;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class UserManager extends FirebaseListenersManager{
    private static final String TAG = UserManager.class.getSimpleName();
    private static UserManager instance;

    private Context context;
    private DatabaseHelper databaseHelper;
    private Map<Context, List<ValueEventListener>> activeListeners = new HashMap<>();

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }

        return instance;
    }

    private UserManager(Context context) {
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
        DatabaseReference databaseReference = databaseHelper.getDatabaseReference().child("profiles").child(id);
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

    public void createOrUpdateProfile(final User user, final OnProfileCreatedListener onProfileCreatedListener) {
        /*String imageTitle = ImageUtil.generateImageTitle(UploadImagePrefix.PROFILE, user.getId());
        UploadTask uploadTask = databaseHelper.uploadImage(imageUri, imageTitle);

        if (uploadTask != null) {
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult().getDownloadUrl();
                        LogUtil.logDebug(TAG, "successful upload image, image url: " + String.valueOf(downloadUrl));

                        user.setPhotoUrl(downloadUrl.toString());
                        databaseHelper.createOrUpdateProfile(user, onProfileCreatedListener);

                    } else {
                        onProfileCreatedListener.onProfileCreated(false);
                        LogUtil.logDebug(TAG, "fail to upload image");
                    }

                }
            });
        } else {
            onProfileCreatedListener.onProfileCreated(false);
            LogUtil.logDebug(TAG, "fail to upload image");
        }*/

        databaseHelper.createOrUpdateUser(user, onProfileCreatedListener);
    }

    public User buildProfile(FirebaseUser firebaseUser) {
        User profile = new User();
        profile.setId(firebaseUser.getUid());
        profile.setEmail(firebaseUser.getEmail());
        profile.setName(firebaseUser.getDisplayName());
        profile.setPictureUrl("gs://quotable-c70b9.appspot.com/profile_pictures/493873a2-a182-11e8-98d0-529269fb1459.png");
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
        } else if (!PreferencesUtil.isProfileCreated(context)){
            return ProfileStatus.NO_PROFILE;
        } else {
            return ProfileStatus.PROFILE_CREATED;
        }
    }
}
