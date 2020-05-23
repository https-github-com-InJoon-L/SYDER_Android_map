package com.example.syder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import static com.example.syder.App.FCM_CHANNEL_ID;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
     private static final String TAG = "MyTag";

     @RequiresApi(api = Build.VERSION_CODES.O)
     @Override
     public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
          super.onMessageReceived(remoteMessage);
          Log.d(TAG, "onMessageReceived: called");
          Log.d(TAG, "onMessageReceived: Message received from : " + remoteMessage.getFrom());

          if(remoteMessage.getNotification() != null){
               String title = remoteMessage.getNotification().getTitle();
               String body = remoteMessage.getNotification().getBody();

               Notification notification = new Notification.Builder(this,FCM_CHANNEL_ID)
                       .setSmallIcon(R.drawable.ic_chat_black_24dp)
                       .setContentTitle(title)
                       .setContentText(body)
                       .setColor(Color.BLUE)
                       .build();

               NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
               manager.notify(1002,notification);
          }

          if(remoteMessage.getData().size() > 0){
               Log.d(TAG,"onMessageReceived: data" + remoteMessage.getData().toString());
          }
     }

     @Override
     public void onDeletedMessages() {
          super.onDeletedMessages();
          Log.d(TAG, "onDeletedMessages: called");
     }

     @Override
     public void onNewToken(@NonNull String s) {
          super.onNewToken(s);
          Log.d(TAG, "onNewToken: called");
     }
}
