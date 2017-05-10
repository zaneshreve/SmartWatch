package com.example.zane.smartwatch;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.os.Handler;
import android.os.Looper;

public class NotificationListener extends NotificationListenerService {

    //Required override..
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {

        //Have to use a handler because the thread operates outside of the normal
        //workings of the app
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                //Check if the app is in the "send" category
                if (SendScreen.activeApps.contains(sbn.getPackageName())) {
                    //Send package name and stop character (#) to be parsed on Arduino end
                    new SendScreen().new SendThread(sbn.getPackageName() + "#").start();
                }
            }
        });
    }

    //Required override..
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}