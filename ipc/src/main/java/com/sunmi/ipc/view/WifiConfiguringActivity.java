package com.sunmi.ipc.view;

import android.content.DialogInterface;
import android.text.Html;
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

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: WifiConfiguringActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_configuring")
public class WifiConfiguringActivity extends BaseMvpActivity<WifiConfiguringPresenter>
        implements WifiConfiguringContract.View {
    @ViewById(resName = "tv_tip")
    TextView tvTip;

    @Extra
    String shopId;
    @Extra
    SunmiDevice sunmiDevice;

    @AfterViews
    void init() {
        mPresenter = new WifiConfiguringPresenter();
        mPresenter.attachView(this);
        tvTip.setText(Html.fromHtml(getString(R.string.tip_keep_same_network)));
        IPCCall.getInstance().getToken(context);
    }

    @UiThread
    @Override
    public void ipcBindWifiSuccess() {
        sunmiDevice.setStatus(1);
        WifiConfigCompletedActivity_.intent(context).shopId(shopId).sunmiDevice(sunmiDevice).start();
    }

    @UiThread
    @Override
    public void ipcBindWifiFail() {
        bindFailDialog();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcToken, IpcConstants.bindIpc};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.getIpcToken) {
            if (res.getResult().has("ipc_info")) {
                try {//"ipc_info":{"sn":"sn123456", "token":"fgu766fekjgllfkekajgiorag8tr..."}
                    JSONObject jsonObject = res.getResult().getJSONObject("ipc_info");
                    if (jsonObject.has("token")) {
                        mPresenter.ipcBind(shopId, jsonObject.getString("token"),
                                1, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == IpcConstants.bindIpc) {
            if (res.getDataErrCode() == 1) {
                sunmiDevice.setStatus(1);
                WifiConfigCompletedActivity_.intent(context).shopId(shopId).sunmiDevice(sunmiDevice).start();
            } else if (res.getDataErrCode() == 5508) {
                shortTip("已经绑定，不要重复绑定");
            } else if (res.getDataErrCode() == 5501) {
                shortTip("设备不存在");
            } else if (res.getDataErrCode() == 5510) {
                shortTip("已被其他用户绑定");
            } else {
                shortTip(res.getResult().toString());
            }
        }
    }

    @UiThread
    void configFailDialog() {
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_set_fail)
                .setMessage(R.string.msg_in_same_wifi)
                .setCancelButton(R.string.str_cancel)
                .setConfirmButton(R.string.str_retry,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
    }

    @UiThread
    void bindFailDialog() {
        new CommonDialog.Builder(context)
                .setTitle("摄像头已绑定其他商米账号，如要添加请先从原账号解绑")
                .setConfirmButton(R.string.str_confirm).create().show();
    }

}
