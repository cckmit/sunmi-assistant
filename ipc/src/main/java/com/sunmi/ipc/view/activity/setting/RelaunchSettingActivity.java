package com.sunmi.ipc.view.activity.setting;

import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yangShiJie
 * @date 2019-11-15
 */
@EActivity(resName = "ipc_setting_preview_relaunch")
public class RelaunchSettingActivity extends BaseActivity {
    private static final int IPC_EVENT_OPCODE_ONLINE = 4200;
    @Extra
    SunmiDevice mDevice;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "sil_relaunch")
    void relaunchClick() {
        relaunchDialog();
    }

    private void relaunchDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(context)
                .setTitle(getString(R.string.ipc_setting_relaunch))
                .setMessage(getString(R.string.ipc_setting_dev_rebooting))
                .setConfirmButton(R.string.str_confirm, R.color.common_orange, (dialog, which) -> {
                    if (!NetworkUtils.isNetworkAvailable(context)) {
                        shortTip(R.string.str_net_exception);
                    } else {
                        relaunch();
                        dialog.dismiss();
                    }
                }).setCancelButton(R.string.sm_cancel, R.color.text_main).create();
        commonDialog.showWithOutTouchable(false);
    }

    private void relaunch() {
        IPCCall.getInstance().ipcRelaunch(this, mDevice.getDeviceid(), mDevice.getModel());
        RelaunchStartSettingActivity_.intent(context).mDevice(mDevice).start();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IPC_EVENT_OPCODE_ONLINE,
                OpcodeConstants.ipcRelaunch};
    }

    /**
     * @param id   id
     * @param args args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == IPC_EVENT_OPCODE_ONLINE) {
            //设备重新上线 0x4200
            try {
                JSONObject object = res.getResult();
                String sn = object.getString("sn");
                if (TextUtils.equals(sn, mDevice.getDeviceid())) {
                    //发送udp
                    SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered, true);
                    BaseNotification.newInstance().postNotificationName(IpcConstants.ipcRelaunchSuccess);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        else if (id == OpcodeConstants.ipcRelaunch) {
//            if (res.getDataErrCode() == 1) {
//                RelaunchStartSettingActivity_.intent(context).mDevice(mDevice).start();
//            } else {
//                shortTip(R.string.str_net_exception);
//            }
//        }
    }
}
