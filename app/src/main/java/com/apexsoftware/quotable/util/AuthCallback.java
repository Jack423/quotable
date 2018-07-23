package com.apexsoftware.quotable.util;

import com.firebase.client.AuthData;

public interface AuthCallback {
    void onSuccess(AuthData authData);
    void onError(String message);
}
