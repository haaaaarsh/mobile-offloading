package com.group70.mobileoffloading.ui.master;

import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Strategy;
import com.group70.mobileoffloading.ui.base.BaseViewModel;

public class MasterViewModel extends BaseViewModel<MasterNavigator> {

    private final String TAG = "MasterViewModel<>";
    private MasterNavigator navigator;
    private ObservableField<String> connectionStatus = new ObservableField<>("Live Monitor");
    private ObservableBoolean mIsLoading = new ObservableBoolean();
    private ObservableBoolean slaveAvailable = new ObservableBoolean();
    private ObservableBoolean resultAvailable = new ObservableBoolean();

    public MasterViewModel() {

    }

    public void startAdvertising() {
        navigator = getNavigator();
        try {
            getNavigator().getConnectionsClientInstance().startAdvertising(android.os.Build.MODEL,
                    getNavigator().getActivityContext().getPackageName(),
                    connectionLifecycleCallback,
                    new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void openResults() {
        getNavigator().openResults();
    }

    public void masterCompute() {
        getNavigator().masterCompute();
    }

    public void slaveCompute() {
        getNavigator().slaveCompute();
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                String slaveName = null;
                String slaveId = null;

                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.e(TAG, "onConnectionInitiated: establishing connection");
                    slaveName = connectionInfo.getEndpointName();
                    slaveId = endpointId;
                    setConnectionStatus("Connecting to: " + slaveName + " : " + slaveId);
                    navigator.showAlertDialog(endpointId, connectionInfo);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        navigator.getConnectionsClientInstance().stopAdvertising();
                        setConnectionStatus("Latest Connected to : " + slaveName + " : " + slaveId);
                    } else {
                        Log.e(TAG, "onConnectionResult: connection failed");
//                        if (sertype.equals("Master")) {
                        setConnectionStatus("Connection failed: " + slaveName + " : " + slaveId);
//                        } else {
//                            setconn.setText("Connection Failed: " + mName + " : " + mid);
//                        }
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    if (navigator.getSlavesMap().containsKey(endpointId)) {
                        int[] b = navigator.getSlavesMap().get(endpointId);
                        navigator.removeSlaveMap(endpointId);
                        navigator.addToSlaveLinkList(b);
                    }
                    setConnectionStatus("Disconnected: " + endpointId);
                    if (navigator.getSlavesMap2().containsKey(endpointId)) {
                        navigator.removeConnection(navigator.getSlavesMap2().get(endpointId));
                        /**navigator.setConnectionsList();*/
                        navigator.getSlavesMap2().get(endpointId).connected = false;
                    }
                    /** listSlaves();*/
                    if (navigator.getSlavesMap2().size() > 0) {
                        /** prints();*/
                    }
                }
            };


    public ObservableField<String> getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus.set(connectionStatus);
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public ObservableBoolean getSlaveAvailable() {
        return slaveAvailable;
    }

    public void setSlaveAvailable(Boolean slaveAvailable) {
        this.slaveAvailable.set(slaveAvailable);
    }

    public ObservableBoolean getResultAvailable() {
        return resultAvailable;
    }

    public void setResultAvailable(Boolean resultAvailable) {
        this.resultAvailable.set(resultAvailable);
    }
}
