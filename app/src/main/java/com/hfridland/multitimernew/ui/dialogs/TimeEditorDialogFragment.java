package com.hfridland.multitimernew.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.data.model.TimerItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.hfridland.multitimernew.R;
import com.hfridland.multitimernew.ui.timers.TimersActivity;


public class TimeEditorDialogFragment extends DialogFragment {
    private TimerItem mTimerItem;

    private NumberPicker mNumpickerHours;
    private NumberPicker mNumpickerMinutes;
    private NumberPicker mNumpickerSeconds;

    private EditText mEtName;
    private NoticeDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int id = getArguments().getInt("id");
        final MultitimerDao multitimerDao = AppDelegate.getMultitimerDao();
        if (id >= 0) {
            multitimerDao.getTimerItemByIdRx(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timerItem -> {
                    mTimerItem = timerItem;

                    mEtName.setText(mTimerItem.getName());

                    int duration = mTimerItem.getDurationSec();
                    int h = duration / 3600;
                    duration %= 3600;
                    int m = duration / 60;
                    int s = duration % 60;

                    mNumpickerHours.setValue(h);
                    mNumpickerMinutes.setValue(m);
                    mNumpickerSeconds.setValue(s);
                });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dlg_timereditor, null);
        setupView(v);
        builder.setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mTimerItem == null) {
                            mTimerItem = new TimerItem();
                        }

                        mTimerItem.setName(mEtName.getText().toString());
                        mTimerItem.setDurationSec(getDurationSec());

                        Single.fromCallable(() -> multitimerDao.insertTimerItem(mTimerItem))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(id -> {
                                mListener.onDialogPositiveClick(TimeEditorDialogFragment.this);
                            });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

        return builder.create();
    }

    private int getDurationSec() {
        int h = mNumpickerHours.getValue();
        int m = mNumpickerMinutes.getValue();
        int s = mNumpickerSeconds.getValue();
        return h * 3600 + m * 60 + s;
    }

    private void setupView(View v) {
        mEtName = v.findViewById(R.id.et_name);

        mNumpickerHours = v.findViewById(R.id.numpickerHours);
        mNumpickerHours.setMinValue(0);
        mNumpickerHours.setMaxValue(23);

        mNumpickerMinutes = v.findViewById(R.id.numpickerMinutes);
        mNumpickerMinutes.setMinValue(0);
        mNumpickerMinutes.setMaxValue(59);

        mNumpickerSeconds = v.findViewById(R.id.numpickerSeconds);
        mNumpickerSeconds.setMinValue(0);
        mNumpickerSeconds.setMaxValue(59);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        for(Fragment fragment : ((TimersActivity) context).getSupportFragmentManager().getFragments()) {
            if (fragment instanceof NoticeDialogListener) {
                mListener = (NoticeDialogListener)fragment;
                break;
            }
        }
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

}
