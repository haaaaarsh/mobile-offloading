package com.group70.mobileoffloading.ui.rolepicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.ui.base.BaseActivity;
import com.group70.mobileoffloading.databinding.ActivityRolePickerBinding;
import com.group70.mobileoffloading.ui.master.MasterActivity;
import com.group70.mobileoffloading.ui.slave.SlaveActivity;

public class RolePickerActivity extends BaseActivity<RolePickerViewModel> implements RolePickerNavigator {

    ActivityRolePickerBinding binding;
    private final int PERMISSION_REQUEST_CODE = 0x01;

    @NonNull
    @Override
    protected RolePickerViewModel createViewModel() {
        RolePickerViewModelFactory factory = new RolePickerViewModelFactory();
        return ViewModelProviders.of(this, factory).get(RolePickerViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_role_picker);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void openNextScreen(int screenNum) {
        switch (screenNum) {
            case 1:
                if (checkPermission(screenNum))
                    openActivity(MasterActivity.class);
                break;
            case 2:
                if (checkPermission(screenNum))
                    openActivity(SlaveActivity.class);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openActivity(MasterActivity.class);
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openActivity(SlaveActivity.class);
                break;
        }
    }

    private boolean checkPermission(int screenNum) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{
                            android.Manifest.permission.BLUETOOTH,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.CHANGE_WIFI_STATE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    screenNum);
            return false;
        }

        return true;
    }
}