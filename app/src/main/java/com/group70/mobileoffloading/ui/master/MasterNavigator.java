package com.group70.mobileoffloading.ui.master;

import android.content.Context;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.group70.mobileoffloading.data.Slave;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public interface MasterNavigator {

    Context getActivityContext();

    void showAlertDialog(String endpointId, ConnectionInfo connectionInfo);

    ConnectionsClient getConnectionsClientInstance();

    Map<String, int[]> getSlavesMap();

    Map<String, Slave> getSlavesMap2();

    LinkedList<int[]> getSlaveLinkList();

    void addToSlaveMap(String key, int[] value);

    void removeSlaveMap(String key);

    void addToSlaveMap2(String key, Slave value);

    void removeSlaveMap2(String key);

    void addToSlaveLinkList(int[] element);

    void removeSlaveLinkList();

    ArrayList<Slave> getConnections();

    void addConnection(Slave s);

    void removeConnection(Slave s);

    void setConnectionsList();
}
