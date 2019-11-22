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
@EActivity(resName = "activity_ipc_chose_internet")
public class IpcChoseInternetActivity extends BaseActivity {

    @Extra
    int ipcType;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "cv_wired")
    public void wiredClick() {
        IpcStartConfigNetworkActivity_.intent(context).ipcType(ipcType).network(IpcConstants.IPC_WIRED_NETWORK).start();
    }

    @Click(resName = "cv_wireless")
    public void wirelessClick() {
        IpcStartConfigNetworkActivity_.intent(context).ipcType(ipcType).network(IpcConstants.IPC_WIRELESS_NETWORK).start();
    }


}
