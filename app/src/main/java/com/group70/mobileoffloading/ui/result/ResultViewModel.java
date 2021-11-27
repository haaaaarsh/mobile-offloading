package com.group70.mobileoffloading.ui.result;

import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.group70.mobileoffloading.ui.base.BaseViewModel;

public class ResultViewModel extends BaseViewModel<ResultNavigator> {

    private final String TAG = "ResultViewModel<>";

    public ResultViewModel() {

    }
}
