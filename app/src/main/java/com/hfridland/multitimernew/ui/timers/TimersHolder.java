package com.hfridland.multitimernew.ui.timers;

import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.databinding.TimerItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class TimersHolder extends RecyclerView.ViewHolder {
    private TimerItemBinding mTimerItemBinding;

    public TimersHolder(TimerItemBinding binding) {
        super(binding.getRoot());

        mTimerItemBinding = binding;
    }

    public void bind(TimerItem item, TimersAdapter.OnTimerItemClickListener onTimerItemClickListener) {
        mTimerItemBinding.setTimerItem(new TimerItemViewModel(item));
        mTimerItemBinding.setOnTimerItemClickListener(onTimerItemClickListener);
        mTimerItemBinding.executePendingBindings();
    }
}
