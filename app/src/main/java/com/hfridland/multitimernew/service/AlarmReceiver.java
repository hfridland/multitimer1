package com.hfridland.multitimernew.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.ui.alarm.AlarmActivity;
import com.hfridland.multitimernew.ui.timers.TimersActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TimerItem expiredItem = (TimerItem) intent.getSerializableExtra("expiredItem");
        //Toast.makeText(context, "Alarm - " + expiredItem.getName(), Toast.LENGTH_LONG).show();
        Intent intentRun = new Intent(context, TimersActivity.class);
        intentRun.setAction(TickService.ALARM_ACTION);
        intentRun.putExtra(TickService.TIMER_ITEM, expiredItem);
        intentRun.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentRun);
    }
}