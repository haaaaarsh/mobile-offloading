package com.group70.mobileoffloading.ui.result;

import androidx.databinding.ObservableField;

import com.group70.mobileoffloading.ui.base.BaseViewModel;

public class ResultViewModel extends BaseViewModel<ResultNavigator> {

    private final String TAG = "ResultViewModel<>";
    private ObservableField<String> masterRes = new ObservableField<>();
    private ObservableField<String> slaveRes = new ObservableField<>();
    private ObservableField<String> diffRes = new ObservableField<>();

    public ResultViewModel() {

    }

    public ObservableField<String> getMasterRes() {
        return masterRes;
    }

    public void setMasterRes(String masterRes) {
        this.masterRes.set(masterRes);
    }

    public ObservableField<String> getSlaveRes() {
        return slaveRes;
    }

    public void setSlaveRes(String slaveRes) {
        this.slaveRes.set(slaveRes);
    }

    public ObservableField<String> getDiffRes() {
        return diffRes;
    }

    public void setDiffRes(String diffRes) {
        this.diffRes.set(diffRes);
    }
}
