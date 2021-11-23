package com.group70.mobileoffloading.ui.splash;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SplashViewModelFactory implements ViewModelProvider.Factory {


    public SplashViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
