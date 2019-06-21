package com.sunmi.ipc.view;

import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;

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

    private int connectStatus = -1;
    private Timer timer = new Timer();
    private int retryCount;
    private boolean alreadyFinish;

    private List<WifiListResp.ScanResultsBean> wifiList = new ArrayList<>();

    @AfterViews
    void init() {
        tvSkip.setText(Html.fromHtml(getString(R.string.tip_skip_config_wifi)));
        getWifiList();
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {
        vTopDivider.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
        rvWifi.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        WifiListAdapter wifiListAdapter = new WifiListAdapter(context, list);
        wifiListAdapter.setOnItemClickListener(this);
        rvWifi.setAdapter(wifiListAdapter);
    }

    @Click(resName = "btn_retry")
    void refreshClick() {
        rlLoading.setVisibility(View.VISIBLE);
        setNoWifiVisible(View.GONE);
        getWifiList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public void onItemClick(String ssid, String mgmt) {
        if (TextUtils.equals(mgmt, "NONE")) {
            showLoadingDialog();
            IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, "", sunmiDevice.getIp());
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
            wifiListGetSuccess(res);
        } else if (id == IpcConstants.setIPCWifi) {
            setIpcWifiSuccess();
        } else if (id == IpcConstants.getApStatus) {
            wifiStatusGetSuccess(res);
        }
    }

    private void getWifiList() {
        IPCCall.getInstance().getWifiList(context, sunmiDevice.getIp());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                if (wifiList.size() == 0) {
                    setNoWifiVisible(View.VISIBLE);
                }
            }
        }, 10000);
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

    //{"data":[{"opcode":"0x3116","result":{},"errcode":0}],"msg_id":"11111","errcode":0}
    @UiThread
    void setIpcWifiSuccess() {
        startGetStatusTimer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                stopTimer();
                if (connectStatus != 1) {
                    gotoBind();
                }
            }
        }, 20000);
    }

    //{"data":[{"opcode":"0x3119","result":{"wireless":{"connect_status":"0"}},"errcode":0}],"msg_id":"11111","errcode":0}
    @UiThread
    void wifiStatusGetSuccess(ResponseBean res) {
        if (res.getResult() != null && res.getResult().has("wireless")) {
            try {
                JSONObject jsonObject = res.getResult().getJSONObject("wireless");
                if (jsonObject.has("connect_status")) {//是否成功关联上前端AP(0:正在关联。1：关联成功。2：关联失败)
                    connectStatus = jsonObject.getInt("connect_status");
                    if (0 == connectStatus) {
                        return;
                    }
                    stopTimer();
                    if (1 == connectStatus) {
                        hideLoadingDialog();
                        gotoBind();
                    } else {
                        hideLoadingDialog();
                        shortTip(R.string.tip_wifi_psw_error);
                    }
                } else {
                    hideLoadingDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startGetStatusTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (retryCount == 30) stopTimer();
                retryCount++;
                IPCCall.getInstance().getApStatus(context, sunmiDevice.getIp());
            }
        }, 0, 3000);
    }

    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void gotoBind() {
        if (alreadyFinish) return;
        alreadyFinish = true;
        ArrayList<SunmiDevice> list = new ArrayList<>();
        list.add(sunmiDevice);
        if (list.size() > 0) {
            IpcConfiguringActivity_.intent(context).sunmiDevices(list).shopId(shopId).start();
            finish();
        }
    }

    @UiThread
    void wifiListGetSuccess(ResponseBean res) {
        setLoadingVisible(View.GONE);
        if (res == null || TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
            setNoWifiVisible(View.VISIBLE);
            return;
        }
        try {
            WifiListResp resp = new GsonBuilder().create()
                    .fromJson(res.getResult().toString(), WifiListResp.class);
            if (resp == null || resp.getScan_results() == null || resp.getScan_results().size() == 0) {
                setNoWifiVisible(View.VISIBLE);
                return;
            }
            if (resp.getScan_results() != null) {
                wifiList = resp.getScan_results();
                initApList(resp.getScan_results());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDialog(final String ssid, final String mgmt) {
        new InputDialog.Builder(context)
                .setTitle(R.string.str_input_psw)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm,
                        new InputDialog.ConfirmClickListener() {
                            @Override
                            public void onConfirmClick(InputDialog dialog, String input) {
                                if (TextUtils.isEmpty(input)) {
                                    shortTip(R.string.str_text_password_no_null);
                                    return;
                                }
                                dialog.dismiss();
                                showLoadingDialog();
                                IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, input, sunmiDevice.getIp());
                            }
                        }).create().show();
    }

}
