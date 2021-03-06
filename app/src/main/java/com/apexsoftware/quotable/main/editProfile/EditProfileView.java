package com.apexsoftware.quotable.main.editProfile;

import com.apexsoftware.quotable.main.pickImageBase.PickImageView;

public interface EditProfileView extends PickImageView {
    //void onHandleSearchResultsReady(List<Profile> profiles);

    void setName(String username);

    void setProfilePhoto(String photoUrl);

    String getNameText();

    String getBio();

    void setBio(String bio);

    void setHandle(String handle);

    String getHandle();

    void setNameError(String string);

    void setBioError(String string);

    void setHandleError(String string);
}
