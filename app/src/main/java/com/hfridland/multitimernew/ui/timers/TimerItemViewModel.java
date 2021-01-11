package com.hfridland.multitimernew.ui.timers;

import com.hfridland.multitimernew.data.model.TimerItem;

import static java.lang.Math.round;
import static com.hfridland.multitimernew.utils.StringUtils.duration2String;

public class TimerItemViewModel {

    private int mId;
    private String mName;
    private int mDurationSec;
    private boolean mActive;
    private long mAlarmTime;


    public TimerItemViewModel(TimerItem timerItem) {
        mId = timerItem.getId();
        mName = timerItem.getName();
        mDurationSec = timerItem.getDurationSec();
        mActive = timerItem.isActive();
        mAlarmTime = timerItem.getAlarmTime();
    }

    public String getName() {
        return mName;
    }

    public int getDurationSec() {
        return mDurationSec;
    }

    public String getDuration() {
        if (!mActive) {
            int duration = mDurationSec;
            return duration2String(duration);
        } else {
            long t = System.currentTimeMillis();
            long duration = round((mAlarmTime - t) / 1000.0);
            return duration2String(duration);
        }
    }

    public int getProgress() {
        if (!mActive) {
            return 0;
        } else {
            return (int)round((mAlarmTime - System.currentTimeMillis()) / 1000.0);
        }
    }

    public int getId() {
        return mId;
    }

    public boolean isActive() {
        return mActive;
    }
}
