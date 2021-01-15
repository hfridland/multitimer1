package com.hfridland.multitimernew.ui.timers;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.service.TickService;
import com.hfridland.multitimernew.ui.about.AboutActivity;
import com.hfridland.multitimernew.ui.dialogs.TimeEditorDialogFragment;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.hfridland.multitimernew.R;

public class TimersFragment extends Fragment implements TimeEditorDialogFragment.NoticeDialogListener {
    private FloatingActionButton mFabNewItem;
    private RecyclerView mRecyclerView;
    private TimersAdapter mTimersAdapter;
    private final MultitimerDao mMultitimerDao = AppDelegate.getMultitimerDao();
    private TimersAdapter.OnTimerItemClickListener mOnTimerItemClickListener = new TimersAdapter.OnTimerItemClickListener() {

        @Override
        public void onStartStopClick(int id) {
            Single.fromCallable(() -> mMultitimerDao.changeActive(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timerItem -> {
                    mTimersAdapter.updateData();
                    if (timerItem.isActive()) {
                        Intent intent = new Intent(getContext(), TickService.class);
                        getActivity().startService(intent);
                    } else {
                        mMultitimerDao.getActiveTimerItemsRx()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(timerItems -> {
                                if (timerItems.isEmpty()) {
                                    Intent intentClose = new Intent(getContext(), TickService.class);
                                    getActivity().stopService(intentClose);
                                }
                            });
                    }
                });
        }

        @Override
        public void onEditClick(int id) {
            Bundle data = new Bundle();
            data.putInt("id", id);
            DialogFragment newFragment = new TimeEditorDialogFragment();
            newFragment.setArguments(data);
            newFragment.show(getActivity().getSupportFragmentManager(), "timerEditor");
        }

        @Override
        public void onDeleteClick(int id) {
            mMultitimerDao.getSingleTimerItemById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(timerItem -> {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Are you really want to delete timer item " + timerItem.getName() + "?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> Single.fromCallable(() -> mMultitimerDao.deleteTimerItem(id))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(n -> {
                                            mTimersAdapter.updateData();
                                        }))
                                .setNegativeButton("No", null).create().show();
                    });
        }
    };

    private Disposable mTimerDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMultitimerDao.getActiveTimerItemsRx()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(timerItems -> {
                if (!timerItems.isEmpty()) {
                    Intent intent = new Intent(getContext(), TickService.class);
                    getActivity().startService(intent);
                }
            });

        mTimerDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seconds -> {
                    mMultitimerDao.getActiveTimerItemsRx()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(timerItems -> {
                            if (!timerItems.isEmpty()) {
                                mTimersAdapter.updateData();
                            }
                        });
                });


    }

    public static TimersFragment newInstance() {
        return new TimersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fr_timers, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mn_timers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_about:
                Intent launchIntent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(launchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTimersAdapter = new TimersAdapter(mOnTimerItemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mTimersAdapter);
        mFabNewItem = getActivity().findViewById(R.id.fabNewItem);
        mFabNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putInt("id", -1);
                DialogFragment newFragment = new TimeEditorDialogFragment();
                newFragment.setArguments(data);
                newFragment.show(getActivity().getSupportFragmentManager(), "timerEditor");
            }
        });

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            if (intent.getAction().equals("UpdateAdapterData")) {
                mTimersAdapter.updateData();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mTimersAdapter.updateData();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerDisposable.dispose();
    }
}
