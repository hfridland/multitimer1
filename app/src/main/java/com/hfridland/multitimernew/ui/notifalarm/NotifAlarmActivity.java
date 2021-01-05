package com.hfridland.multitimernew.ui.notifalarm;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.R;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.service.AlarmNotifHelper;
import com.hfridland.multitimernew.service.TickService;
import com.hfridland.multitimernew.ui.timers.TimersActivity;
import com.hfridland.multitimernew.utils.StringUtils;

import java.util.concurrent.TimeUnit;

public class NotifAlarmActivity extends AppCompatActivity {
    private Button mBtnClose;
    private TextView mTvTimerName;
    private TextView mTvTime;

    private Disposable mTimerDisposable;
    private long mCurMill = System.currentTimeMillis();
    private int mCnt = 0;

    private final MultitimerDao mMultitimerDao = AppDelegate.getMultitimerDao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notif);

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean locked = km.inKeyguardRestrictedInputMode();
        if (!locked) {
            finish();
        }

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(flags);

        mTvTimerName = findViewById(R.id.tvTimerName);
        String timerName = "";
        Intent intent = getIntent();
        if (intent != null) {
            timerName = intent.getStringExtra("timerName");
            mTvTimerName.setText(timerName);
        }

        mBtnClose = findViewById(R.id.btnClose);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTvTime = findViewById(R.id.tvTime);
        mTimerDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> {
                    long sec = (System.currentTimeMillis() - mCurMill) / 1000;
                    String s = StringUtils.duration2String(sec);
                    mTvTime.setText(s);

                    mTvTime.setVisibility(mCnt % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
                    mCnt++;
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmNotifHelper.get().stopVibSound(this);
        mTimerDisposable.dispose();
        mMultitimerDao.getActiveTimerItemsRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timerItems -> {
                    if (timerItems.isEmpty()) {
                        Intent intentClose = new Intent(this, TickService.class);
                        stopService(intentClose);
                    }
                });

        Intent intent = new Intent(this, TimersActivity.class);
        intent.setAction("");
        startActivity(intent);
    }
}
