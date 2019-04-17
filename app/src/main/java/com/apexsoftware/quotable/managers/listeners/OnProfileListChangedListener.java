package com.apexsoftware.quotable.managers.listeners;
//Created by Jack Butler on 4/4/2019

import com.apexsoftware.quotable.model.ProfileListResult;

public interface OnProfileListChangedListener<Profile> {
    public void onListChanged(ProfileListResult result);

    void onCanceled(String message);
}
