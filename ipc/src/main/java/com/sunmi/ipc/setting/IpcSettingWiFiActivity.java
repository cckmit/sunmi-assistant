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
import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcSettingWiFiContract;
import com.sunmi.ipc.model.WifiListResp;
import com.sunmi.ipc.presenter.IpcSettingWifiPresenter;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

/**
 * Created by YangShiJie on 2019/7/16.
 */
@EActivity(resName = "ipc_activity_wifi")
public class IpcSettingWiFiActivity extends BaseMvpActivity<IpcSettingWifiPresenter>
        implements IpcSettingWiFiContract.View {
    private static final int COUNTDOWN_CONFIG_WIFI = 150;
    private static final int COUNTDOWN_CONFIG_SEARCHING = 100;
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
    @Extra
    String wifiSsid, wifiMgmt;
    @Extra
    int wifiIsWire;
    /**
     * 网络异常
     */
    CommonDialog commonDialog;
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
        } else if (countdown <= COUNTDOWN_CONFIG_SEARCHING && countdown % 5 == 0) {
            mPresenter.getIpcStatus(mDevice.getDeviceid(), mDevice.getModel());
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
        mPresenter = new IpcSettingWifiPresenter();
        mPresenter.attachView(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        tvWifiNme.setText(wifiSsid);
        if ("NONE".equalsIgnoreCase(wifiMgmt)) {
            ivLock.setVisibility(View.GONE);
        } else {
            ivLock.setVisibility(View.VISIBLE);
        }
        loadingWifiMessage();
    }

    private void loadingWifiMessage() {
        tvStatus.setText(R.string.ipc_setting_tip_wifi_discovery);
        if (CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
            ip = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid()).getIp();
            showLoadingDialog(getString(R.string.ipc_setting_search_wifi));
            IPCCall.getInstance().getWifiList(this, ip);//wifi list
        } else {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Click(resName = "btn_refresh")
    void btnRefreshClick() {
        loadingWifiMessage();
    }

    @UiThread
    void netExceptionView(boolean isExceptionView) {
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

    /**
     * @param ipcStatus 0离线 1在线
     */
    @Override
    public void ipcStatusSuccessView(int ipcStatus) {
        LogCat.e(TAG, "ipc status=" + ipcStatus);
        if (ipcStatus == 1) {
            stopTimer();
            shortTip(R.string.ipc_setting_dialog_wifi_success);
            Intent intent = getIntent();
            intent.putExtra("ssid", mSsid);
            intent.putExtra("mgmt", mMgmt);
            intent.putExtra("isWire", wifiIsWire);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getWifiList, IpcConstants.setIPCWifi};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        hideLoadingDialog();
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
            netExceptionView(true);
        } else if (id == IpcConstants.getWifiList) {
            getWifiList(res);
        } else if (id == IpcConstants.setIPCWifi) {
            //LogCat.e(TAG, "1111  33=" + res.getResult());
        }
    }

    @UiThread
    void getWifiList(ResponseBean res) {
        if (res.getResult() == null) {
            netExceptionView(true);
            return;
        }
        netExceptionView(false);
        tvStatus.setText(R.string.ipc_setting_tip_wifi_choose);
        WifiListResp bean = null;
        try {
            bean = new Gson().fromJson(res.getResult().toString(), WifiListResp.class);
        } catch (Exception e) {
            e.printStackTrace();
            netExceptionView(true);
        }
        if ((bean != null ? bean.getScan_results() : null) == null || bean.getScan_results().size() == 0) {
            netExceptionView(true);
            return;
        }
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
                        mMgmt = bean.getKey_mgmt();
                        if (isNoneKey) {
                            connectWifi(mSsid, mMgmt, "NONE");
                        } else {
                            inputPasswordDialog();
                        }
                    }
                });
            }
        });
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
    private void inputPasswordDialog() {
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
                        mPassword = input;
                        dialog.dismiss();
                        connectWifi(mSsid, mMgmt, mPassword);
                    }
                }).create().show();
    }

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