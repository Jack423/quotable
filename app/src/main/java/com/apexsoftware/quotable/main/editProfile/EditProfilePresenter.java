package com.apexsoftware.quotable.main.editProfile;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.base.BaseView;
import com.apexsoftware.quotable.main.pickImageBase.PickImagePresenter;
import com.apexsoftware.quotable.managers.ProfileManager;
import com.apexsoftware.quotable.managers.listeners.OnObjectChangedListenerSimple;
import com.apexsoftware.quotable.model.Handle;
import com.apexsoftware.quotable.model.Profile;
import com.apexsoftware.quotable.util.ValidationUtil;

public class EditProfilePresenter<V extends EditProfileView> extends PickImagePresenter<V> {

    protected Profile profile;
    protected Handle handle;
    protected ProfileManager profileManager;

    protected EditProfilePresenter(Context context) {
        super(context);
        profileManager = ProfileManager.getInstance(context.getApplicationContext());

    }

    public void loadProfile() {
        ifViewAttached(BaseView::showProgress);
        profileManager.getProfileSingleValue(getCurrentUserId(), new OnObjectChangedListenerSimple<Profile>() {
            @Override
            public void onObjectChanged(Profile obj) {
                profile = obj;
                ifViewAttached(view -> {
                    if (profile != null) {
                        view.setName(profile.getUsername());
                        view.setBio(profile.getBio());
                        view.setHandle(profile.getHandle());

                        if (profile.getPhotoUrl() != null) {
                            view.setProfilePhoto(profile.getPhotoUrl());
                        }
                    }

                    view.hideProgress();
                    view.setNameError(null);
                });
            }
        });
    }

    public void attemptCreateProfile(Uri imageUri) {
        if (checkInternetConnection()) {
            ifViewAttached(view -> {
                view.setNameError(null);

                String name = view.getNameText().trim();
                String bio = view.getBio();
                String handle = view.getHandle();
                boolean cancel = false;

                if (TextUtils.isEmpty(name)) {
                    view.setNameError(context.getString(R.string.error_field_required));
                    cancel = true;
                } else if (!ValidationUtil.isNameValid(name)) {
                    view.setNameError(context.getString(R.string.error_profile_name_length));
                    cancel = true;
                } else if (TextUtils.isEmpty(bio)) {
                    view.setBioError("This field is required");
                    cancel = true;
                } else if (TextUtils.isEmpty(handle)) {
                    view.setHandleError("This field is required");
                    cancel = true;
                } else if (!ValidationUtil.isHandleValid(handle)) {
                    view.setHandleError("Max length of the handle should be less that 20 characters");
                    cancel = true;
                }

                if (!cancel) {
                    view.showProgress();
                    profile.setUsername(name);
                    profile.setBio(bio);
                    profile.setHandle(handle);

                    createOrUpdateProfile(imageUri);
                }
            });
        }
    }

    private void createOrUpdateProfile(Uri imageUri) {
        profileManager.createOrUpdateProfile(profile, imageUri, success -> {
            ifViewAttached(view -> {
                view.hideProgress();
                if (success) {
                    onProfileUpdatedSuccessfully();
                } else {
                    view.showSnackBar(R.string.error_fail_create_profile);
                }
            });
        });
    }

    protected void onProfileUpdatedSuccessfully() {
        ifViewAttached(BaseView::finish);
    }

}
