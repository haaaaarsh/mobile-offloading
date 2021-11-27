package com.group70.mobileoffloading.ui.result;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.databinding.ActivityResultBinding;
import com.group70.mobileoffloading.ui.base.BaseActivity;

public class ResultActivity extends BaseActivity<ResultViewModel> implements ResultNavigator {

    ActivityResultBinding binding;
    private final String TAG = "ResultActivity<>";

    @NonNull
    @Override
    protected ResultViewModel createViewModel() {
        ResultViewModelFactory factory = new ResultViewModelFactory();
        return ViewModelProviders.of(this, factory).get(ResultViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBindings();
        viewModel.setNavigator(this);
        setToolBar();
        getResult();
    }

    private void getResult() {
        String zeroCheck = getIntent().getStringExtra("Result");
        String masterResult = getIntent().getStringExtra("Master");
        String slaveResult = getIntent().getStringExtra("Slaves");
        viewModel.setMasterRes(masterResult);
        viewModel.setSlaveRes(slaveResult);
        Log.e(TAG, zeroCheck);
    }

    private void setToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_result));
    }

    private void setDataBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}