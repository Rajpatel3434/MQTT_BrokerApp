package com.example.mqtt_brokerapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.eclipse.paho.android.service.BuildConfig;

import java.util.Timer;
import java.util.TimerTask;



public class MyBackgroundService extends Service {

    private static final String TAG = "MyBackgroundService";

    // Class attributes for Notifications
    public static final String NOTIFICATION_CHANNEL_ID = "com.ebottli.tests.myservice";
    private static String notificationTitle = "Nukon";
    private static String notificationContent = "";
    private static String notificationSubtext = "Running";

    // Attributes for Notifications
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId = 223346;

    // Singleton
    private static MyBackgroundService instance;
    public static MyBackgroundService getInstance() {
        return instance;
    }
    private static boolean isServiceRunning = false;
    public static boolean isRunning() { return isServiceRunning; }

    //


    // Attribute for the tasks
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Log.d(TAG, "Service created");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Notification Channel
            NotificationChannel notificationChannel13 = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "My Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel13.setDescription("This is the channel for my foreground service.");
            notificationChannel13.setSound(null, null);
            notificationChannel13.enableVibration(false);
            notificationChannel13.enableLights(false);

            // Notification Manager
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel13);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "App Service", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);

            // Notification Manager
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(notificationId, getNotificationBuilder().build());
        updateNotification(notificationContent, notificationSubtext);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        isServiceRunning = true;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask()
                                          {
                                              public void run()
                                              {
                                                  Log.d(TAG, "Service called");
                                              }
                                          },
                        0,      // run first occurrence immediatetly
                        2000); // run every two seconds


//                ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
//                exec.scheduleAtFixedRate(new Runnable() {
//                    public void run() {
//                        // code to execute repeatedly
//                    }
//                }, 0, 60, TimeUnit.SECONDS); // execute every 60 seconds

            }
        }, 5000);
        // Add your background task here (e.g., MQTT communication, background processing, etc.).
        // Note: The task should run on a separate thread to avoid blocking the main thread.

        return START_STICKY; // This makes the service continue running until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Cancel the scheduled task
        }
        isServiceRunning = false;
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Notifications
    private NotificationCompat.Builder getNotificationBuilder() {
        if (notificationBuilder == null) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pIntent = null ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                pIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE );
            }

            notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText("")
                    .setSubText("")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pIntent)   //Do not start anymore the activity when user clicks on the notif
                    .setOngoing(true)
                    .setShowWhen(false)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
        return notificationBuilder;
    }


    private void updateNotification() {
        try {
            notificationTitle = "";

            // Nexus5
            String versionName = BuildConfig.VERSION_NAME;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                // Nexus5
                NotificationCompat.Builder nb = getNotificationBuilder();
                nb.setContentTitle(notificationTitle);
                nb.setContentText(notificationContent);
                nb.setSubText(notificationSubtext);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(notificationId, nb.build());
            } else {
                // S7
                NotificationCompat.Builder nb = getNotificationBuilder();
                nb.setSubText(" " + notificationTitle );
                nb.setContentTitle(notificationContent);
                nb.setContentText(notificationSubtext);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(notificationId, nb.build());
            }
        } catch (Exception e) {
            Log.e(TAG, "E210", e);
        }
    }

    public void updateNotification(String content, String subtext) {
        if (content != null) {
            notificationContent = content;
        }
        if (subtext != null) {
            notificationSubtext = subtext;
        }
        updateNotification();
    }


}