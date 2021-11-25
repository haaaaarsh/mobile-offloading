package com.group70.mobileoffloading.ui.master;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivityMasterBinding;

public class MasterActivity extends BaseActivity<MasterViewModel> implements MasterNavigator {

    ActivityMasterBinding binding;

    @NonNull
    @Override
    protected MasterViewModel createViewModel() {
        MasterViewModelFactory factory = new MasterViewModelFactory();
        return ViewModelProviders.of(this, factory).get(MasterViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_master);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}