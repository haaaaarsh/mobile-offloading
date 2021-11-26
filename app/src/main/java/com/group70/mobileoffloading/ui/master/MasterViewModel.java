package com.group70.mobileoffloading.ui.master;

import android.location.Location;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Strategy;
import com.group70.mobileoffloading.ui.base.BaseViewModel;

public class MasterViewModel extends BaseViewModel<MasterNavigator> {

    private final String TAG = "MasterViewModel<>";
    private MasterNavigator navigator;
    private ObservableField<String> connectionStatus = new ObservableField<>();
    private ObservableBoolean mIsLoading = new ObservableBoolean();

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
//                        if (sertype.equals("Master")) {

                        navigator.getConnectionsClientInstance().stopAdvertising();
                        setConnectionStatus("Latest Connected to : " + slaveName + " : " + slaveId);

                        /*} else {
                            connectionsClient.stopDiscovery();
                            setconn.setText("Connected to master: " + mName + " : " + mid);
//                            disconnect.setVisibility(View.VISIBLE);
                            connect.setEnabled(false);
                            disconnect.setEnabled(true);
                            disconnect.setOnClickListener(v -> {
                                connectionsClient.disconnectFromEndpoint(mid);
                                recreate();
                            });

                            getLocation();
                            Slave senslave = new Slave(sername, slaveId, getBatteryLevel(), getBatteryLevel(), lat, lon, null, null, null, true);
                            connectionsClient.sendPayload(mid, Payload.fromStream(new ByteArrayInputStream(gson.toJson(senslave).getBytes(UTF_8))));
                            Log.e(TAG, "sent");
                            TimerTask timerTask = new KeepSending();
                            timer = new Timer(true);
                            timer.scheduleAtFixedRate(timerTask, 0, 10000);
                        }*/
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
                        navigator.removeConnection(new String(navigator.getSlavesMap2().get(endpointId).name));
                        /**navigator.setConnectionsList();*/
                        navigator.getSlavesMap2().get(endpointId).connected = false;
                    }
                    /** listSlaves();*/
                    if (navigator.getSlavesMap2().size() > 0) {
                        /** prints();*/
                    }
                }
            };


    public double getDistance(double lat1, double lon1, double lat2, double lon2, String s_name) {
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) {
            return 0;
        }
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

//        Toast.makeText(MainActivity.this, "Distance From Slave("+s_name+"): "+String.valueOf(startPoint.distanceTo(endPoint)), Toast.LENGTH_LONG).show();

        return startPoint.distanceTo(endPoint);
    }

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
}
