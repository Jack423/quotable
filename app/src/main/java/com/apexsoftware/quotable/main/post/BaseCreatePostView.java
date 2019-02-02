package com.apexsoftware.quotable.main.post;
// Created by Jack Butler on 10/8/2018.

import com.apexsoftware.quotable.main.pickImageBase.PickImageView;
import com.hootsuite.nachos.NachoTextView;

import java.util.List;

public interface BaseCreatePostView extends PickImageView {
    void setQuoteError(String error);
    void setContextError(String error);
    //void setNamesError(String error);
    void setTagsError(String error);
    String getQuoteText();
    String getContextText();
    List<String> getTags();
    //String getNamesText();
    void onPostSavedSuccess();
}
