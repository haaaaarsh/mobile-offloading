package com.group70.mobileoffloading.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<MainViewModel> implements MainNavigator {

    ActivityMainBinding binding;

    @NonNull
    @Override
    protected MainViewModel createViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory();
        return ViewModelProviders.of(this, factory).get(MainViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}