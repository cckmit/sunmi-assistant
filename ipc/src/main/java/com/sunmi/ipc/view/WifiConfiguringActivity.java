package com.sunmi.ipc.view;

import android.content.DialogInterface;

import com.sunmi.ipc.IPCCall;
import com.sunmi.ipc.IpcConstants;
import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.WifiConfiguringContract;
import com.sunmi.ipc.presenter.WifiConfiguringPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description: WifiConfiguringActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_configuring")
public class WifiConfiguringActivity extends BaseMvpActivity<WifiConfiguringPresenter>
        implements WifiConfiguringContract.View {

    @Extra
    String shopId;

    @AfterViews
    void init() {
        mPresenter = new WifiConfiguringPresenter();
        mPresenter.attachView(this);
        IPCCall.getInstance().getToken(context);
    }

    @UiThread
    @Override
    public void ipcBindWifiSuccess() {
        WifiConfigCompletedActivity_.intent(context).start();
    }

    @UiThread
    @Override
    public void ipcBindWifiFail() {
        bindFailDialog();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcToken};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == IpcConstants.getIpcToken) {
            ResponseBean res = (ResponseBean) args[0];
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
