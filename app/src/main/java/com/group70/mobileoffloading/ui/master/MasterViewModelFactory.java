package com.group70.mobileoffloading.ui.master;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MasterViewModelFactory implements ViewModelProvider.Factory {


    public MasterViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MasterViewModel.class)) {
            return (T) new MasterViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
