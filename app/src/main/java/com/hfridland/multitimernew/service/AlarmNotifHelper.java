package com.hfridland.multitimernew.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.hfridland.multitimernew.R;
import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.ui.notifalarm.NotifAlarmActivity;
import com.hfridland.multitimernew.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class AlarmNotifHelper {
    private static AlarmNotifHelper mAlarmNotifHelper = null;
    public static AlarmNotifHelper get() {
        if (mAlarmNotifHelper == null) {
            mAlarmNotifHelper = new AlarmNotifHelper();
        }
        return mAlarmNotifHelper;
    }

    private AlarmNotifHelper() { }

    private static final String CHANNEL_ID = "AlarmNotifchannelId";
    public static final int NOTIFICATION_ID = 3;
    public static final int START_NOTIFICATION_ID = 3;

    private static int CUR_NOTIFICATION_ID = START_NOTIFICATION_ID;

    public void showNotification(Context context, TimerItem timerItem) {
        String title  = "Time's up";
        String description = timerItem.getName();

        PendingIntent fullScreenIntent = getFullScreenIntent(context, timerItem.getName(), CUR_NOTIFICATION_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(fullScreenIntent, true)
                .addAction(R.drawable.ic_launcher_foreground, "Dismiss", fullScreenIntent)
                .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        buildChannel(context, notificationManager);
        Notification notification = builder.build();
        notificationManager.notify(CUR_NOTIFICATION_ID, notification);
        CUR_NOTIFICATION_ID++;
        startVibSound(context);
    }

    private void buildChannel(Context context, NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if(nm.getNotificationChannel(CHANNEL_ID) == null) {
                String name = "Example Notification Channel";
                String descriptionText = "This is used to demonstrate the Full Screen Intent";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(descriptionText);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private PendingIntent getFullScreenIntent(Context context, String timerName, int notificationId)  {
        Intent intent = new Intent(context, NotifAlarmActivity.class);
        intent.setAction("StopNotification" + notificationId);
        intent.putExtra("timerName", timerName);
        intent.putExtra("notificationId", notificationId);

        // flags and request code are 0 for the purpose of demonstration
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    int count = 0;
    private static Vibrator mVibrator = null;
    private static MediaPlayer mMediaPlayer;

    private static AudioManager mAudioManager;
    private static int mAudioManagerMode;
    private static boolean mSpeakerphoneOn;

    private void startVibSound(Context context) {
        if (count == 0) {
            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000}, 1));
            } else {
                mVibrator.vibrate(new long[]{0, 1000, 1000}, 1);
            }

            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mAudioManagerMode = mAudioManager.getMode();
            mSpeakerphoneOn = mAudioManager.isSpeakerphoneOn();
            mAudioManager.setMode(AudioManager.STREAM_MUSIC);
            mAudioManager.setSpeakerphoneOn(true);

            mMediaPlayer = MediaPlayer.create(context, R.raw.alarm_clock_digital);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
        count++;
    }

    public void stopVibSound(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        count--;
        if (count == 0) {
            mVibrator.cancel();

            mMediaPlayer.pause();
            mMediaPlayer.stop();

            mAudioManager.setMode(mAudioManagerMode);
            mAudioManager.setSpeakerphoneOn(mSpeakerphoneOn);
        }
    }
}
