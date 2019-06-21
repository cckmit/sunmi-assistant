package com.sunmi.ipc.view;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcConfiguringContract;
import com.sunmi.ipc.presenter.IpcConfiguringPresenter;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: IpcConfiguringActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_ipc_configuring")
public class IpcConfiguringActivity extends BaseMvpActivity<IpcConfiguringPresenter>
        implements IpcConfiguringContract.View {
    @ViewById(resName = "tv_tip")
    TextView tvTip;

    @Extra
    String shopId;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    Set<String> deviceIds = new HashSet<>();
    private boolean isTimeoutStart;

    @AfterViews
    void init() {
        mPresenter = new IpcConfiguringPresenter();
        mPresenter.attachView(this);
        tvTip.setText(Html.fromHtml(getString(R.string.tip_keep_same_network)));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bind();
            }
        }, 5000);
    }

    private void bind() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            configFailDialog(R.string.tip_set_fail, R.string.str_bind_net_error);
            return;
        }
        if (sunmiDevices != null) {
            for (SunmiDevice sunmiDevice : sunmiDevices) {
                deviceIds.add(sunmiDevice.getDeviceid());
                mPresenter.ipcBind(shopId, sunmiDevice.getDeviceid(),
                        sunmiDevice.getToken(), 1, 1);
            }
        }
    }

    @UiThread
    @Override
    public void ipcBindWifiSuccess(String sn) {
        startCountDown();
    }

    private void startCountDown() {
        if (!isTimeoutStart) {
            isTimeoutStart = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (deviceIds.isEmpty()) return;
                    for (String deviceId : deviceIds) {
                        setDeviceStatus(deviceId, RpcErrorCode.RPC_ERR_TIMEOUT);
                    }
                    configComplete();
                }
            }, 30000);
        }
    }

    @UiThread
    @Override
    public void ipcBindWifiFail(String sn, int code, String msg) {
        startCountDown();
        setDeviceStatus(sn, code);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcToken, IpcConstants.bindIpc};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
            configFailDialog(R.string.tip_set_fail, R.string.str_bind_net_error);
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
                deviceIds.remove(sn);
                device.setStatus(status);
            }
        }
        configComplete();
    }

    private void configComplete() {
        if (deviceIds.isEmpty()) {
            IpcConfigCompletedActivity_.intent(context).shopId(shopId)
                    .sunmiDevices(sunmiDevices).start();
            finish();
        }
    }

    @UiThread
    void configFailDialog(int titleRes, int messageRes) {
        new CommonDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setCancelButton(R.string.str_quit_config, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GotoActivityUtils.gotoMainActivity(context);
                    }
                })
                .setConfirmButton(R.string.str_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bind();
                    }
                }).create().show();
    }

}
