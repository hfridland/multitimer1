package com.hfridland.multitimernew.ui.alarm;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hfridland.multitimernew.data.model.TimerItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hfridland.multitimernew.R;

public class AlarmFragment extends Fragment {
    public static final String TIMER_ITEM = "TIMER_ITEM";

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    private TextView mTvAlarmMsg;
    private Button mBtnClose;
    private TimerItem mTimerItem;

    AudioManager mAudioManager;
    MediaPlayer mMediaPlayer;
    Vibrator mVibrator;

    private int mAudioManagerMode;
    private boolean mSpeakerphoneOn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTvAlarmMsg = view.findViewById(R.id.tv_alarm_msg);
        mBtnClose = view.findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            getActivity().setTitle("Timer Alarm");

            Intent intent = getActivity().getIntent();

            String name = intent.getStringExtra("NAME");
//            mTimerItem = (TimerItem) intent.getSerializableExtra(TIMER_ITEM);
            mTvAlarmMsg.setText(name);

            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManagerMode = mAudioManager.getMode();
            mSpeakerphoneOn = mAudioManager.isSpeakerphoneOn();
            mAudioManager.setMode(AudioManager.STREAM_MUSIC);
            mAudioManager.setSpeakerphoneOn(true);

            mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.alarm_clock_digital);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 2000, 1000};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
            } else {
                mVibrator.vibrate(pattern, 1);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mVibrator.cancel();
        mMediaPlayer.stop();

        mAudioManager.setMode(mAudioManagerMode);
        mAudioManager.setSpeakerphoneOn(mSpeakerphoneOn);
    }
}
