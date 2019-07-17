package com.sunmi.ipc.view;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/4/9.
 */
@EActivity(resName = "activity_ipc_test")
public class IpcTestActivity extends BaseActivity {
    @Extra
    String shopId;
    @Extra
    int ipcType;

    @AfterViews
    void init() {
        IPCFragment ipcFragment = IPCFragment_.builder().shopId(shopId).build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_layout, ipcFragment);
        transaction.commit();
        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcDiscovered};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == IpcConstants.ipcDiscovered) {
            SunmiDevice ipc = (SunmiDevice) args[0];
            ipcFound(ipc);
        }
    }

    //1 udp搜索完成 --> ap登录
    private void ipcFound(SunmiDevice ipc) {
        if (ipcType == 0 && DeviceTypeUtils.getInstance().isFS1(ipc.getModel())) {
            LogCat.e(TAG, "ipcFound  ipcdevice = " + ipc.toString());
            IpcConstants.IPC_SN = ipc.getDeviceid();
            IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
            LogCat.e(TAG, "ipcFound  IPC_IP = " + ipc.getIp());
        } else if (ipcType == 1 && DeviceTypeUtils.getInstance().isSS1(ipc.getModel())) {
            LogCat.e(TAG, "ipcFound  ipcdevice = " + ipc.toString());
            IpcConstants.IPC_SN = ipc.getDeviceid();
            IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
            LogCat.e(TAG, "ipcFound  IPC_IP = " + ipc.getIp());
        }
    }

}
