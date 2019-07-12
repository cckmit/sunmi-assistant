package com.sunmi.ipc.ipcset;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * Created by YangShiJie on 2019/7/12.
 */
@EActivity(resName = "activity_ipc_setting")
public class IpcSettingActivity extends BaseActivity {

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "sil_camera_name")
    void cameraNameClick() {

    }

    @Click(resName = "sil_camera_detail")
    void cameraDetailClick() {

    }
}
