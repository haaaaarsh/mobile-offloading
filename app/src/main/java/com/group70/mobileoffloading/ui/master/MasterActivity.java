package com.group70.mobileoffloading.ui.master;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.data.Slave;
import com.group70.mobileoffloading.databinding.ActivityMasterBinding;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.utils.AppUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MasterActivity extends BaseActivity<MasterViewModel> implements MasterNavigator, SlaveAdapter.SlaveClickListener {

    ActivityMasterBinding binding;
    private final String TAG = "MasterActivity<>";
    private ConnectionsClient connectionsClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static volatile Map<String, int[]> slavesMap = new LinkedHashMap<>();
    private static volatile LinkedList<int[]> slaveLinkList = new LinkedList<>();
    private static Map<String, Slave> slaveMap2 = new HashMap<>();
    private ArrayList<Slave> connectedList = new ArrayList<>();
    private double lat, lon;
    private SlaveAdapter adapter;

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
        setConnectionClients();
        setRecyclerView();
    }

    private void setConnectionClients() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        connectionsClient = Nearby.getConnectionsClient(this);
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

    private void getLocation() {
        if (this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MasterActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.e(TAG, "Last location : " + location.toString());
                        lat = location.getLatitude();
                        lon = location.getLongitude();
//                        x.setVisibility(View.VISIBLE);
//                        y.setVisibility(View.VISIBLE);
                        /**x.setText(String.valueOf(lat));
                         y.setText(String.valueOf(lon));*/
                    } else {
                        Log.d(TAG, "could not get location");
                    }
                }
            });
        }
    }

    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(final String endpointId, Payload payload) {
                    Log.e(TAG, "received");
                    InputStream is = payload.asStream().asInputStream();
                    Slave slave = new Gson().fromJson(new InputStreamReader(is, UTF_8), Slave.class);
                    //                    if (sertype.equals("Master")) {
                    try {
                        if (slave.result == null) {
                            /**writeToFile(slave);*/
                            if (slave.bat > 20 && AppUtils.getDistance(lat, lon, slave.lat, slave.lon, slave.name) < 2000) {
                                if (!slaveMap2.containsKey(endpointId)) {
                                    slave.connected = true;
                                    slaveMap2.put(endpointId, slave);
                                    addSlaveToList();
                                }
                                /**scompute.setEnabled(true);*/
                                /**listSlaves();*/
                                /**slave_log.setEnabled(true);*/
                            } else {
                                if (slavesMap.containsKey(endpointId)) {
                                    int[] b = slavesMap.get(endpointId);
                                    slavesMap.remove(endpointId);
                                    slaveLinkList.addLast(b);
                                }
                                viewModel.setConnectionStatus("Disconnected: " + endpointId);
                                if (slaveMap2.containsKey(endpointId)) {
                                    removeConnection(slaveMap2.get(endpointId));
                                    setRecyclerView();
                                    slaveMap2.get(endpointId).connected = false;
                                }
                                /**listSlaves();*/
                                if (slaveMap2.size() > 0) {
                                    /**prints();*/
                                }
                            }
                        } else {
                            /**computeOutput(slave, endpointId);
                             writeToFile(slave);*/
                            if (!slaveLinkList.isEmpty()) {
                                /**generateSlaveMatrix(endpointId);*/
                            } else if (slavesMap.isEmpty()) {
                                long end = System.currentTimeMillis();
                                BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                                long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                                /**Double totalEnergy = (1.0 * (Math.abs(startbattery - endEnergy))) / 1000.0;
                                 viewModel.setConnectionStatus("Finished by Slave(s) in " + (double) (end - start) / 1000 + " seconds\n" + "Power Consumed~ " + totalEnergy + " mAh\n")
                                 start = 0;
                                 startbattery = 0;
                                 Log.e("finalresult", Arrays.deepToString(output)); // Add to text file*/

                                for (String key : slaveMap2.keySet()) {
                                    Slave currentSlave = slaveMap2.get(key);
                                    currentSlave.comp = true;
                                    /**slaveSend(currentSlave);*/
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
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

    private void addSlaveToList() {
        for (String k : slaveMap2.keySet()) {
            if (!connectedList.contains(slaveMap2.get(k))) {
                addConnection(slaveMap2.get(k));
                AppUtils.writeToFile(this, slaveMap2.get(k));
            }

        }
    }

    private void setRecyclerView() {
        adapter = new SlaveAdapter(this, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvSlaves.getContext(),
                new LinearLayoutManager(this).getOrientation());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSlaves.setLayoutManager(layoutManager);
        binding.rvSlaves.addItemDecoration(dividerItemDecoration);
        binding.rvSlaves.setAdapter(adapter);
        adapter.addAll(connectedList);
    }

    @Override
    public ConnectionsClient getConnectionsClientInstance() {
        return connectionsClient;
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
    public ArrayList<Slave> getConnections() {
        return connectedList;
    }

    @Override
    public void addConnection(Slave s) {
        adapter.add(s);
    }

    @Override
    public void removeConnection(Slave s) {
        adapter.remove(s);
    }

    @Override
    public void setConnectionsList() {

    }

    @Override
    public void onSlaveClick(Slave slave) {

    }
}