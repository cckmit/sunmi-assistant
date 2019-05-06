package com.sunmi.ipc.view;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.WifiConfiguringContract;
import com.sunmi.ipc.presenter.WifiConfiguringPresenter;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: IpcConfiguringActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_ipc_configuring")
public class IpcConfiguringActivity extends BaseMvpActivity<WifiConfiguringPresenter>
        implements WifiConfiguringContract.View {
    @ViewById(resName = "tv_tip")
    TextView tvTip;

    @Extra
    String shopId;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    Set<String> deviceIds = new HashSet<>();

    @AfterViews
    void init() {
        mPresenter = new WifiConfiguringPresenter();
        mPresenter.attachView(this);
        tvTip.setText(Html.fromHtml(getString(R.string.tip_keep_same_network)));
        for (SunmiDevice sunmiDevice : sunmiDevices) {
            IPCCall.getInstance().getToken(context,sunmiDevice.getIp());
        }
    }

    @UiThread
    @Override
    public void ipcBindWifiSuccess() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                for (SunmiDevice device : sunmiDevices) {
                    if (deviceIds.contains(device.getDeviceid())) {
                        device.setStatus(RpcErrorCode.RPC_ERR_TIMEOUT);
                    }
                }
            }
        }, 10000);
    }

    @Override
    public void onBackPressed() {

    }

    @UiThread
    @Override
    public void ipcBindWifiFail() {
        configFailDialog(R.string.tip_connect_ipc_fail, R.string.msg_in_same_wifi);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcToken, IpcConstants.bindIpc};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (TextUtils.equals(res.getErrCode(), RpcErrorCode.WHAT_ERROR + "")) {
            configFailDialog(R.string.tip_set_fail, R.string.str_bind_net_error);
        } else if (id == IpcConstants.getIpcToken) {
            if (res.getResult().has("ipc_info")) {
                try {//"ipc_info":{"sn":"sn123456", "token":"fgu766fekjgllfkekajgiorag8tr..."}
                    JSONObject jsonObject = res.getResult().getJSONObject("ipc_info");
                    if (jsonObject.has("sn") && jsonObject.has("token")) {
                        deviceIds.add(jsonObject.getString("sn"));
                        mPresenter.ipcBind(shopId, jsonObject.getString("token"),
                                1, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == IpcConstants.bindIpc) {
            try {
                setDeviceStatus(res.getResult().getString("sn"), res.getDataErrCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDeviceStatus(String sn, int status) {
        for (SunmiDevice device : sunmiDevices) {
            if (TextUtils.equals(device.getDeviceid(), sn)) {
                device.setStatus(status);
                deviceIds.remove(sn);
            }
        }
        if (deviceIds.isEmpty()) {
            sunmiDevices.get(0).setStatus(1);//todo
            IpcConfigCompletedActivity_.intent(context).shopId(shopId).sunmiDevices(sunmiDevices).start();
            finish();
        }
    }

    @UiThread
    void configFailDialog(int titleRes, int messageRes) {
        new CommonDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setCancelButton(R.string.str_quit_config,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GotoActivityUtils.gotoMainActivity(context);
                            }
                        })
                .setConfirmButton(R.string.str_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                IPCCall.getInstance().getToken(context);
                            }
                        }).create().show();
    }

}
