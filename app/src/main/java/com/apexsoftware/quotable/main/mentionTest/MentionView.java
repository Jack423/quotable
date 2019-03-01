package com.apexsoftware.quotable.main.mentionTest;

import com.apexsoftware.quotable.main.base.BaseView;
import com.apexsoftware.quotable.model.Mention;

import java.util.List;

//Created By: Jack Butler on 2/28/2019

public interface MentionView extends BaseView {
    void onHandleListReady(List<Mention> mentions);
    void showEmptyListLayout();
}
