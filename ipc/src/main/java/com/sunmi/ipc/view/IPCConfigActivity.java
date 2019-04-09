package com.sunmi.ipc.view;

import android.text.TextUtils;
import android.widget.CheckedTextView;

import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.ViewUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/3/27.
 */
@EActivity(resName = "activity_ipc_config")
class IPCConfigActivity extends BaseActivity {
    @ViewById(resName = "ctv_privacy")
    CheckedTextView ctvPrivacy;

    @Extra
    String shopId;

    @AfterViews
    void init() {
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.colorOrange, false);
        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (!ctvPrivacy.isChecked()) {
            shortTip(R.string.tip_agree_protocol);
            return;
        }
        if (TextUtils.isEmpty(IpcConstants.IPC_IP)) {
            shortTip("请将手机连接到【SUNMI_AP】无线网路");
            return;
        }
        WifiConfigActivity_.intent(context).shopId(shopId).start();
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
        LogCat.e(TAG, "ipcFound  ipcdevice = " + ipc.toString());
        IpcConstants.IPC_SN = ipc.getDeviceid();
        IpcConstants.IPC_IP = "https://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
    }

}
