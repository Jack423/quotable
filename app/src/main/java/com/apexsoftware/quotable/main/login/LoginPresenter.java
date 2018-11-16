package com.apexsoftware.quotable.main.login;

import android.content.Context;
import android.net.Uri;

import com.apexsoftware.quotable.Constants;
import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BasePresenter;
import com.apexsoftware.quotable.main.interactors.ProfileInteractor;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.util.LogUtil;
import com.apexsoftware.quotable.util.PreferencesUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

class LoginPresenter extends BasePresenter<LoginView> {

    LoginPresenter(Context context) {
        super(context);
    }

    public void checkIsProfileExist(final String userId) {
        ProfileManager.getInstance(context).isProfileExist(userId, exist -> {
            ifViewAttached(view -> {
                if (!exist) {
                    view.startCreateProfileActivity();
                } else {
                    PreferencesUtil.setProfileCreated(context, true);
                    ProfileInteractor.getInstance(context.getApplicationContext())
                            .addRegistrationToken(FirebaseInstanceId.getInstance().getToken(), userId);
                }

                view.hideProgress();
                view.finish();
            });
        });
    }

    public void onGoogleSignInClick() {
        if (checkInternetConnection()) {
            ifViewAttached(LoginView::signInWithGoogle);
        }
    }

    public void handleGoogleSignInResult(GoogleSignInResult result) {
        ifViewAttached(view -> {
            if (result.isSuccess()) {
                view.showProgress();

                GoogleSignInAccount account = result.getSignInAccount();

                view.setProfilePhotoUrl(buildGooglePhotoUrl(account.getPhotoUrl()));

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                view.firebaseAuthWithCredentials(credential);

                LogUtil.logDebug(TAG, "firebaseAuthWithGoogle:" + account.getId());

            } else {
                LogUtil.logDebug(TAG, "SIGN_IN_GOOGLE failed :" + result);
                view.hideProgress();
            }
        });
    }

    private String buildGooglePhotoUrl(Uri photoUrl) {
        return String.format(context.getString(R.string.google_large_image_url_pattern),
                photoUrl, Constants.Profile.MAX_AVATAR_SIZE);
    }

    public void handleAuthError(Task<AuthResult> task) {
        Exception exception = task.getException();
        LogUtil.logError(TAG, "signInWithCredential", exception);

        ifViewAttached(view -> {
            if (exception != null) {
                view.showWarningDialog(exception.getMessage());
            } else {
                view.showSnackBar(R.string.error_authentication);
            }

            view.hideProgress();
        });
    }
}
