package com.apexsoftware.quotable.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.apexsoftware.quotable.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Created By: Jack Butler
//Date: 7/22/2018

public class AuthHelper {
    public static final String TAG = "LoginHelper";

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseUser firebaseUser;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void registerNewUser2(final String name, final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail: Success");
                    firebaseUser = firebaseAuth.getCurrentUser();

                    User user = new User();

                    user.setId(firebaseUser.getUid());
                    user.setEmail(email);
                    user.setName(name);
                    user.setPictureUrl("gs://quotable-c70b9.appspot.com/profile_pictures/493873a2-a182-11e8-98d0-529269fb1459.png");
                    user.setBio("Change me");
                    user.setFollowers(0);
                    user.setFollowing(0);
                    user.setPostCount(0);

                    DatabaseReference reference = database.getReference().child("users");
                    reference.child(firebaseUser.getUid()).setValue(user);

                    Log.d(TAG, "User Created with id" + firebaseUser.getUid());

                    login(email, password);

                } else {
                    Log.w(TAG, "createUserWithEmail: failure", task.getException());
                }
            }
        });
    }

    public static void login(final String email, final String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Log.d(TAG, "Logged in " + firebaseUser.getUid());
                } else {
                    Log.w(TAG, "Sign in failed: " + task.getException());
                }
            }
        });
    }
}
