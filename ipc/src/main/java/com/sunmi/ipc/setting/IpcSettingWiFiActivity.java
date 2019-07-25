package com.sunmi.ipc.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcConnectApResp;
import com.sunmi.ipc.model.IpcConnectStatusResp;
import com.sunmi.ipc.model.WifiListResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

/**
 * Created by YangShiJie on 2019/7/16.
 */
@EActivity(resName = "ipc_activity_wifi")
public class IpcSettingWiFiActivity extends BaseActivity {
    private static final int COUNTDOWN_CONFIG_WIFI = 60;
    @ViewById(resName = "tv_wifi_name")
    TextView tvWifiNme;
    @ViewById(resName = "tv_status")
    TextView tvStatus;
    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;
    @ViewById(resName = "iv_lock")
    ImageView ivLock;
    @ViewById(resName = "rl_wifi")
    RelativeLayout rlWifi;
    @ViewById(resName = "rl_net_exception")
    RelativeLayout rlNetException;

    @Extra
    SunmiDevice mDevice;

    private TextView tvProgress;
    private Dialog connectDialog;

    private Timer timer = null;
    private TimerTask timerTask = null;
    private int countdown = COUNTDOWN_CONFIG_WIFI;
    private String ip, mSsid, mMgmt, mPassword;


    //开启计时
    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                showDownloadProgress();
                IPCCall.getInstance().getApStatus(IpcSettingWiFiActivity.this, ip);
            }
        }, 0, 1000);
    }

    @UiThread
    void showDownloadProgress() {
        countdown--;
        tvProgress.setText(getString(R.string.ipc_setting_dialog_wifi_progress, countdown));
        if (countdown == 0) {
            stopTimer();
            netExceptionDialog();
        }
    }

    private void connectDialogDismiss() {
        if (connectDialog != null) {
            connectDialog.dismiss();
            connectDialog = null;
        }
    }

    // 停止计时
    private void stopTimer() {
        countdown = COUNTDOWN_CONFIG_WIFI;
        connectDialogDismiss();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        tvStatus.setText(R.string.ipc_setting_tip_wifi_discovery);
        if (CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
            ip = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid()).getIp();
        }
        showLoadingDialog(getString(R.string.ipc_setting_search_wifi));
        IPCCall.getInstance().getIpcConnectApMsg(this, ip);//ipc连接wifi信息
        IPCCall.getInstance().getWifiList(this, ip);//wifi list
    }

    private void netExceptionView(boolean isExceptionView) {
        if (isExceptionView) {
            tvStatus.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            rlWifi.setVisibility(View.GONE);
            rlNetException.setVisibility(View.VISIBLE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            rlWifi.setVisibility(View.VISIBLE);
            rlNetException.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcConnectApMsg, IpcConstants.getWifiList,
                IpcConstants.setIPCWifi, IpcConstants.getApStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (TextUtils.equals(res.getErrCode(), CommonConstants.WHAT_ERROR + "")) {
            netExceptionView(true);
        } else if (id == IpcConstants.getIpcConnectApMsg) {
            getIpcConnectApMsg(res);
        } else if (id == IpcConstants.getWifiList) {
            hideLoadingDialog();
            getWifiList(res);
        } else if (id == IpcConstants.setIPCWifi) {
            //LogCat.e(TAG, "1111  33=" + res.getResult());
        } else if (id == IpcConstants.getApStatus) {
            queryConnectStatus(res);
        }
    }

    @UiThread
    void getIpcConnectApMsg(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        IpcConnectApResp device = new GsonBuilder().create().fromJson(res.getResult().toString(), IpcConnectApResp.class);
        tvWifiNme.setText(device.getWireless().getSsid());
        if ("NONE".equalsIgnoreCase(device.getWireless().getKey_mgmt())) {
            ivLock.setVisibility(View.GONE);
        } else {
            ivLock.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    void getWifiList(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        tvStatus.setText(R.string.ipc_setting_tip_wifi_choose);
        WifiListResp bean = new Gson().fromJson(res.getResult().toString(), WifiListResp.class);
        recyclerView.setAdapter(new CommonListAdapter<WifiListResp.ScanResultsBean>(context,
                R.layout.ipc_item_wifi, bean.getScan_results()) {
            @Override
            public void convert(ViewHolder holder, final WifiListResp.ScanResultsBean bean) {
                TextView tvName = holder.getView(R.id.tv_wifi_name);
                ImageView ivLock = holder.getView(R.id.iv_lock);
                tvName.setText(bean.getSsid());
                final boolean isNoneKey = ("NONE".equalsIgnoreCase(bean.getKey_mgmt()));
                if (isNoneKey) {
                    ivLock.setVisibility(View.GONE);
                } else {
                    ivLock.setVisibility(View.VISIBLE);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSsid = bean.getSsid();
                        String mgmt = bean.getKey_mgmt();
                        if (isNoneKey) {
                            connectWifi(mSsid, mgmt, "NONE");
                        } else {
                            inputPasswordDialog(mSsid, mgmt);
                        }
                    }
                });
            }
        });
    }

    @UiThread
    void queryConnectStatus(ResponseBean res) {
        //0:正在关联。1：关联成功。2：关联失败
        if (res.getResult() != null && res.getDataErrCode() == 1) {
            netExceptionView(false);
            IpcConnectStatusResp bean = new Gson().fromJson(res.getResult().toString(), IpcConnectStatusResp.class);
            String status = bean.getWireless().getConnect_status();
            if (TextUtils.equals("1", status)) {
                stopTimer();
                shortTip(R.string.ipc_setting_dialog_wifi_success);
                Intent intent = getIntent();
                intent.putExtra("ssid", mSsid);
                setResult(RESULT_OK, intent);
                finish();
            } else if (TextUtils.equals("2", status)) {
                stopTimer();
                connectDialogDismiss();
                netExceptionDialog();
            }
        } else {
            netExceptionView(true);
        }
    }

    //连接wifi
    private void connectWifi(String ssid, String mgmt, String password) {
        startTimer();
        IPCCall.getInstance().setIPCWifi(context, ssid, mgmt, password, ip);
        connectWifiProgress();
    }

    /**
     * 输入wifi密码
     */
    private void inputPasswordDialog(final String ssid, final String mgmt) {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_wifi_input_pwd)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        if (TextUtils.isEmpty(input)) {
                            shortTip(R.string.str_text_password_no_null);
                            return;
                        }
                        mSsid = ssid;
                        mMgmt = mgmt;
                        mPassword = input;
                        dialog.dismiss();
                        connectWifi(mSsid, mMgmt, mPassword);
                    }
                }).create().show();
    }

//    /**
//     * wifi密码error
//     */
//    private void passwordErrorDialog() {
//        CommonDialog commonDialog = new CommonDialog.Builder(this)
//                .setTitle(R.string.ipc_setting_dialog_wifi_pwd_error)
//                .setMessage(getString(R.string.ipc_setting_dialog_wifi_pwd_error_content))
//                .setConfirmButton(R.string.ipc_setting_dialog_wifi_pwd_retry, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        connectWifi();
//                    }
//                }).setCancelButton(R.string.str_close).create();
//        commonDialog.showWithOutTouchable(false);
//    }

    /**
     * 网络异常
     */
    CommonDialog commonDialog;

    private void netExceptionDialog() {
        if (commonDialog != null) return;
        commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_wifi_net_error)
                .setMessage(getString(R.string.ipc_setting_dialog_wifi_net_error_content))
                .setConfirmButton(R.string.str_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectWifi(mSsid, mMgmt, mPassword);
                    }
                }).setCancelButton(R.string.str_close).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 连接wifi中
     */
    private void connectWifiProgress() {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        connectDialog = new Dialog(context, R.style.Son_dialog);
        View layout = inflater.inflate(R.layout.dialog_connect_wifi_progress, null);
        tvProgress = layout.findViewById(R.id.tv_countDown);
        tvProgress.setText(getString(R.string.ipc_setting_dialog_wifi_progress, 0));

        connectDialog.setContentView(layout);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.show();
    }

}