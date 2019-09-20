package com.sunmi.ipc.view.activity;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcConfiguringContract;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.presenter.IpcConfiguringPresenter;
import com.sunmi.ipc.rpc.OpcodeConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.mqtt.MqttManager;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
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
    @ViewById(resName = "iv_device")
    ImageView ivDevice;

    @Extra
    String shopId;
    @Extra
    int deviceType;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    Set<String> deviceIds = new HashSet<>();
    private boolean isTimeoutStart;

    private int retryCount;

    @AfterViews
    void init() {
        if (CommonConstants.TYPE_IPC_FS == deviceType) {
            ivDevice.setImageResource(R.mipmap.ic_no_fs);
        }
        mPresenter = new IpcConfiguringPresenter();
        mPresenter.attachView(this);
        tvTip.setText(Html.fromHtml(getString(R.string.tip_keep_same_network)));
        bind();
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
    public void ipcBindSuccess(String sn) {
        MqttManager.getInstance().reconnect();
        retryCount = 20;
        startCountDown();
    }

    private synchronized void startCountDown() {
        if (!isTimeoutStart) {
            isTimeoutStart = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (deviceIds.isEmpty()) {
                        return;
                    }
                    mPresenter.getIpcList(SpUtils.getCompanyId(), shopId);
                }
            }, 30000);
        }
    }

    @UiThread
    @Override
    public void ipcBindFail(final String sn, final int code, String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sunmiDevices.size() > 1 || retryCount == 20
                        || code == 5501 || code == 5508 || code == 5509 || code == 5510
                        || code == 5511 || code == 5512 || code == 5013 || code == 5514) {
                    startCountDown();
                    setDeviceStatus(sn, code);
                    return;
                }
                retryCount++;
                bind();
            }
        }, 2000);
    }

    @Override
    public void getIpcListSuccess(List<IpcListResp.SsListBean> ipcList) {
        for (IpcListResp.SsListBean bean : ipcList) {
            for (SunmiDevice device : sunmiDevices) {
                if (TextUtils.equals(bean.getSn(), device.getDeviceid())) {
                    setDeviceStatus(device.getDeviceid(), 1);
                }
            }
        }
        setRemainDevicesStatus();
    }

    @Override
    public void getIpcListFail(int code, String msg) {
        setRemainDevicesStatus();
    }

    /**
     * 剩余设备配置失败
     */
    private void setRemainDevicesStatus() {
        for (SunmiDevice device : sunmiDevices) {
            if (deviceIds.contains(device.getDeviceid())) {
                device.setStatus(RpcErrorCode.RPC_ERR_TIMEOUT);
            }
        }
        deviceIds.clear();
        configComplete();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.getIpcToken, OpcodeConstants.bindIpc};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
            configFailDialog(R.string.tip_set_fail, R.string.str_bind_net_error);
        } else if (id == OpcodeConstants.bindIpc) {
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
        BaseNotification.newInstance().postNotificationName(IpcConstants.refreshIpcList);
        if (deviceIds.isEmpty()) {
            IpcConfigCompletedActivity_.intent(context).shopId(shopId)
                    .deviceType(deviceType).sunmiDevices(sunmiDevices).start();
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
