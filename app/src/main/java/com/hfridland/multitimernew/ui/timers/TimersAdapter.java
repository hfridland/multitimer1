package com.hfridland.multitimernew.ui.timers;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hfridland.multitimernew.AppDelegate;
import com.hfridland.multitimernew.data.database.MultitimerDao;
import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.databinding.TimerItemBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class TimersAdapter extends RecyclerView.Adapter<TimersHolder> {

    @NonNull
    private final List<TimerItem> mTimerItems = new ArrayList<>();
    private final OnTimerItemClickListener mOnTimerItemClickListener;

    public TimersAdapter(OnTimerItemClickListener onTimerItemClickListener) {
        mOnTimerItemClickListener = onTimerItemClickListener;
        updateData();
    }

    @NonNull
    @Override
    public TimersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TimerItemBinding binding = TimerItemBinding.inflate(inflater, parent, false);
        return new TimersHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TimersHolder holder, int position) {
        TimerItem timerItem = mTimerItems.get(position);
        holder.bind(timerItem, mOnTimerItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mTimerItems.size();
    }

    public void updateData() {
        final MultitimerDao multitimerDao = AppDelegate.getMultitimerDao();
        multitimerDao.getTimerItemsRx()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(timerItems -> {
                mTimerItems.clear();
                mTimerItems.addAll(timerItems);
                notifyDataSetChanged();
            });
    }

    public interface OnTimerItemClickListener {
        void onStartStopClick(int id);
        void onEditClick(int id);
        void onDeleteClick(int id);
    }

}
