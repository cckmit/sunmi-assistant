package com.sunmi.ipc.view.activity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.view.WifiListAdapter;

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
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.dialog.InputDialog;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
@EActivity(resName = "activity_wifi_config")
public class WifiConfigActivity extends BaseActivity
        implements WifiListAdapter.OnItemClickListener {

    @ViewById(resName = "rl_main")
    RelativeLayout rlMain;
    @ViewById(resName = "rl_progress")
    RelativeLayout rlLoading;
    @ViewById(resName = "tv_progress_tip")
    TextView tvProgressTip;
    @ViewById(resName = "rv_wifi")
    SmRecyclerView rvWifi;
    @ViewById(resName = "rl_no_device")
    RelativeLayout rlNoWifi;
    @ViewById(resName = "v_divider")
    View vTopDivider;

    @Extra
    String shopId;
    @Extra
    int deviceType;
    @Extra
    SunmiDevice sunmiDevice;
    @Extra
    int source;

    private static int TIMEOUT_GET_WIFI = 15_000;
    private static int TIMEOUT_GET_IPC_STATUS_FAIL = 10_000;
    private static int DURATION_STATUS_GOT = 20_000;

    private Timer timer = new Timer();
    private CountDownTimer countDownTimer;//获取online状态后超时等待
    private boolean alreadyFinish;
    private int failGetStatusCount;

    private List<WifiListResp.ScanResultsBean> wifiList = new ArrayList<>();

    @AfterViews
    void init() {
        tvProgressTip.setText(R.string.loading_search_wifi);
        rvWifi.init(R.drawable.shap_line_divider);
        getWifiList();
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {
        vTopDivider.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
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
        if (TextUtils.equals(mgmt, "NONE")) {//无密码
            showLoadingDialog();
            IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, "", sunmiDevice.getIp());
        } else if (TextUtils.equals(mgmt, "WPA-PSK")) {
            createDialog(ssid, mgmt);
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.getWifiList, OpcodeConstants.setIPCWifi, OpcodeConstants.getApStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == OpcodeConstants.getWifiList) {
            wifiListGetSuccess(res);
        } else if (id == OpcodeConstants.setIPCWifi) {
            setIpcWifiSuccess();
        } else if (id == OpcodeConstants.getApStatus) {
            wifiStatusGetSuccess(res);
        }
    }

    private void getWifiList() {
        IPCCall.getInstance().getWifiList(context, sunmiDevice.getIp());
        new Handler().postDelayed(() -> {
            if (wifiList.size() == 0) {
                setNoWifiVisible(View.VISIBLE);
            }
        }, TIMEOUT_GET_WIFI);
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

    @UiThread
    void setIpcWifiSuccess() {
        startGetStatusTimer();
    }

    //{"data":[{"opcode":"0x3119","result":{"online":0,"wireless":{"connect_status":"0"}},"errcode":0}],"msg_id":"11111","errcode":0}
    @UiThread
    void wifiStatusGetSuccess(ResponseBean res) {
        if (!res.isErrCodeZero()) {
            countGetStatusFail();
            return;
        }
        if (res.getResult() != null && res.getResult().has("wireless")) {
            try {
                JSONObject jsonObject = res.getResult().getJSONObject("wireless");
                if (jsonObject.has("connect_status")) {//是否成功关联上前端AP(0:正在关联。1：关联成功。2：关联失败)
                    String connectStatus = jsonObject.getString("connect_status");
                    if (TextUtils.equals("1", connectStatus)) {//返回1，继续判断online状态
                        if (res.getResult().has("online")) {
                            int online = res.getResult().getInt("online");
                            if (1 == online) {
                                stopTimer();
                                gotoBind();
                            } else if (0 == online) {
                                startWaitCountTimer();
                            }
                        }
                    } else if (TextUtils.equals("2", connectStatus)) {
                        stopTimer();
                        cancelWaitCountTimer();
                        hideLoadingDialog();
                        shortTip(R.string.tip_wifi_psw_error);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void countGetStatusFail() {
        failGetStatusCount++;
        if (failGetStatusCount > 2) {
            cancelWaitCountTimer();
            stopTimer();
            failGetStatusCount = 0;
            new Handler().postDelayed(() -> {
                hideLoadingDialog();
                gotoBind();
            }, TIMEOUT_GET_IPC_STATUS_FAIL);
        }
    }

    /**
     * 主动轮询获取设备联网状态
     */
    private void startGetStatusTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                IPCCall.getInstance().getApStatus(context, sunmiDevice.getIp());
            }
        }, 0, 1000);
    }

    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startWaitCountTimer() {
        if (countDownTimer != null) return;
        countDownTimer = new CountDownTimer(DURATION_STATUS_GOT, DURATION_STATUS_GOT) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                gotoBind();
            }
        };
        countDownTimer.start();
    }

    private void cancelWaitCountTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void gotoBind() {
        if (alreadyFinish) return;
        alreadyFinish = true;
        ArrayList<SunmiDevice> list = new ArrayList<>();
        list.add(sunmiDevice);
        if (list.size() > 0) {
            IpcConfiguringActivity_.intent(context)
                    .deviceType(deviceType).sunmiDevices(list).shopId(shopId).source(source).start();
            hideLoadingDialog();
            finish();
        }
    }

    @UiThread
    void wifiListGetSuccess(ResponseBean res) {
        setLoadingVisible(View.GONE);
        if (res == null || RpcErrorCode.RPC_COMMON_ERROR ==res.getErrCode()) {
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
                .setTitle(R.string.hint_input_password)
                .setHint(R.string.str_tip_password_8_64)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_save, (dialog, input) -> {
                    if (TextUtils.isEmpty(input)) {
                        shortTip(R.string.str_text_password_no_null);
                        return;
                    }
                    if (input.length() < 8 || input.length() > 64) {
                        shortTip(R.string.tip_wifi_psw_length_inconformity);
                        return;
                    }
                    dialog.dismiss();
                    showLoadingDialog();
                    IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, input, sunmiDevice.getIp());
                }).create().show();
    }

}
