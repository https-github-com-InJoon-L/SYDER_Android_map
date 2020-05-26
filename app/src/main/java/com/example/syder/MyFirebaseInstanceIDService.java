package com.example.syder;

import android.app.Activity;
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
               Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
               String title = remoteMessage.getNotification().getTitle();
               String body = remoteMessage.getNotification().getBody();
               String click_action = remoteMessage.getNotification().getClickAction();
               sendNotification(title, body, click_action);
          }

          if(remoteMessage.getData().size() > 0){
               Log.d(TAG,"onMessageReceived: data" + remoteMessage.getData());
          }
     }

     @RequiresApi(api = Build.VERSION_CODES.O)
     private void sendNotification(String title, String messageBody, String click_action) {
          if(title == null) {
               title = "FCM Noti";
          }

          Intent intent = null;
          if(click_action.equals("ConsentActivity")) {
               intent = new Intent(this, ActivityWait.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          }else {
               intent = new Intent(this, ActivityLogin.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          }

          PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                  intent, PendingIntent.FLAG_ONE_SHOT);

          Notification notification = new Notification.Builder(this,FCM_CHANNEL_ID)
                  .setSmallIcon(R.drawable.ic_chat_black_24dp)
                  .setContentTitle(title)
                  .setContentText(messageBody)
                  .setColor(Color.BLUE)
                  .setContentIntent(pendingIntent)
                  .build();

          NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          manager.notify(1002, notification);
     }
     @Override
     public void onDeletedMessages() {
          super.onDeletedMessages();
          Log.d(TAG, "onDeletedMessages: called");
     }

     @Override
     public void onNewToken(@NonNull String s) {
          super.onNewToken(s);
          Log.d(TAG, "onNewToken: called" + s);
     }
}
