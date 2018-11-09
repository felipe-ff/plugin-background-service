package com.red_folder.phonegap.plugin.backgroundservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.os.Bundle;

import io.ionic.starter.MyService;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends NotificationListenerService {
    //http://developer.android.com/reference/android/service/notification/NotificationListenerService.html

    private static final String TAG = NotificationService.class.getSimpleName();

    //TODO store this in config
    private static final String IGNORE_PKG = "snapdragon,com.google.android.googlequicksearchbox";
    private static int notificationId = 1;

    private static List<StatusBarNotification> notifications ;
    public static boolean enabled = false;
    private static Context context ;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        enabled = true;

        notifications = new ArrayList<StatusBarNotification>();
        context = this;
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        enabled = false;
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //Do not send notifications from this app (can cause an infinite loop)
        Log.d(TAG, "notification package name " + sbn.getPackageName());

        String pk = sbn.getPackageName();

        if (pk.equals("android") ||  ignorePkg(pk) || sbn.isOngoing()) Log.d(TAG, "Ignore notification from pkg " + pk);
        else {
            Bundle extras = sbn.getNotification().extras;
            String packageName = sbn.getPackageName().toUpperCase();
            String title = getExtra(extras, "android.title").toUpperCase();
            String text = getExtra(extras, "android.text").toUpperCase();
            if ( title.contains("28824") || title.contains("NUBANK") || title.contains("CARTÕES") || title.contains("NEXT") || packageName.contains("PUSHBULLET") ) {
              if ( text.contains("COMPRA") ) {
                MyService.mHelloTo += getExtra(extras, "android.title") + " - " + getExtra(extras, "android.text") + "%-%";
                //NotificationCommands.notifyListener(sbn);
                addNotification(sbn);
              }
            }
        }
    }
    private boolean ignorePkg(String pk){
        for(String s: IGNORE_PKG.split(",")) if (pk.contains(s)) return true;
        return false;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //debugNotification(sbn);
    }

    private void addNotification(StatusBarNotification msg) {
        notifications.add(msg);
    }

    public static void removeAll(){
      try {
         for (StatusBarNotification n : notifications) remove(n);
         notifications.clear();
      } catch (Exception e){
         Log.e(TAG, "Unable to remove notifications",e);
      }
    }

    private static void remove(StatusBarNotification n) {
      String ns = Context.NOTIFICATION_SERVICE;
      NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(ns);

      int id = n.getId();
      String tag = n.getTag();
      Log.i("Cancelling notification ", tag + ", " + id);
      nMgr.cancel(tag, id);
    }

    private static String getExtraLines(Bundle extras, String extra) {
      try {
          CharSequence[] lines = extras.getCharSequenceArray(extra);
          return lines[lines.length-1].toString();
      } catch( Exception e) {
          Log.d(TAG, "Unable to get extra lines " + extra);
          return "";
      }
    }

    private static String getExtra(Bundle extras, String extra) {
        try {
            return extras.get(extra).toString();
        } catch( Exception e) {
            return "";
        }
    }
}
