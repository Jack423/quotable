package com.apexsoftware.quotable.main.pickImageBase;

import android.net.Uri;

import com.apexsoftware.quotable.main.base.BaseView;

public interface PickImageView extends BaseView {
    void hideLocalProgress();

    void loadImageToImageView(Uri imageUri);
}
