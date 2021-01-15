package com.hfridland.multitimernew.service;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.ui.notifalarm.NotifAlarmActivity;
import com.hfridland.multitimernew.ui.timers.TimersActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().startsWith("StopNotification")) {
            int notificationId = intent.getIntExtra("notificationId", -1);
            String timerName = intent.getStringExtra("timerName");

            MultitimerDao multitimerDao = AppDelegate.getMultitimerDao();
            multitimerDao.getActiveTimerItemsRx()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(timerItems -> {
                        if (timerItems.isEmpty()) {
                            Intent intentClose = new Intent(context, TickService.class);
                            context.stopService(intentClose);
                            Intent intent1 = new Intent(context, TimersActivity.class);
                            intent1.setAction("UpdateAdapterData");
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    });


            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean locked = km.inKeyguardRestrictedInputMode();
            if (!locked) {
                AlarmNotifHelper.get().stopVibSound(context, notificationId);
            } else {
                Intent intent1 = new Intent(context, NotifAlarmActivity.class);
                intent1.putExtra("timerName", timerName);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }
    }
}