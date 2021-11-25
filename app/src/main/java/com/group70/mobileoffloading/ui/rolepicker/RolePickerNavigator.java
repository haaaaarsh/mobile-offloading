package com.group70.mobileoffloading.ui.rolepicker;

import android.content.Context;

public interface RolePickerNavigator {

    Context getActivityContext();

    void openNextScreen(int screenNum);

}
