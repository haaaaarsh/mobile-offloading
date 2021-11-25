package com.group70.mobileoffloading.ui.splash;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivitySplashBinding;
import com.group70.mobileoffloading.ui.rolepicker.RolePickerActivity;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends BaseActivity<SplashViewModel> implements SplashNavigator{

    ActivitySplashBinding binding;

    @NonNull
    @Override
    protected SplashViewModel createViewModel() {
        SplashViewModelFactory factory = new SplashViewModelFactory();
        return ViewModelProviders.of(this, factory).get(SplashViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        viewModel.setNavigator(this);
        decideNextActivity();
        getSupportActionBar().hide();
    }

    private void decideNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity(RolePickerActivity.class);
                finish();
            }
        }, TimeUnit.SECONDS.toMillis(1));
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }
}