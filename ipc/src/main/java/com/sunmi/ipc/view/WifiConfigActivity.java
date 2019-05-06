package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.dialog.InputDialog;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
@EActivity(resName = "activity_wifi_config")
public class WifiConfigActivity extends BaseActivity implements WifiListAdapter.OnItemClickListener {

    @ViewById(resName = "rl_main")
    RelativeLayout rlMain;
    @ViewById(resName = "rl_progress")
    RelativeLayout rlLoading;
    @ViewById(resName = "rv_wifi")
    RecyclerView rvWifi;
    @ViewById(resName = "rl_no_device")
    RelativeLayout rlNoWifi;
    @ViewById(resName = "tv_skip")
    TextView tvSkip;
    @ViewById(resName = "divider")
    View vTopDivider;

    @Extra
    String shopId;
    @Extra
    SunmiDevice sunmiDevice;

    WifiListAdapter wifiListAdapter;

    ArrayList<SunmiDevice> list = new ArrayList<>();

    private boolean hasConfig;

    @AfterViews
    void init() {
        list.add(sunmiDevice);
        IPCCall.getInstance().getWifiList(context);
        tvSkip.setText(Html.fromHtml(getString(R.string.tip_skip_config_wifi)));
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {
        vTopDivider.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
        rvWifi.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        wifiListAdapter = new WifiListAdapter(context, list);
        wifiListAdapter.setOnItemClickListener(this);
        rvWifi.setAdapter(wifiListAdapter);
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        rlLoading.setVisibility(View.VISIBLE);
        setNoWifiVisible(View.GONE);
    }

//    @Click(resName = "tv_skip")
//    void skipClick() {
//        IpcConfiguringActivity_.intent(context).sunmiDevices(list).shopId(shopId).start();
//    }

    @Override
    public void onItemClick(String ssid, String mgmt) {
        showLoadingDialog();
        if (TextUtils.equals(mgmt, "NONE")) {
            IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, "");
        } else if (TextUtils.equals(mgmt, "WPA-PSK")) {
            createDialog(ssid, mgmt);
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hideLoadingDialog();
//                if (!hasConfig) {
//
//                }
//            }
//        }, 10000);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getWifiList, IpcConstants.setIPCWifi, IpcConstants.getApStatus};
    }

    @UiThread
    public void setNoWifiVisible(int visibility) {
        rlNoWifi.setVisibility(visibility);
        rlMain.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @UiThread
    public void setLoadingVisible(int visibility) {
        rlLoading.setVisibility(visibility);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.getWifiList) {
            setLoadingVisible(View.GONE);
            if (res == null || TextUtils.equals(res.getErrCode(), RpcErrorCode.WHAT_ERROR + "")) {
                setNoWifiVisible(View.VISIBLE);
                return;
            }
            WifiListResp resp = new GsonBuilder().create()
                    .fromJson(res.getResult().toString(), WifiListResp.class);
            if (resp == null || resp.getScan_results() == null || resp.getScan_results().size() == 0) {
                setNoWifiVisible(View.VISIBLE);
                return;
            }
            initApList(resp.getScan_results());
        } else if (id == IpcConstants.setIPCWifi) {
            //{"data":[{"opcode":"0x3116","result":{},"errcode":0}],"msg_id":"11111","errcode":0}
            IPCCall.getInstance().getApStatus(context);
        } else if (id == IpcConstants.getApStatus) {
            //{"data":[{"opcode":"0x3119","result":{"wireless":{"connect_status":"0"}},"errcode":0}],"msg_id":"11111","errcode":0}
            hideLoadingDialog();
            if (res.getResult() != null && res.getResult().has("wireless")) {
                try {
                    JSONObject jsonObject = res.getResult().getJSONObject("wireless");
                    if (jsonObject.has("connect_status")) {//是否成功关联上前端AP(0:正在关联。1：关联成功。2：关联失败)
                        if (TextUtils.equals("1", jsonObject.getString("connect_status"))) {
                            IpcConfiguringActivity_.intent(context).sunmiDevices(list).shopId(shopId).start();
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
