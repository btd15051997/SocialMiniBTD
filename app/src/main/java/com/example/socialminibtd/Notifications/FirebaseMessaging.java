package com.example.socialminibtd.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.socialminibtd.Notifications.ModelNotifi.Token;
import com.example.socialminibtd.R;
import com.example.socialminibtd.Utils.Const;
import com.example.socialminibtd.Utils.Controller;
import com.example.socialminibtd.Utils.PreferenceHelper;
import com.example.socialminibtd.View.Activity.ChatUser.ChatActivity;
import com.example.socialminibtd.View.Activity.PostDetail.PostDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String savedCurrentUser = new PreferenceHelper(this).getMyUid();

        Controller.appLogDebug(Const.LOG_DAT, " onMessageReceived : " + savedCurrentUser);
        /*Now there are two types of notificationType=PostNotification and ChatNotification*/

        String notificationType = remoteMessage.getData().get("notificationType");

        if (notificationType.equals("PostNotification")) {

            String pId = remoteMessage.getData().get("pId");
            String sender = remoteMessage.getData().get("sender");
            String pTitle = remoteMessage.getData().get("pTitle");
            String pDescription = remoteMessage.getData().get("pDescription");

            Controller.appLogDebug(Const.LOG_DAT, " PostNotification :" + pId + "  " + sender + " " + pTitle);

            if (!sender.equals(savedCurrentUser)) {

                showPostNotification(pId, pTitle, pDescription);
            }
        }

        if (notificationType.equals("ChatNotification")) {

            String sent = remoteMessage.getData().get("sent");
            String user = remoteMessage.getData().get("user");

            Controller.appLogDebug(Const.LOG_DAT, " ChatNotification :" + sent + "  " + user);

            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

            if (fuser != null && sent.equals(fuser.getUid())) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    sendOAndAboveNotification(remoteMessage);

                } else {

                    sendNormalNotification(remoteMessage);

                }

            }

        }


    }

    private void showPostNotification(String pId, String pTitle, String pDescription) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            setUpPostNotificationChannel(notificationManager);

        }

        //show postdetail activity using id post when user clicked notification

        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("postId", pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //LargeIcon
        Bitmap LargeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        // sound for notification
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "" + ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(LargeIcon)
                .setContentTitle(pTitle)
                .setSound(notificationSoundUri)
                .setContentText(pDescription)
                .setContentIntent(pendingIntent);

        //show notification
        notificationManager.notify(notificationId, notificationBuilder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpPostNotificationChannel(NotificationManager notificationManager) {

        CharSequence channelName = "New Notification";
        String channelDescription = "Device to device post notification";

        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        adminChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

        if (notificationManager != null) {

            notificationManager.createNotificationChannel(adminChannel);

        }


    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, ChatActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString("hisUid", user);

        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri SoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(SoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0) {
            j = i;
        }

        notificationManager.notify(j, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        //  RemoteMessage.Notification notification = remoteMessage.getNotification();

        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, ChatActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString("hisUid", user);

        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri SoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification andAboveNotification = new OreoAndAboveNotification(this);
        Notification.Builder builder1 = andAboveNotification.getONotifications(title, body, pIntent, SoundUri, icon);


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(Integer.parseInt(icon))
//                .setContentText(body)
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                .setSound(SoundUri)
//                .setContentIntent(pIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0) {
            j = i;
        }

        andAboveNotification.getManager().notify(j, builder1.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String token = s.toString();

        token = FirebaseInstanceId.getInstance().getToken();
        Controller.appLogDebug(Const.LOG_DAT, " Token :" + token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            UpdateToken(token);
        }

    }

    private void UpdateToken(String token) {

        new PreferenceHelper(this).putDeviceToken(token);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token1 = new Token(token);

        reference.child(user.getUid()).setValue(token1);

    }
}
