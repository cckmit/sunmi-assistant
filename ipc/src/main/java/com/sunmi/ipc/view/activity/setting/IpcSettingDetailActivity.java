package com.sunmi.ipc.view.activity.setting;

import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.utils.IpcUtils;
import com.xiaojinzi.component.impl.Router;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.AppApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @date 2019-07-15
 */
@EActivity(resName = "ipc_setting_activity_detail")
public class IpcSettingDetailActivity extends BaseActivity {

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "sil_camera_model")
    SettingItemLayout mCameraModel;
    @ViewById(resName = "sil_camera_sn")
    SettingItemLayout mCameraSn;
    @ViewById(resName = "sil_camera_ip")
    SettingItemLayout silCameraIp;
    @ViewById(resName = "sil_camera_mac")
    SettingItemLayout silCameraMac;
    @ViewById(resName = "sil_camera_net")
    SettingItemLayout silCameraNet;

    @Extra
    SunmiDevice mDevice;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mCameraModel.setEndContent(mDevice.getModel());
        mCameraSn.setEndContent(mDevice.getDeviceid());
        mTitleBar.getRightText().setOnClickListener(v -> deleteDevice(mDevice));
        IPCCall.getInstance().getIsWire(mDevice.getModel(), mDevice.getDeviceid());
    }

    private void deleteDevice(final SunmiDevice device) {
        new CommonDialog.Builder(context).setTitle(R.string.tip_delete_ipc)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_delete, R.color.caution_primary, (dialog, which) -> {
                    dialog.dismiss();
                    if (!NetworkUtils.isNetworkAvailable(context)) {
                        unBindNetDisConnected();
                        return;
                    }
                    unbindIpc(device.getId());
                }).create().show();
    }

    private void unBindNetDisConnected() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.str_dialog_net_disconnected)
                .setCancelButton(R.string.str_confirm, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void unbindIpc(int deviceId) {
        IpcCloudApi.getInstance().unbindIpc(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId,
                new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        shortTip(R.string.str_delete_success);
                        BaseNotification.newInstance().postNotificationName(IpcConstants.refreshIpcList);
                        Router.withApi(AppApi.class).goToMain(context);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        shortTip(R.string.str_delete_fail);
                    }
                });
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.getIsWire};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null || args.length < 1) {
            return;
        }
        if (args[0] instanceof ResponseBean) {
            ResponseBean res = (ResponseBean) args[0];
            if (id == OpcodeConstants.getIsWire) {
                if (res.getResult() != null) {
                    setWired(res.getResult());
                }
            }
        }
    }

    @UiThread
    void setWired(JSONObject res) {
        try {
            JSONObject ap = res.getJSONObject("network").getJSONObject("ap");
            String ipAddress = ap.getString("ipaddr");
            silCameraIp.setEndContent(TextUtils.isEmpty(ipAddress) ? "--" : ipAddress);
            if (res.has("mac")) {
                silCameraMac.setEndContent(IpcUtils.formatMac(res.getString("mac"), ":"));
            }
            if (res.has("conntype")) {
                int type = res.getInt("conntype");
                switch (type) {
                    case 0:
                        silCameraNet.setEndContent(R.string.str_disconnected);
                        break;
                    case 1:
                        silCameraNet.setEndContent(R.string.str_wired_connect);
                        break;
                    case 2:
                        silCameraNet.setEndContent(R.string.str_wireless_connect);
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
