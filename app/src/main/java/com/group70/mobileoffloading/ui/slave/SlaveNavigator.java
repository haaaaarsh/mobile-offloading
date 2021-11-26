package com.group70.mobileoffloading.ui.slave;

import android.content.Context;

import com.google.android.gms.nearby.connection.ConnectionsClient;

public interface SlaveNavigator {

    Context getActivityContext();

    ConnectionsClient getConnectionsClientInstance();

}
