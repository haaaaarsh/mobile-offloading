package com.group70.mobileoffloading.ui.slave;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivitySlaveBinding;

public class SlaveActivity extends BaseActivity<SlaveViewModel> implements SlaveNavigator {

    ActivitySlaveBinding binding;

    @NonNull
    @Override
    protected SlaveViewModel createViewModel() {
        SlaveViewModelFactory factory = new SlaveViewModelFactory();
        return ViewModelProviders.of(this, factory).get(SlaveViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_slave);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}