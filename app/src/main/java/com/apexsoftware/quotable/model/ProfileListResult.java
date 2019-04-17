package com.apexsoftware.quotable.model;

import java.util.ArrayList;
import java.util.List;

public class ProfileListResult {
    boolean isMoreDataAvailable;
    List<Profile> profiles = new ArrayList<>();

    public boolean isMoreDataAvailable() {
        return isMoreDataAvailable;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
