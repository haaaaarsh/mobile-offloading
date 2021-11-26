package com.group70.mobileoffloading.ui.slave;

import android.content.Context;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;

public interface SlaveNavigator {

    Context getActivityContext();

    void showAlertDialog(String endpointId, ConnectionInfo connectionInfo);

    ConnectionsClient getConnectionsClientInstance();

    void getLocation();

    void onConnectionSuccess(String endpointId, ConnectionResolution result);

    String getDeviceName();
}
