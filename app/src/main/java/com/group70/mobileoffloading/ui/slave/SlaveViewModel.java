package com.group70.mobileoffloading.ui.slave;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.group70.mobileoffloading.ui.base.BaseViewModel;

public class SlaveViewModel extends BaseViewModel<SlaveNavigator> {

    private final String TAG = "SlaveViewModel<>";
    private ObservableField<String> connectionStatus = new ObservableField<>();
    private ObservableField<String> masterName = new ObservableField<>();
    private ObservableField<Boolean> endPointDiscover = new ObservableField<>(false);

    public SlaveViewModel() {

    }

    public void startScanning() {
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
                    String masterName = info.getEndpointName();
                    String masterId = endpointId;
                    setConnectionStatus("Found Master " + masterName + " : " + masterId);
                    setMasterName(masterName + " (ID: " + masterId + ")");
                    endPointDiscover.set(true);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                }
            };

    public void connect() {

    }

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
}
