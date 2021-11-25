package com.group70.mobileoffloading.ui.rolepicker;

import com.group70.mobileoffloading.base.BaseViewModel;

public class RolePickerViewModel extends BaseViewModel<RolePickerNavigator> {

    public RolePickerViewModel() {

    }

    public void openNextScreen(int screenNum) {
        getNavigator().openNextScreen(screenNum);
    }
}
