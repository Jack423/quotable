package com.apexsoftware.quotable.main.userList;

import com.apexsoftware.quotable.main.base.BaseView;

import java.util.List;

import androidx.annotation.StringRes;

public interface UsersListView extends BaseView {

    void onProfilesIdsListLoaded(List<String> list);

    void showLocalProgress();

    void hideLocalProgress();

    void setTitle(@StringRes int title);

    void showEmptyListMessage(String message);

    void hideEmptyListMessage();

    void updateSelectedItem();
}
