package com.sunmi.ipc.ipcset;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import sunmi.common.utils.StatusBarUtils;

/**
 * @author yinhui
 * @date 2019-07-15
 */
@EActivity(resName = "ipc_setting_activity_detail")
public class IpcSettingDetailActivity extends AppCompatActivity {

    @Extra
    String mModel;
    @Extra
    String mSn;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);

    }

}
