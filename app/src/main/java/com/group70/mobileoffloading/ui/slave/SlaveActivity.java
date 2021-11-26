package com.group70.mobileoffloading.ui.slave;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivitySlaveBinding;

public class SlaveActivity extends BaseActivity<SlaveViewModel> implements SlaveNavigator {

    ActivitySlaveBinding binding;
    private ConnectionsClient connectionsClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @NonNull
    @Override
    protected SlaveViewModel createViewModel() {
        SlaveViewModelFactory factory = new SlaveViewModelFactory();
        return ViewModelProviders.of(this, factory).get(SlaveViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
        setToolBar();
        setConnectionClients();
        setObservables();
    }

    private void setConnectionClients() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        connectionsClient = Nearby.getConnectionsClient(this);
    }

    private void setObservables() {
        viewModel.getEndPointDiscover().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                boolean b = ((ObservableField<Boolean>)sender).get();
                /**find.setEnabled(false);*/
                /**connect.setVisibility(View.VISIBLE);
                 connect.setEnabled(true);
                 connect.setText("Connect");
                 connect.setOnClickListener(v -> {
                 setConnectionStatus("Connecting to Master: " + masterName + " : " + masterId);
                 connectionsClient.requestConnection(sername, endpointId, connectionLifecycleCallback);

                 });*/
            }
        });
    }

    private void setToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getDeviceName() + " " + getResources().getString(R.string.title_slave));
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_slave);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public ConnectionsClient getConnectionsClientInstance() {
        return connectionsClient;
    }
}