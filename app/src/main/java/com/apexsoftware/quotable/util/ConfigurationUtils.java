package com.apexsoftware.quotable.util;
// Created by Jack Butler on 9/10/2018.

import android.content.Context;

import com.apexsoftware.quotable.R;
import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationUtils {
    public static boolean isGoogleMisconfigured(Context context) {
        return AuthUI.UNCONFIGURED_CONFIG_VALUE.equals(
                context.getString(R.string.default_web_client_id));
    }

    public static List<AuthUI.IdpConfig> getConfiguredProviders(Context context) {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(new AuthUI.IdpConfig.PhoneBuilder().build());

        if (!isGoogleMisconfigured(context)) {
            providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        }

        return providers;
    }
}
