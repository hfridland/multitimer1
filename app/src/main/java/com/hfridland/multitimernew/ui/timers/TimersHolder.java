package com.hfridland.multitimernew.ui.timers;

import android.view.MenuItem;
import android.widget.Toolbar;

import com.hfridland.multitimernew.data.model.TimerItem;
import com.hfridland.multitimernew.databinding.TimerItemBinding;

import androidx.recyclerview.widget.RecyclerView;

import com.hfridland.multitimernew.R;

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

    public void setupMenu() {
        mTimerItemBinding.tbItem.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mn_itemedit:
                        mTimerItemBinding.getOnTimerItemClickListener().onEditClick(mTimerItemBinding.getTimerItem().getId());
                        return true;
                    case R.id.mn_itemdelete:
                        mTimerItemBinding.getOnTimerItemClickListener().onDeleteClick(mTimerItemBinding.getTimerItem().getId());
                        return true;
                }
                return false;
            }
        });
    }
}
