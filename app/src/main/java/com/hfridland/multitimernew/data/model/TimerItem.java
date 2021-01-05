package com.hfridland.multitimernew.data.model;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TimerItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "duration_sec")
    private int mDurationSec;

    @ColumnInfo(name = "active")
    private boolean mActive;

    @ColumnInfo(name = "alarmTime")
    private long mAlarmTime;

    @Ignore
    public TimerItem(String name, int durationSec) {
        mName = name;
        mDurationSec = durationSec;
        mActive = false;
        mAlarmTime = Long.MAX_VALUE;
    }

    public TimerItem() {
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getDurationSec() {
        return mDurationSec;
    }

    public boolean isActive() {
        return mActive;
    }

    public long getAlarmTime() {
        return mAlarmTime;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setDurationSec(int durationSec) {
        mDurationSec = durationSec;
    }

    public void setActive(boolean active) {
        mActive = active;
        if (mActive) {
            mAlarmTime = System.currentTimeMillis() + mDurationSec * 1000;
        } else {
            mAlarmTime = Long.MAX_VALUE;
        }
    }

    public void setAlarmTime(long alarmTime) {
        mAlarmTime = alarmTime;
    }

    @Override
    public String toString() {
        return "Id: " + mId + " Name: " + mName + " Duration: " + mDurationSec + " Active: " + isActive();
    }
}
