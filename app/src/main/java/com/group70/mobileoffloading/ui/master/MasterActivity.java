package com.group70.mobileoffloading.ui.master;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.AlertDialog;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.gson.Gson;
import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.data.Slave;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivityMasterBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MasterActivity extends BaseActivity<MasterViewModel> implements MasterNavigator {

    ActivityMasterBinding binding;
    private final String TAG = "MasterActivity<>";
    private ConnectionsClient connectionsClient;
    private static volatile Map<String, int[]> slavesMap = new LinkedHashMap<>();
    private static volatile LinkedList<int[]> slaveLinkList = new LinkedList<>();
    private static Map<String, Slave> slaveMap2 = new HashMap<>();
    private ArrayList<String> connectedList = new ArrayList<>();

    @NonNull
    @Override
    protected MasterViewModel createViewModel() {
        MasterViewModelFactory factory = new MasterViewModelFactory();
        return ViewModelProviders.of(this, factory).get(MasterViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
        setToolBar();
        viewModel.startAdvertising(null, null);
    }

    private void setToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getDeviceName() + " " + getResources().getString(R.string.title_master));
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_master);
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

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(final String endpointId, Payload payload) {
                    /**Log.e(TAG, "received");
                     InputStream is = payload.asStream().asInputStream();
                     Slave slave = new Gson().fromJson(new InputStreamReader(is, UTF_8), Slave.class);
                     //                    if (sertype.equals("Master")) {
                     try {
                     if (slave.result == null) {
                     /**writeToFile(slave);
                     if (slave.bat > 20 && getDistance(lat, lon, slave.lat, slave.lon, slave.name) < 2000) {
                     if (!sdict.containsKey(endpointId)) {
                     slave.connected = true;
                     sdict.put(endpointId, slave);
                     setupSlaveList();
                     }
                     scompute.setEnabled(true);
                     /**listSlaves();
                     slave_log.setEnabled(true);
                     } else {
                     if (slavesMap.containsKey(endpointId)) {
                     int[] b = slavesMap.get(endpointId);
                     slavesMap.remove(endpointId);
                     slaveLinkList.addLast(b);
                     }
                     setconn.setText("Disconnected: " + endpointId);

                     if (sdict.containsKey(endpointId)) {
                     inputList.remove(new String(sdict.get(endpointId).name));
                     makeList();
                     sdict.get(endpointId).connected = false;
                     }
                     listSlaves();
                     if (sdict.size() > 0) {
                     prints();
                     }
                     }
                     } else {
                     computeOutput(slave, endpointId);
                     writeToFile(slave);
                     if (!slaveLinkList.isEmpty()) {
                     generateSlaveMatrix(endpointId);
                     } else if (slavesMap.isEmpty()) {
                     long end = System.currentTimeMillis();
                     BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                     long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                     Double totalEnergy = (1.0 * (Math.abs(startbattery - endEnergy))) / 1000.0;
                     setconn.setText("Finished by Slave(s) in " + (double) (end - start) / 1000 + " seconds\n" + "Power Consumed~ " + totalEnergy + " mAh\n");
                     start = 0;
                     startbattery = 0;
                     Log.d("finalresult", Arrays.deepToString(output)); // Add to text file

                     for (String key : sdict.keySet()) {
                     Slave currentSlave = sdict.get(key);
                     currentSlave.comp = true;
                     slaveSend(currentSlave);
                     }
                     }
                     }
                     } catch (Exception e) {
                     Log.e(TAG, e.toString());
                     }*/
                    /*} else {
                        try {
                            if (slave.comp) {
                                computing = false;
                                long endTime = System.currentTimeMillis();
                                BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                                long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

                                Double totalEnergy = (1.0 * (Math.abs(startbatteryslave - endEnergy))) / 1000;
                                setconn.setText("Finished in " + (double) (endTime - startslave) / 1000 + " seconds\n" + "Power Consumed: " + totalEnergy + " mAh");
//                                startbatteryslave = 0;
                            } else {
                                if (!computing) {
                                    computing = true;
                                    startslave = System.currentTimeMillis();
                                    BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                                    startbatteryslave = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                                }
                                setconn.setText("Performing matrix multiplication for: " + mName + " : " + mid);
                                Log.d("hello", "check");
                                multiplication(slave);
                                connectionsClient.sendPayload(mid, Payload.fromStream(new ByteArrayInputStream(gson.toJson(slave).getBytes(UTF_8))));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }*/
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {

                }
            };

    @Override
    public ConnectionsClient getConnectionsClientInstance() {
        return connectionsClient;
    }

    @Override
    public void showConnectionStatus(String message) {
        binding.txt.setText(message);
    }

    @Override
    public Map<String, int[]> getSlavesMap() {
        return slavesMap;
    }

    @Override
    public Map<String, Slave> getSlavesMap2() {
        return slaveMap2;
    }

    @Override
    public LinkedList<int[]> getSlaveLinkList() {
        return slaveLinkList;
    }

    @Override
    public void addToSlaveMap(String key, int[] value) {
        slavesMap.put(key, value);
    }

    @Override
    public void removeSlaveMap(String key) {
        slavesMap.remove(key);
    }

    @Override
    public void addToSlaveMap2(String key, Slave value) {
        slaveMap2.put(key, value);
    }

    @Override
    public void removeSlaveMap2(String key) {
        slavesMap.remove(key);
    }

    @Override
    public void addToSlaveLinkList(int[] element) {
        slaveLinkList.addLast(element);
    }

    @Override
    public void removeSlaveLinkList() {
        slaveLinkList.removeFirst();
    }

    @Override
    public ArrayList<String> getConnections() {
        return connectedList;
    }

    @Override
    public void addConnection(String s) {
        connectedList.add(s);
    }

    @Override
    public void removeConnection(String s) {
        connectedList.remove(s);
    }

    @Override
    public void setConnectionsList() {

    }
}