package com.apexsoftware.quotable.main.interactors;
// Created by Jack Butler on 10/5/2018.

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.apexsoftware.quotable.ApplicationHelper;
import com.apexsoftware.quotable.enums.UploadImagePrefix;
import com.apexsoftware.quotable.managers.DatabaseHelper;
import com.apexsoftware.quotable.managers.listeners.OnDataChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListener;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListenerSimple;
import com.apexsoftware.quotable.managers.listeners.OnObjectExistListener;
import com.apexsoftware.quotable.managers.listeners.OnProfileCreatedListener;
import com.apexsoftware.quotable.model.Post;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.ImageUtil;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static com.apexsoftware.quotable.managers.DatabaseHelper.IMAGES_STORAGE_KEY;

public class ProfileInteractor {
    private static final String TAG = ProfileInteractor.class.getSimpleName();
    private static ProfileInteractor instance;

    private DatabaseHelper databaseHelper;
    private Context context;

    public static ProfileInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileInteractor(context);
        }

        return instance;
    }

    private ProfileInteractor(Context context) {
        this.context = context;
        databaseHelper = ApplicationHelper.getDatabaseHelper();
    }

    public void createOrUpdateProfile(final Profile profile, final OnProfileCreatedListener onProfileCreatedListener) {
        Task<Void> task = databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.PROFILES_DB_KEY)
                .child(profile.getId())
                .setValue(profile);
        task.addOnCompleteListener(task1 -> {
            onProfileCreatedListener.onProfileCreated(task1.isSuccessful());
            addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), profile.getId());
            LogUtil.logDebug(TAG, "createOrUpdateProfile, success: " + task1.isSuccessful());
        });
    }

    public void createOrUpdateProfileWithImage(final Profile profile, Uri imageUri, final OnProfileCreatedListener onProfileCreatedListener) {
        String imageTitle = ImageUtil.generateImageTitle(UploadImagePrefix.PROFILE, profile.getId());
        databaseHelper.uploadImage(imageUri, imageTitle).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    final StorageReference reference = FirebaseStorage.getInstance().getReference().child(IMAGES_STORAGE_KEY + "/" + imageTitle);
                    return reference.getDownloadUrl();
                } else {
                    throw task.getException();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();

                    profile.setPhotoUrl(downloadUrl);
                    createOrUpdateProfile(profile, onProfileCreatedListener);
                } else {
                    onProfileCreatedListener.onProfileCreated(false);
                    LogUtil.logDebug(TAG, "failed to upload image");
                }
            }
        });
    }

    public void isProfileExist(String id, final OnObjectExistListener<Profile> onObjectExistListener) {
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

    public void isHandleExist(String handle, final OnObjectExistListener<Profile> onObjectExistListener) {
        //DatabaseReference databaseReference = databaseHelper.getDatabaseReference().child("profiles").child()
        //TODO: implement handle checking on profile setup
    }

    public ValueEventListener getProfile(String id, final OnObjectChangedListener<Profile> listener) {
        DatabaseReference databaseReference = databaseHelper.getDatabaseReference().child(DatabaseHelper.PROFILES_DB_KEY).child(id);
        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                listener.onObjectChanged(profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
                LogUtil.logError(TAG, "getProfile(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });
        databaseHelper.addActiveListener(valueEventListener, databaseReference);
        return valueEventListener;
    }

    public void getProfileSingleValue(String id, final OnObjectChangedListener<Profile> listener) {
        DatabaseReference databaseReference = databaseHelper.getDatabaseReference().child(DatabaseHelper.PROFILES_DB_KEY).child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);
                listener.onObjectChanged(profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
                LogUtil.logError(TAG, "getProfileSingleValue(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });
    }

    public void updateProfileLikeCountAfterRemovingPost(Post post) {
        DatabaseReference profileRef = databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.PROFILES_DB_KEY + "/" + post.getAuthorId() + "/likesCount");
        final long likesByPostCount = post.getLikesCount();

        profileRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue != null && currentValue >= likesByPostCount) {
                    mutableData.setValue(currentValue - likesByPostCount);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                LogUtil.logInfo(TAG, "Updating likes count transaction is completed.");
            }
        });

    }

    public void addRegistrationToken(String token, String userId) {
        Task<Void> task = databaseHelper
                .getDatabaseReference()
                .child(DatabaseHelper.PROFILES_DB_KEY)
                .child(userId).child("notificationTokens")
                .child(token).setValue(true);
        task.addOnCompleteListener(task1 -> LogUtil.logDebug(TAG, "addRegistrationToken, success: " + task1.isSuccessful()));
    }

    public void updateRegistrationToken(final String token) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            final String currentUserId = firebaseUser.getUid();

            getProfileSingleValue(currentUserId, new OnObjectChangedListenerSimple<Profile>() {
                @Override
                public void onObjectChanged(Profile obj) {
                    if(obj != null) {
                        addRegistrationToken(token, currentUserId);
                    } else {
                        LogUtil.logError(TAG, "updateRegistrationToken",
                                new RuntimeException("Profile is not found"));
                    }
                }
            });
        }
    }

    public void removeRegistrationToken(String token, String userId) {
        DatabaseReference databaseReference = ApplicationHelper.getDatabaseHelper().getDatabaseReference();
        DatabaseReference tokenRef = databaseReference.child(DatabaseHelper.PROFILES_DB_KEY).child(userId).child("notificationTokens").child(token);
        Task<Void> task = tokenRef.removeValue();
        task.addOnCompleteListener(task1 -> LogUtil.logDebug(TAG, "removeRegistrationToken, success: " + task1.isSuccessful()));
    }

    public ValueEventListener searchProfiles(String searchText, OnDataChangedListener<Profile> onDataChangedListener) {
        DatabaseReference reference = databaseHelper.getDatabaseReference().child(DatabaseHelper.PROFILES_DB_KEY);
        ValueEventListener valueEventListener = getSearchQuery(reference, "username", searchText).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    list.add(profile);
                }
                onDataChangedListener.onListChanged(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtil.logError(TAG, "searchProfiles(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });

        databaseHelper.addActiveListener(valueEventListener, reference);
        return valueEventListener;
    }

    private Query getSearchQuery(DatabaseReference databaseReference, String childOrderBy, String searchText) {
        return databaseReference
                .orderByChild(childOrderBy)
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");
    }
}
