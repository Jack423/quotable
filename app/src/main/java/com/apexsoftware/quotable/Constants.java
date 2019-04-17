package com.apexsoftware.quotable;
//Created by Jack Butler on 7/25/2018.

public class Constants {
    public static class Profile {
        public static final int MAX_AVATAR_SIZE = 1280; //px, side of square
        public static final int MIN_AVATAR_SIZE = 100; //px, side of square
        public static final int MAX_NAME_LENGTH = 120;
        public static final int MAX_HANDLE_LENGTH = 20;
    }

    public static class Post {
        public static final int MAX_TEXT_LENGTH_IN_LIST = 300; //characters
        public static final int POST_AMOUNT_ON_PAGE = 10;
    }

    public static class Database {
        public static final int MAX_UPLOAD_RETRY_MILLIS = 60000; //1 minute
    }

    public static class PushNotification {
        public static final int LARGE_ICONE_SIZE = 256; //px
    }

    public static class General {
        public static final long DOUBLE_CLICK_TO_EXIT_INTERVAL = 3000; // in milliseconds
    }

    public static class Algolia {
        public static final String APPLICATION_ID = "K8DB4XKE4D";
        public static final String SEARCH_KEY = "0ee1cee2b4a5c45657a2165ced5d210d";
        public static final String ADMIN_KEY = "a2d20c8f4645f8925e6529b76a874668";
    }
}
