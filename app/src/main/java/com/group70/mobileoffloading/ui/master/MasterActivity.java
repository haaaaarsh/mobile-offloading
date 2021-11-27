package com.group70.mobileoffloading.ui.master;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.gson.Gson;
import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.data.Slave;
import com.group70.mobileoffloading.databinding.ActivityMasterBinding;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.ui.result.ResultActivity;
import com.group70.mobileoffloading.utils.AppUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MasterActivity extends BaseActivity<MasterViewModel> implements MasterNavigator, SlaveAdapter.SlaveClickListener {

    ActivityMasterBinding binding;
    private final String TAG = "MasterActivity<>";
    private ConnectionsClient connectionsClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static volatile Map<String, int[]> slavesMap = new LinkedHashMap<>();
    private static volatile LinkedList<int[]> slaveLinkList = new LinkedList<>();
    private static Map<String, Slave> slaveMap2 = new HashMap<>();
    private static Map<String, String> tempMap = new HashMap<>();
    private ArrayList<Slave> connectedList = new ArrayList<>();
    private double lat, lon;
    private SlaveAdapter adapter;
    private Gson gson = new Gson();

    private static int diff = 5;
    private static int dim = 50;
    private static volatile int[][] m1 = new int[dim][dim];
    private static volatile int[][] m2 = new int[dim][dim];
    private static volatile int[][] output = new int[dim][dim];
    private long start = 0, startslave = 0, startbattery = 0, startbatteryslave = 0;

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
        generateRandomMatrix();
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
                            AppUtils.writeToFile(MasterActivity.this, slave);
                            if (slave.battery > 20 && AppUtils.getDistance(lat, lon, slave.latitude, slave.longitude, slave.name) < 2000) {
                                if (!slaveMap2.containsKey(endpointId)) {
                                    slave.connected = true;
                                    slaveMap2.put(endpointId, slave);
                                    addSlaveToList();
                                }
                                binding.btnSlave.setEnabled(true);
                                debugLogSlaves();
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
                                debugLogSlaves();
                                if (slaveMap2.size() > 0) {
                                    prints();
                                }
                            }
                        } else {
                            getOutput(slave, endpointId);
                            AppUtils.writeToFile(MasterActivity.this, slave);
                            if (!slaveLinkList.isEmpty()) {
                                generateSlaveMatrix(endpointId);
                            } else if (slavesMap.isEmpty()) {
                                long end = System.currentTimeMillis();
                                BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                                long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                                Double totalEnergy = (1.0 * (Math.abs(startbattery - endEnergy))) / 1000.0;
                                 viewModel.setConnectionStatus("Finished by Slave(s) in " + (double) (end - start) / 1000 + " seconds\n"
//                                         + "Power Consumed~ " + totalEnergy + " mAh\n"
                                 );
                                 start = 0;
                                 startbattery = 0;
                                 Log.e("finalresult", Arrays.deepToString(output)); // Add to text file
                                viewModel.setResultAvailable(true);
                                for (String key : slaveMap2.keySet()) {
                                    Slave currentSlave = slaveMap2.get(key);
                                    currentSlave.comp = true;
                                    slaveSend(currentSlave);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {

                }
            };

    private void addSlaveToList() {
        for (String k : slaveMap2.keySet()) {
            if (!adapter.getList().contains(slaveMap2.get(k))) {
                addConnection(slaveMap2.get(k));
                AppUtils.writeToFile(this, slaveMap2.get(k));
            }

        }
    }

    private void setRecyclerView() {
        adapter = new SlaveAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvSlaves.setLayoutManager(layoutManager);
        binding.rvSlaves.setAdapter(adapter);
        adapter.addAll(connectedList);
    }

    private void generateRandomMatrix() {
        m1 = new int[dim][dim];
        m2 = new int[dim][dim];
        output = new int[dim][dim];
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                m1[i][j] = (int) (Math.random() * 10);
                m2[i][j] = (int) (Math.random() * 10);
            }
        }
        binding.btnMaster.setEnabled(true);
        binding.btnSlave.setEnabled(true);
        showSnackbar("Matrices have been created", Color.RED, Color.WHITE);
    }

    @Override
    public void masterCompute() {
        showSnackbar("Computing on master", Color.RED, Color.WHITE);
        long startTime = System.currentTimeMillis();
        BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        long startEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        Slave mslave = new Slave("master", "", 0.0, 0.0, 0.0, 0.0, m1, m2, null, true);
        AppUtils.multiplication(mslave);
        long endTime = System.currentTimeMillis();
        long endEnergy = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        Double diffe = (1.0 * (Math.abs(startEnergy - endEnergy))) / 1000;
        viewModel.setConnectionStatus("Computation Time :  " + (double) (endTime - startTime) / 1000 + " seconds\n"
//                + "Power Consumed~ " + diffe + " mAh"
        );
    }

    @Override
    public void slaveCompute() {
        prints();
        slaveLinkList = new LinkedList<>();
        slavesMap = new HashMap<>();
        start = System.currentTimeMillis();
        BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        startbattery = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

        for (int i = 0; i < m1.length; i += diff) {
            for (int j = 0; j < m2.length; j += diff) {
                slaveLinkList.addLast(new int[]{i, j});
            }
        }
        for (String k : slaveMap2.keySet()) {
            if (slaveMap2.get(k).connected) {
                Slave currentSlave = slaveMap2.get(k);
                int[] cur = slaveLinkList.removeFirst();
                currentSlave.m1 = Arrays.copyOfRange(m1, cur[0], cur[0] + diff);
                currentSlave.m2 = Arrays.copyOfRange(m2, cur[1], cur[1] + diff);

                slavesMap.put(k, cur);
                slaveSend(currentSlave);
            }
        }
    }


    private void generateSlaveMatrix(String d) {
        prints();
        int[] ind = slaveLinkList.removeFirst();
        slavesMap.put(d, ind);
        new Thread(() -> {
            Slave currentSlave = slaveMap2.get(d);
            if (currentSlave != null) {
                int minr = ind[0];
                int maxr = ind[0] + diff;
                int minc = ind[1];
                int maxc = ind[1] + diff;
                currentSlave.m1 = Arrays.copyOfRange(m1, minr, maxr);
                currentSlave.m2 = Arrays.copyOfRange(m2, minc, maxc);
                slaveSend(currentSlave);
            } else {
                slaveMap2.remove(d);
            }
        }).start();
    }

    public void slaveSend(Slave slave) {
        connectionsClient.sendPayload(slave.id, Payload.fromStream(new ByteArrayInputStream(gson.toJson(slave).getBytes(UTF_8))));
    }

    private void getOutput(Slave s, String d) {
        final int[] cur = slavesMap.get(d);
        final int[][] rslave = s.result;
        new Thread(() -> {
            if (cur != null) {
                for (int i = cur[0]; i < cur[0] + diff; i += 1) {
                    for (int j = cur[1]; j < cur[1] + diff; j += 1) {
                        output[i][j] = rslave[i - cur[0]][j - cur[1]];
                    }
                }
            }
        }).start();
        slavesMap.remove(d);
        binding.btnResults.setEnabled(true);
        if (!tempMap.containsKey(s.name)) {
            tempMap.put(s.name, Arrays.deepToString(rslave));
        } else {
            String lout = tempMap.get(s.name) + "\n";
            lout += Arrays.deepToString(rslave);
            tempMap.put(s.name, lout);
        }
    }

    private void prints() {
        List<String> list = new ArrayList<>();
        for (String k : slaveMap2.keySet()) {
            if (slaveMap2.get(k).connected) {
                list.add(slaveMap2.get(k).name + " : " + k);
            }
        }
        viewModel.setConnectionStatus("Slaves Computing :\n" + String.join("\n", list));
    }

    private void debugLogSlaves() {
        for (String key : slaveMap2.keySet()) {
            if (slaveMap2.get(key).id == null) {
                slaveMap2.get(key).id = key;
            }
            Log.d(TAG, slaveMap2.get(key).getAllVariables());
        }
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
        if (!adapter.getList().isEmpty())
            viewModel.setSlaveAvailable(true);
    }

    @Override
    public void removeConnection(Slave s) {
        adapter.remove(s);
        if (adapter.getList().isEmpty())
            viewModel.setSlaveAvailable(false);
    }

    @Override
    public void setConnectionsList() {

    }

    @Override
    public void openResults() {
        Slave slave = new Slave("master", "", 0.0, 0.0, 0.0, 0.0, m1, m2, null, true);
        AppUtils.multiplication(slave);
        int[][] localmresult = slave.result;
        int[][] finalOut = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                finalOut[i][j] = output[i][j] - localmresult[i][j];
            }
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("Result", Arrays.deepToString(finalOut));
        intent.putExtra("Master", Arrays.deepToString(localmresult));
        intent.putExtra("Slaves", Arrays.deepToString(output));
        startActivity(intent);
    }

    @Override
    public void onSlaveClick(Slave slave) {

    }
}