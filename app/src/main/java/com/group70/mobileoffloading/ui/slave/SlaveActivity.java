package com.group70.mobileoffloading.ui.slave;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.data.Slave;
import com.group70.mobileoffloading.databinding.ActivitySlaveBinding;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.utils.AppUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class SlaveActivity extends BaseActivity<SlaveViewModel> implements SlaveNavigator {

    ActivitySlaveBinding binding;
    private final String TAG = "SlaveActivity<>";
    private ConnectionsClient connectionsClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Gson gson = new Gson();
    private Timer timer;
    private double latitude, longitude;
    private boolean isComputing;
    private long startSlave = 0, startBatterySlaveLevel = 0;

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
        getLocation();
        isComputing = false;
    }

    private void setConnectionClients() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        connectionsClient = Nearby.getConnectionsClient(this);
    }

    private void setObservables() {
        viewModel.getEndPointDiscover().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

            }
        });

        viewModel.getIsMasterConnected().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                boolean isConnect = ((ObservableBoolean) sender).get();
                binding.connectToggle.setText(isConnect ? getResources().getString(R.string.disconnect)
                        : getResources().getString(R.string.connect));
            }
        });
    }

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(final String endpointId, Payload payload) {
                    Log.e(TAG, "received");
                    InputStream is = payload.asStream().asInputStream();
                    Slave slave = new Gson().fromJson(new InputStreamReader(is, UTF_8), Slave.class);
                    try {
                        if (slave.comp) {
                            isComputing = false;
                            long endTime = System.currentTimeMillis();
                            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                            long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

                            Double totalEnergy = (1.0 * (Math.abs(startBatterySlaveLevel - endEnergy))) / 1000;
                            viewModel.setConnectionStatus("Matrix solved in " + (double) (endTime - startSlave) / 1000 + " seconds\n"
//                                    + "Power Consumed: " + totalEnergy + " mAh"
                            );
                        } else {
                            if (!isComputing) {
                                isComputing = true;
                                startSlave = System.currentTimeMillis();
                                BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                                startBatterySlaveLevel = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                            }
                            viewModel.setConnectionStatus("Performing matrix multiplication for: " + viewModel.getMasterDeviceName() + " : " + viewModel.getMasterDeviceId());
                            Log.d("hello", "check");
                            AppUtils.multiplication(slave);
                            connectionsClient.sendPayload(viewModel.getMasterDeviceId(), Payload.fromStream(new ByteArrayInputStream(gson.toJson(slave).getBytes(UTF_8))));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {

                }
            };

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
    public void showAlertDialog(String endpointId, ConnectionInfo connectionInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Connection");
        builder.setMessage("Server requesting connection: " + connectionInfo.getEndpointName());
        builder.setPositiveButton("Accept", (dialog, id) -> connectionsClient.acceptConnection(endpointId, payloadCallback));
        builder.setNegativeButton("Deny", (dialog, id) -> connectionsClient.rejectConnection("Rejected"));
        AlertDialog dialog = builder.create();

        dialog.show();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public ConnectionsClient getConnectionsClientInstance() {
        return connectionsClient;
    }

    @Override
    public void getLocation() {
        if (this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.e(TAG, "Last location : " + location.toString());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } else {
                        Log.d(TAG, "could not get location");
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionSuccess(String endpointId, ConnectionResolution result) {
        String master = viewModel.getMasterDeviceName();
        String mId = viewModel.getMasterDeviceId();
        if (result.getStatus().isSuccess()) {
            connectionsClient.stopDiscovery();
            viewModel.setConnectionStatus("Established connection to " + master + " (ID: " + mId + ")");
            viewModel.setIsMasterConnected(true);
            getLocation();
            Slave slave = new Slave(getDeviceName(), null, getBatteryLevel(), getBatteryLevel(), latitude, longitude, null, null, null, true, 0.0f);
            connectionsClient.sendPayload(mId, Payload.fromStream(new ByteArrayInputStream(gson.toJson(slave).getBytes(UTF_8))));
            Log.e(TAG, "sent");
            TimerTask timerTask = new SendLiveStatus();
            timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, 10000);
        } else {
            viewModel.setIsMasterConnected(false);
            Log.e(TAG, "onConnectionResult: connection failed");
            viewModel.setConnectionStatus("Failed to connect to " + master + " (ID: " + mId + ")");
        }
    }

    public class SendLiveStatus extends TimerTask {
        @Override
        public void run() {
            getLocation();
            Slave slave = new Slave(getDeviceName(), null, getBatteryLevel(), getBatteryLevel(), latitude, longitude, null, null, null, true, 0.0f);
            connectionsClient.sendPayload(viewModel.getMasterDeviceId(), Payload.fromStream(new ByteArrayInputStream(gson.toJson(slave).getBytes(UTF_8))));
        }
    }

    private double getBatteryLevel() {
        IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent battery = this.registerReceiver(null, intent);
        int bat = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int s = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryLevel = bat * 100 / s;
        return batteryLevel;
    }
}