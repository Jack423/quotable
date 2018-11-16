package com.apexsoftware.quotable.main.login;

import com.apexsoftware.quotable.main.base.BaseView;
import com.google.firebase.auth.AuthCredential;

public interface LoginView extends BaseView {
    void startCreateProfileActivity();

    void signInWithGoogle();

    void setProfilePhotoUrl(String url);

    void firebaseAuthWithCredentials(AuthCredential credential);
}
