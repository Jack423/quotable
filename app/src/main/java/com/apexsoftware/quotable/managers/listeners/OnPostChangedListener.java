package com.apexsoftware.quotable.managers.listeners;

import com.apexsoftware.quotable.model.Post;

public interface OnPostChangedListener {
    public void onObjectChanged(Post obj);

    public void onError(String errorText);
}
