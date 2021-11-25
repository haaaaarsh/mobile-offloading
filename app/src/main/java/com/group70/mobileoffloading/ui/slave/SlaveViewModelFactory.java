package com.group70.mobileoffloading.ui.slave;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SlaveViewModelFactory implements ViewModelProvider.Factory {


    public SlaveViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SlaveViewModel.class)) {
            return (T) new SlaveViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
