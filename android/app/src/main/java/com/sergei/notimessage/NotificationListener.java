package com.sergei.notimessage;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sergei on 7/15/2017.
 */

public class NotificationListener extends NotificationListenerService {

    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("notification");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    //Broadcasts notification information to main activity to be send to server
    public void onNotificationPosted(StatusBarNotification sbn){
        Intent i = new  Intent("notification");

        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");

        CharSequence tickerText = sbn.getNotification().tickerText;
        String content;
        if(tickerText != null){
            content= tickerText.toString();
        } else {
            content = extras.getCharSequence("android.text").toString();
        }

        String appName = getApplicationName(sbn);

        Icon icon = sbn.getNotification().getSmallIcon();
        Drawable drawable = icon.loadDrawable(this);
        byte[] bitmap = convertToByteArray(drawable);

        i.putExtra("content", content + "\n");
        i.putExtra("title", title);
        i.putExtra("appName", appName);
        i.putExtra("bitmap", bitmap);

        sendBroadcast(i);
    }

    private byte[] convertToByteArray(Drawable icon){
        Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //Returns the name of the application that created the notification
    private String getApplicationName(StatusBarNotification sbn){
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( sbn.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }

    public void onNotificationRemoved(StatusBarNotification sbn){}
}
