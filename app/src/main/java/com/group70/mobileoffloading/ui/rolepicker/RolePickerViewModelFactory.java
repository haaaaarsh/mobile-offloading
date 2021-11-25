package com.group70.mobileoffloading.ui.rolepicker;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RolePickerViewModelFactory implements ViewModelProvider.Factory {


    public RolePickerViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RolePickerViewModel.class)) {
            return (T) new RolePickerViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
