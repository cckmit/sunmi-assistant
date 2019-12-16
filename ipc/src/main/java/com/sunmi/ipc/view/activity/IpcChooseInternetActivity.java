package com.sunmi.ipc.view.activity;

import com.sunmi.ipc.config.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-21.
 */
@EActivity(resName = "activity_ipc_choose_internet")
public class IpcChooseInternetActivity extends BaseActivity {

    @Extra
    int ipcType;
    @Extra
    int source;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "cv_wired")
    public void wiredClick() {
        IpcStartConfigNetworkActivity_.intent(context).ipcType(ipcType).network(IpcConstants.IPC_CONFIG_MODE_WIRED).source(source).start();
    }

    @Click(resName = "cv_wireless")
    public void wirelessClick() {
        IpcStartConfigNetworkActivity_.intent(context).ipcType(ipcType).network(IpcConstants.IPC_CONFIG_MODE_AP).source(source).start();
    }


}
