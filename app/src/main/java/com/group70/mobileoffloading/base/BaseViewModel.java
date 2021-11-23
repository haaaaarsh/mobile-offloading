package com.group70.mobileoffloading.base;

import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;

public class BaseViewModel<N> extends ViewModel {


    private WeakReference<N> mNavigator;

    public N getNavigator() {
        return mNavigator.get();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }
}
