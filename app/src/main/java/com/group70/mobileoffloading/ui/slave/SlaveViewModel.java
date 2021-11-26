package com.group70.mobileoffloading.ui.slave;

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

public class SlaveViewModel extends BaseViewModel<SlaveNavigator> {

    private final String TAG = "SlaveViewModel<>";
    private SlaveNavigator navigator;
    private ObservableField<String> connectionStatus = new ObservableField<>();
    private ObservableField<String> masterName = new ObservableField<>();
    private ObservableField<Boolean> endPointDiscover = new ObservableField<>(false);
    private ObservableBoolean mIsLoading = new ObservableBoolean();
    private ObservableBoolean isMasterConnected = new ObservableBoolean(false);
    private String masterDeviceName = null, masterDeviceId = null;

    public SlaveViewModel() {

    }

    public void startScanning() {
        navigator = getNavigator();
        setIsLoading(true);
        try {
            getNavigator().getConnectionsClientInstance().startDiscovery(getNavigator().getActivityContext().getPackageName(),
                    endpointDiscoveryCallback,
                    new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    masterDeviceName = info.getEndpointName();
                    masterDeviceId = endpointId;
                    setConnectionStatus("Found Master " + masterDeviceName + " : " + masterDeviceId);
                    setMasterName(masterDeviceName + " (ID: " + masterDeviceId + ")");
                    endPointDiscover.set(true);
                    setIsLoading(false);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    setIsLoading(false);
                }
            };

    public void connectDisconnectToggle() {
        if (!isMasterConnected.get())
            getNavigator().getConnectionsClientInstance().requestConnection(getNavigator().getDeviceName(),
                    masterDeviceId,
                    connectionLifecycleCallback);
        else {
            getNavigator().getConnectionsClientInstance().disconnectFromEndpoint(getMasterDeviceId());
            setIsMasterConnected(false);
//            ((SlaveActivity) getNavigator().getActivityContext()).recreate();
        }
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                String slaveName = null;
                String slaveId = null;

                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.e(TAG, "onConnectionInitiated: establishing connection");
                    navigator.showAlertDialog(endpointId, connectionInfo);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    navigator.onConnectionSuccess(endpointId, result);
                }

                @Override
                public void onDisconnected(String endpointId) {

                }
            };

    public ObservableField<String> getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus.set(connectionStatus);
    }

    public ObservableField<String> getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName.set(masterName);
    }

    public ObservableField<Boolean> getEndPointDiscover() {
        return endPointDiscover;
    }

    public void setEndPointDiscover(Boolean endPointDiscover) {
        this.endPointDiscover.set(endPointDiscover);
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public ObservableBoolean getIsMasterConnected() {
        return isMasterConnected;
    }

    public void setIsMasterConnected(Boolean isMasterConnected) {
        this.isMasterConnected.set(isMasterConnected);
    }

    public String getMasterDeviceName() {
        return masterDeviceName;
    }

    public String getMasterDeviceId() {
        return masterDeviceId;
    }
}
