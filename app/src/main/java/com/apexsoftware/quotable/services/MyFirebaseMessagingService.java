package com.apexsoftware.quotable.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;

import com.apexsoftware.quotable.Constants;
import com.apexsoftware.quotable.R;
import com.apexsoftware.quotable.main.main.MainActivity;
import com.apexsoftware.quotable.main.postDetails.PostDetailsActivity;
import com.apexsoftware.quotable.main.profile.ProfileActivity;
import com.apexsoftware.quotable.managers.PostManager;
import com.apexsoftware.quotable.util.GlideApp;
import com.apexsoftware.quotable.util.ImageUtil;
import com.apexsoftware.quotable.util.LogUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static int notificationId = 0;

    private static final String POST_ID_KEY = "postId";
    private static final String PROFILE_ID_KEY = "profileId";
    private static final String AUTHOR_ID_KEY = "authorId";
    private static final String ACTION_TYPE_KEY = "actionType";
    private static final String TITLE_KEY = "title";
    private static final String BODY_KEY = "body";
    private static final String ICON_KEY = "icon";
    private static final String ACTION_TYPE_NEW_LIKE = "new_like";
    private static final String ACTION_TYPE_NEW_COMMENT = "new_comment";
    private static final String ACTION_TYPE_NEW_POST = "new_post";
    private static final String ACTION_TYPE_NEW_FOLLOWER = "new_follower";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null && remoteMessage.getData().get(ACTION_TYPE_KEY) != null) {
            handleRemoteMessage(remoteMessage);
        } else {
            LogUtil.logError(TAG, "onMessageReceived()", new RuntimeException("FCM remoteMessage doesn't contains Action Type"));
        }
    }

    private void handleRemoteMessage(RemoteMessage remoteMessage) {
        String receivedActionType = remoteMessage.getData().get(ACTION_TYPE_KEY);
        LogUtil.logDebug(TAG, "Message Notification Action Type: " + receivedActionType);

        switch (receivedActionType) {
            case ACTION_TYPE_NEW_LIKE:
                parseCommentOrLike(Channel.NEW_LIKE, remoteMessage);
                break;
            case ACTION_TYPE_NEW_COMMENT:
                parseCommentOrLike(Channel.NEW_COMMENT, remoteMessage);
                break;
            case ACTION_TYPE_NEW_POST:
                handleNewPostCreatedAction(remoteMessage);
                break;
            case ACTION_TYPE_NEW_FOLLOWER :
                handleNewFollower(Channel.NEW_FOLLOWER, remoteMessage);
                break;
        }
    }

    private void handleNewPostCreatedAction(RemoteMessage remoteMessage) {
        String postAuthorId = remoteMessage.getData().get(AUTHOR_ID_KEY);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Send notification for each users except author of post.
        if (firebaseUser != null && !firebaseUser.getUid().equals(postAuthorId)) {
            PostManager.getInstance(this.getApplicationContext()).incrementNewPostsCounter();
        }
    }

    private void parseCommentOrLike(Channel channel, RemoteMessage remoteMessage) {
        String notificationTitle = remoteMessage.getData().get(TITLE_KEY);
        String notificationBody = remoteMessage.getData().get(BODY_KEY);
        String notificationImageUrl = remoteMessage.getData().get(ICON_KEY);
        String postId = remoteMessage.getData().get(POST_ID_KEY);

        Intent backIntent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, postId);

        Bitmap bitmap = getBitmapFromUrl(notificationImageUrl);

        sendNotification(channel, notificationTitle, notificationBody, bitmap, intent, backIntent);
        LogUtil.logDebug(TAG, "Message Notification Body: " + remoteMessage.getData().get(BODY_KEY));
    }

    private void handleNewFollower(Channel channel, RemoteMessage remoteMessage) {
        String notificationTitle = remoteMessage.getData().get(TITLE_KEY);
        String notificationBody = remoteMessage.getData().get(BODY_KEY);
        String profileId = remoteMessage.getData().get(PROFILE_ID_KEY);
        String notificationImageUrl = remoteMessage.getData().get(ICON_KEY);

        Bitmap bitmap = getBitmapFromUrl(notificationImageUrl);

        Intent backIntent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, profileId);

        sendNotification(channel, notificationTitle, notificationBody, bitmap, intent, backIntent);
        LogUtil.logDebug(TAG, "Message Notification Body: " + remoteMessage.getData().get(BODY_KEY));
    }

    @Nullable
    public Bitmap getBitmapFromUrl(String imageUrl) {
        return ImageUtil.loadBitmap(GlideApp.with(this), imageUrl, Constants.PushNotification.LARGE_ICONE_SIZE, Constants.PushNotification.LARGE_ICONE_SIZE);
    }

    private void sendNotification(Channel channel, String notificationTitle, String notificationBody, Bitmap bitmap, Intent intent) {
        sendNotification(channel, notificationTitle, notificationBody, bitmap, intent, null);
    }

    private void sendNotification(Channel channel, String notificationTitle, String notificationBody, Bitmap bitmap, Intent intent, Intent backIntent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent;

        if (backIntent != null) {
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent[] intents = new Intent[]{backIntent, intent};
            pendingIntent = PendingIntent.getActivities(this, notificationId++, intents, PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getActivity(this, notificationId++, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel.id);
        notificationBuilder.setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.ic_push_notification) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setLargeIcon(bitmap)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channel.id, getString(channel.name), importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId(channel.id);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    enum Channel {
        NEW_LIKE("new_like_id", R.string.new_like_channel_name),
        NEW_COMMENT("new_comment_id", R.string.new_comment_channel_name),
        NEW_FOLLOWER("new_follower_id", R.string.new_follower_channel_name);

        String id;
        @StringRes
        int name;

        Channel(String id, @StringRes int name) {
            this.id = id;
            this.name = name;
        }
    }
}
