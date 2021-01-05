package com.hfridland.multitimernew.data.database;

import com.hfridland.multitimernew.data.model.TimerItem;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TimerItem.class}, version = 1)
public abstract class MultitimerDatabase extends RoomDatabase {
    public abstract MultitimerDao getMultitimerDao();
}
