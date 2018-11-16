package com.apexsoftware.quotable.main.search.users;

import com.apexsoftware.quotable.main.base.BaseFragmentView;
import com.apexsoftware.quotable.model.Profile;

import java.util.List;

public interface SearchUsersView extends BaseFragmentView {
    void onSearchResultsReady(List<Profile> profiles);

    void showLocalProgress();

    void hideLocalProgress();

    void showEmptyListLayout();

    void updateSelectedItem();
}
