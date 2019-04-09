package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.dialog.InputDialog;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
@EActivity(resName = "activity_wifi_config")
public class WifiConfigActivity extends BaseActivity implements WifiListAdapter.OnItemClickListener {

    @ViewById(resName = "rv_wifi")
    RecyclerView rvWifi;

    @Extra
    String shopId;

    WifiListAdapter wifiListAdapter;

    @AfterViews
    void init() {
        IPCCall.getInstance().getWifiList(context);
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
        rvWifi.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        wifiListAdapter = new WifiListAdapter(context, list);
        wifiListAdapter.setOnItemClickListener(this);
        rvWifi.setAdapter(wifiListAdapter);
    }

    @Override
    public void onItemClick(String ssid, String mgmt) {
        if (TextUtils.equals(mgmt, "NONE")) {
            IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, "");
        } else if (TextUtils.equals(mgmt, "WPA-PSK")) {
            createDialog(ssid, mgmt);
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getWifiList, IpcConstants.setIPCWifi, IpcConstants.getApStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.getWifiList) {
            WifiListResp resp = new GsonBuilder().create()
                    .fromJson(res.getResult().toString(), WifiListResp.class);
            initApList(resp.getScan_results());
        } else if (id == IpcConstants.setIPCWifi) {//{"data":[{"opcode":"0x3116","result":{},"errcode":0}],"msg_id":"11111","errcode":0}
            IPCCall.getInstance().getApStatus(context);
        } else if (id == IpcConstants.getApStatus) {//{"data":[{"opcode":"0x3119","result":{"wireless":{"connect_status":"0"}},"errcode":0}],"msg_id":"11111","errcode":0}
            if (res.getResult().has("wireless")) {
                try {
                    JSONObject jsonObject = res.getResult().getJSONObject("wireless");
                    if (jsonObject.has("connect_status")) {//是否成功关联上前端AP(0:正在关联。1：关联成功。2：关联失败)
                        if (TextUtils.equals("1", jsonObject.getString("connect_status"))) {
                            WifiConfiguringActivity_.intent(context).shopId(shopId).start();
                            return;
                        }
                    }
                    shortTip(R.string.tip_wifi_psw_error);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createDialog(final String ssid, final String mgmt) {
        new InputDialog.Builder(context)
                .setTitle(R.string.str_input_psw)
                .setCancelButton(R.string.str_cancel)
                .setConfirmButton(R.string.str_confirm,
                        new InputDialog.ConfirmClickListener() {
                            @Override
                            public void onConfirmClick(InputDialog dialog, String input) {
                                if (TextUtils.isEmpty(input)) {
                                    shortTip("密码不能为空");
                                    return;
                                }
                                dialog.dismiss();
                                IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, input);
                            }
                        }).create().show();
    }

}
