package com.group70.mobileoffloading.ui.result;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ResultViewModelFactory implements ViewModelProvider.Factory {


    public ResultViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ResultViewModel.class)) {
            return (T) new ResultViewModel();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
