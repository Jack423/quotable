package com.apexsoftware.quotable.enums;

public enum UploadImagePrefix {

    PROFILE("profile_"), POST("quote_");

    String prefix;

    UploadImagePrefix(String prefix) {
        this.prefix = prefix;
    }

    private String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return getPrefix();
    }
}