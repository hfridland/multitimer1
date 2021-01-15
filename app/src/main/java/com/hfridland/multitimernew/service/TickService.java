package com.hfridland.multitimernew.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.ui.timers.TimersActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;

import com.hfridland.multitimernew.R;
import com.hfridland.multitimernew.utils.StringUtils;

import static java.lang.Math.round;

public class TickService extends Service {
    public static final String ALARM_ACTION = "com.hfridland.multitimer.ticker.ALARM_ACTION";
    public static final String TIMER_ITEM = "TIMER_ITEM";
    public static final String CHANNEL_ALARM = "CHANNEL_ALARM";
    public static final String ALARMNOTIFY_ACTION = "CHANNEL_ALARM";
    public static final int ACTIVETIMERS_NOTIFY = 1;
    public static final int ALARM_NOTIFY = 2;


    private ScheduledExecutorService mScheduledExecutorService;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    public TickService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = getNotificationBuilder();

        mBuilder.setContentTitle("Active Timers:")
                .setSmallIcon(R.drawable.ic_launcher_foreground);
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(this);
        } else {
            String channelId = "timers_channel_id";
            if(mManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new
                        NotificationChannel(channelId, "Active timers",
                        NotificationManager.IMPORTANCE_LOW);

                        mManager.createNotificationChannel(channel);
            }
            return new NotificationCompat.Builder(this, channelId);
        }
    }

    private Notification getNotification(List<TimerItem> timerItems) {
        Intent intent = new Intent(this, TimersActivity.class);
        intent.setAction("");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String contentText = !timerItems.isEmpty() ? "" + timerItems.size() + " active timers" : "";
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(contentText);
        for(TimerItem ti : timerItems) {
            int progress = (int)round((ti.getAlarmTime() - System.currentTimeMillis()) / 1000.0);
            inboxStyle.addLine(ti.getName() + " " + StringUtils.duration2String(progress));
        }

        return mBuilder.setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(inboxStyle)
                .build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, getNotification(new ArrayList<>()));

        final MultitimerDao multitimerDao = AppDelegate.getMultitimerDao();

        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<TimerItem> timerItems = multitimerDao.getActiveTimerItems();
                if (!timerItems.isEmpty()) {
                    mManager.notify(ACTIVETIMERS_NOTIFY, getNotification(timerItems));
                    TimerItem expiredItem = null;
                    StringBuilder sb = new StringBuilder();
                    long t = System.currentTimeMillis();
                    for(TimerItem timerItem : timerItems) {
                        if (timerItem.isActive() && timerItem.getAlarmTime() <= System.currentTimeMillis()) {
                            expiredItem = timerItem;
                            break;
                        }
                    }

                    if (expiredItem != null) {
                        expiredItem.setActive(false);
                        multitimerDao.insertTimerItem(expiredItem);

                        AlarmNotifHelper.get().showNotification(TickService.this, expiredItem);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            AlarmNotifHelper.get().showNotification(TickService.this, expiredItem);
//                        } else {
//                            Intent intentAlarm = new Intent(TickService.this, TimersActivity.class);
//                            intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intentAlarm.setAction(ALARM_ACTION);
//                            intentAlarm.putExtra(TIMER_ITEM, expiredItem);
//                            startActivity(intentAlarm);
//                        }
                    }
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mManager.cancel(1);
        stopForeground(true);
        mScheduledExecutorService.shutdownNow();
    }
}
