package com.hfridland.multitimernew;

import android.app.Application;

import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.data.database.MultitimerDatabase;

import androidx.room.Room;

public class AppDelegate extends Application {

    private static MultitimerDao sMultitimerDao;

    public static MultitimerDao getMultitimerDao() {
        return sMultitimerDao;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sMultitimerDao =
                Room.databaseBuilder(getApplicationContext(), MultitimerDatabase.class, "multitimer_database")
                //.allowMainThreadQueries()
                .build().getMultitimerDao();
    }

}
