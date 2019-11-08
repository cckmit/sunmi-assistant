package com.sunmi.ipc.setting;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author YangShiJie
 * @date 2019/7/15
 */
@EActivity(resName = "ipc_activity_version")
public class IpcSettingVersionActivity extends BaseActivity implements View.OnClickListener {
    private static final int IPC_EVENT_OPCODE_STATUS = 4103;
    private static final int IPC_EVENT_OPCODE_ONLINE = 4200;
    /**
     * 升级类型
     */
    private static final int IPC_DOWNLOAD = 1;
    private static final int IPC_UPGRADE_AI = 2;
    private static final int IPC_RELAUNCH = 3;
    private static final int IPC_CONNECT_TIMEOUT = 4;
    /**
     * 升级时间
     */
    private static final long IPC_DOWNLOAD_TIME = 225 * 1000; //下载时间
    private static final long IPC_UPGRADE_TIME_AI = 285 * 1000;//AI固件升级时间
    private static final long IPC_RELAUNCH_TIME = 220 * 1000;//重启时间+固件升级时间(55 +165)*1000
    /**
     * 下载进度
     */
    private static final int PERCENT_DOWNLOAD = 30;//下载进度30%
    private static final int PERCENT_UPGRADE = 50;//升级进度50%
    private static final int PERCENT_RELAUNCH = 20;//重启进度20%
    /**
     * ss,fs 版本
     */
    private static final int ssVersion = 126, fsVersion = 113;
    /**
     * 开启定时
     */
    private final MyCountDownTimer downloadTimer = new MyCountDownTimer(IPC_DOWNLOAD_TIME, 1000);
    private final MyCountDownTimer upgradeAiTimer = new MyCountDownTimer(IPC_UPGRADE_TIME_AI, 1000);
    private final MyCountDownTimer relaunchTimer = new MyCountDownTimer(IPC_RELAUNCH_TIME, 1000);
    private final MyCountDownTimer mqttConnectTimeout = new MyCountDownTimer(15 * 1000, 1000);
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "tv_device_id")
    TextView tvDeviceId;
    @ViewById(resName = "tv_ipc_status")
    TextView tvIpcStatus;
    @ViewById(resName = "tv_ipc_upgrade_tip")
    TextView tvIpcUpgradeTip;
    @ViewById(resName = "btn_upgrade")
    Button btnUpgrade;
    @ViewById(resName = "iv_ipc")
    ImageView ivIpc;
    @ViewById(resName = "pBar_progress")
    ProgressBar mProgress;
    @ViewById(resName = "ipc_setting_upgrade_group")
    Group ipcSettingUpgradeGroup;
    @Extra
    SunmiDevice mDevice;
    @Extra
    IpcNewFirmwareResp mResp;
    private boolean isSS;
    //升级进度
    private int setProgress;
    //升级过程中的状态
    private int mUpgradeStatus;
    //固件是否包含ai升级
    private boolean isAiUpgrade;
    //是否升级失败
    private boolean isUpgradeFail;
    private CommonDialog failDialog;
    //是否升级成功
    private boolean isUpgradeSuccess;
    private CommonDialog successDialog;
    //是否升级过程中
    private boolean isUpgradeProcess;
    //当前设备版本
    private int mCurrentVersion;
    //mqtt是否连接lost
    private boolean isMqttConnectionLost;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftImg().setOnClickListener(this);
        isSS = DeviceTypeUtils.getInstance().isSS1(mDevice.getModel());
        if (!isSS) {
            ivIpc.setImageResource(R.mipmap.ic_no_fs);
        }
        tvDeviceId.setText(mDevice.getDeviceid());
        String str = mDevice.getFirmware().replace(".", "");
        mCurrentVersion = Integer.valueOf(str);
        if (isQueryStatus()) {
            queryIpcUpgradeStatus();
        } else {
            initSetButtonStatus();
        }
    }

    /**
     * 根据版本返回 是否调用查询升级状态命令
     *
     * @return true or false
     */
    private boolean isQueryStatus() {
        if (isSS) {
            return mCurrentVersion >= ssVersion;
        } else {
            return mCurrentVersion >= fsVersion;
        }
    }

    /**
     * //是否需要更新:0-不需要，1-需要
     */
    @UiThread
    void initSetButtonStatus() {
        tvIpcStatus.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
        tvIpcUpgradeTip.setVisibility(View.GONE);
        ipcSettingUpgradeGroup.setVisibility(View.GONE);
        if (mResp.getUpgrade_required() == 1) {
            tvIpcStatus.setText(getString(R.string.ipc_setting_version_find_new, mResp.getLatest_bin_version()));
            btnUpgrade.setVisibility(View.VISIBLE);
        } else {
            tvIpcStatus.setText(String.format("%s\n%s", mResp.getLatest_bin_version(), getString(R.string.ipc_setting_version_no_new)));
            btnUpgrade.setVisibility(View.GONE);
        }
    }

    private void timeoutMqtt() {
        showLoadingDialog();
        mUpgradeStatus = IPC_CONNECT_TIMEOUT;
        startTimerCountDown(IPC_CONNECT_TIMEOUT);
    }

    /**
     * 查询升级状态
     */
    private void queryIpcUpgradeStatus() {
        timeoutMqtt();
        IPCCall.getInstance().ipcQueryUpgradeStatus(this,
                mDevice.getModel(), mDevice.getDeviceid());
    }

    /**
     * 升级中
     */
    private void upgrading() {
        btnUpgrade.setVisibility(View.GONE);
        tvIpcStatus.setVisibility(View.VISIBLE);
        tvIpcUpgradeTip.setVisibility(View.VISIBLE);
        ipcSettingUpgradeGroup.setVisibility(View.GONE);
        timeoutMqtt();
        startTimerCountDown(IPC_DOWNLOAD);
        startTimerCountDown(IPC_UPGRADE_AI);
        startTimerCountDown(IPC_RELAUNCH);
        IPCCall.getInstance().ipcUpgrade(this, mDevice.getModel(),
                mDevice.getDeviceid(), mResp.getUrl(), mResp.getLatest_bin_version());
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isQueryStatus()) {
            super.onBackPressed();
        } else {
            if (isUpgradeProcess) {
                backWarningDialog();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogCat.e(TAG, "1111 onResume");
        //新版本且需要升级
        if (isUpgradeSuccess) {
            upgradeVerSuccessDialog();
        } else {
            if (isQueryStatus() && mResp.getUpgrade_required() == 1 && isMqttConnectionLost) {
                LogCat.e(TAG, "1111 isMqttConnectionLost onResume");
                stopTimeoutTimer();
                isMqttConnectionLost = false;
                queryIpcUpgradeStatus();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeoutTimer();
    }

    /**
     * close 计时器
     */
    private void stopTimeoutTimer() {
        hideLoadingDialog();
        stopTimerCountDown(IPC_CONNECT_TIMEOUT);
        stopTimerCountDown(IPC_DOWNLOAD);
        stopTimerCountDown(IPC_UPGRADE_AI);
        stopTimerCountDown(IPC_RELAUNCH);
    }

    /**
     * 升级
     */
    @Click(resName = "btn_upgrade")
    void upgrade() {
        upgradeStartDialog();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.ipcQueryUpgradeStatus,
                CommonNotifications.netDisconnection,
                IPC_EVENT_OPCODE_STATUS,
                IPC_EVENT_OPCODE_ONLINE};
    }

    /**
     * @param id   id
     * @param args args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.netDisconnection) {
            LogCat.e(TAG, "1111 isMqttConnectionLost didReceivedNotification");
            isMqttConnectionLost = true;
            stopTimeoutTimer();
            dialogUpgradeTip();
            return;
        }
        if (args == null || isUpgradeFail) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == OpcodeConstants.ipcQueryUpgradeStatus) {
            //查询升级状态
            stopTimeoutTimer();
            setLayoutVisible();
            if (TextUtils.equals("1", res.getErrCode())) {
                try {
                    JSONObject object = res.getResult();
                    int status = object.getInt("status");
                    upgradeStatus(status, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == IPC_EVENT_OPCODE_STATUS) {
            //升级命令返回的升级状态 0x4103
            hideLoadingDialog();
            stopTimerCountDown(IPC_CONNECT_TIMEOUT);
            try {
                JSONObject object = res.getResult();
                int status = object.getInt("status");
                upgradeStatus(status, object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == IPC_EVENT_OPCODE_ONLINE) {
            //设备重新上线 0x4200
            try {
                JSONObject object = res.getResult();
                String sn = object.getString("sn");
                if (isUpgradeProcess && TextUtils.equals(sn, mDevice.getDeviceid())) {
                    isUpgradeSuccess = true;
                    isUpgradeProcess = false;
                    stopTimerCountDown(mUpgradeStatus);
                    setText(IPC_RELAUNCH, 100);
                    setLayoutVisible();
                    upgradeVerSuccessDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @UiThread
    void setLayoutVisible() {
        btnUpgrade.setVisibility(View.GONE);
    }

    /**
     * 0: 正在下载固
     * 10:下载失败，可能是固件没有下载下来，也可能是校验错误
     * 1：下载完成，正在升级
     * 2：AI固件升级成功，开始升级主机固件（当下载的固件中有AI固件时才会发送该消息）
     * 11:没有升级事件
     *
     * @param status status
     * @param object object
     */
    @UiThread
    void upgradeStatus(int status, JSONObject object) {
        if (status == 0) {
            mUpgradeStatus = IPC_DOWNLOAD;
            startTimerCountDown(mUpgradeStatus);
        } else if (status == 1) {
            //如果固件中有AI固件，则status为1时，该参数为升级AI需要的时间；为2时，该参数为升级主机固件时间
            //如果固件中没有AI固件，则status为1时，该参数为升级主机固件时间，不发送status为2的消息 + 重启时间
            //ai =0 没有AI固件   ai =1 有AI固件
            stopTimerCountDown(IPC_DOWNLOAD);
            try {
                if (object.has("ai") && object.getInt("ai") == 1) {
                    mUpgradeStatus = IPC_UPGRADE_AI;
                } else {
                    mUpgradeStatus = IPC_RELAUNCH;
                }
                startTimerCountDown(mUpgradeStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (status == 2) {
            //AI固件升级成功，开始升级主机固件（当下载的固件中有AI固件时才会发送该消息）+重启时间
            stopTimerCountDown(IPC_UPGRADE_AI);
            mUpgradeStatus = IPC_RELAUNCH;
            startTimerCountDown(mUpgradeStatus);
        } else if (status == 10) {
            upgradeVerFailDialog();
        } else if (status == 11) {
            //没有升级状态的
            if (isUpgradeFail) {
                dialogUpgradeTip();
            } else {
                initSetButtonStatus();
            }
        }
    }

    /**
     * 开始升级
     */
    private void upgradeStartDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade_title)
                .setMessage(R.string.ipc_setting_dialog_progress_need_time)
                .setConfirmButton(R.string.ipc_setting_dialog_download_install, (dialog, which) -> upgrading())
                .setCancelButton(R.string.sm_cancel).create();
        commonDialog.showWithOutTouchable(false);
        commonDialog.setCancelable(false);
    }

    /**
     * 返回提示
     */
    private void backWarningDialog() {
        CommonDialog successDialog = new CommonDialog.Builder(this)
                .setMessage(getString(R.string.ipc_setting_dialog_upgrade_warning, isSS ? "5" : "8"))
                .setConfirmButton(R.string.str_confirm).create();
        successDialog.showWithOutTouchable(true);
        successDialog.setCancelable(false);
    }

    /**
     * 更新版本成功
     */
    @UiThread
    void upgradeVerSuccessDialog() {
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
        successDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade_success)
                .setMessage(getString(R.string.ipc_setting_dialog_upgrade_success_content))
                .setConfirmButton(R.string.str_confirm, (dialog, which) -> {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }).create();
        successDialog.showWithOutTouchable(false);
        successDialog.setCancelable(false);
    }

    /**
     * 更新版本失败
     */
    private void upgradeVerFailDialog() {
        stopTimeoutTimer();
        isAiUpgrade = false;
        isUpgradeProcess = false;
        isUpgradeFail = false;
        if (failDialog != null && failDialog.isShowing()) {
            failDialog.dismiss();
        }
        failDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade_fail)
                .setMessage(R.string.ipc_setting_upgrade_fail_net_exception)
                .setConfirmButton(R.string.str_retry, (dialog, which) -> upgrading())
                .setCancelButton(R.string.sm_cancel, (dialog, which) -> finish())
                .create();
        failDialog.showWithOutTouchable(false);
        failDialog.setCancelable(false);
    }

    /**
     * 无法获取状态的提示
     */
    @UiThread
    void dialogUpgradeTip() {
        hideLoadingDialog();
        isUpgradeProcess = false;
        ipcSettingUpgradeGroup.setVisibility(View.VISIBLE);
        tvIpcStatus.setVisibility(View.GONE);
        tvIpcUpgradeTip.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
        btnUpgrade.setVisibility(View.GONE);
    }

    private void startTimerCountDown(int status) {
        setProgress = 0;
        if (status == IPC_DOWNLOAD) {
            downloadTimer.start();
        } else if (status == IPC_UPGRADE_AI) {
            upgradeAiTimer.start();
        } else if (status == IPC_RELAUNCH) {
            relaunchTimer.start();
        } else if (status == IPC_CONNECT_TIMEOUT) {
            mqttConnectTimeout.start();
        }
    }

    @UiThread
    void stopTimerCountDown(int status) {
        if (status == IPC_DOWNLOAD) {
            if (downloadTimer != null) {
                downloadTimer.cancel();
            }
        } else if (status == IPC_UPGRADE_AI) {
            if (upgradeAiTimer != null) {
                upgradeAiTimer.cancel();
            }
        } else if (status == IPC_RELAUNCH) {
            if (relaunchTimer != null) {
                relaunchTimer.cancel();
            }
        } else if (status == IPC_CONNECT_TIMEOUT) {
            if (mqttConnectTimeout != null) {
                mqttConnectTimeout.cancel();
            }
        }
    }

    @UiThread
    void showProgress(int status, long l) {
        if (l / 1000 == 1) {
            isUpgradeFail = true;
        }
        if (isFastClick(300)) {
            return;
        }
        if (status == IPC_CONNECT_TIMEOUT) {
            if (l / 1000 == 1) {
                stopTimerCountDown(status);
                dialogUpgradeTip();
            }
            return;
        }
        int downRate;
        if (status == IPC_DOWNLOAD) {
            isUpgradeProcess = true;
            if (showUpgradeFail(l, IPC_DOWNLOAD)) {
                return;
            }
            downRate = (int) (IPC_DOWNLOAD_TIME / PERCENT_DOWNLOAD / 1000 + 1);
            if ((l / 1000) % downRate == 0) {
                setProgress++;
            }
            if (setProgress > PERCENT_DOWNLOAD) {
                return;
            }
            setText(IPC_DOWNLOAD, setProgress);
        } else if (status == IPC_UPGRADE_AI) {
            isAiUpgrade = true;
            isUpgradeProcess = true;
            if (showUpgradeFail(l, IPC_UPGRADE_AI)) {
                return;
            }
            downRate = (int) (IPC_UPGRADE_TIME_AI / PERCENT_UPGRADE / 1000 + 1);
            if ((l / 1000) % downRate == 0) {
                setProgress++;
            }
            int textProgress = PERCENT_DOWNLOAD + setProgress;
            if (textProgress > PERCENT_UPGRADE + PERCENT_DOWNLOAD) {
                return;
            }
            setText(IPC_UPGRADE_AI, textProgress);
        } else if (status == IPC_RELAUNCH) {
            isUpgradeProcess = true;
            if (showUpgradeFail(l, IPC_RELAUNCH)) {
                return;
            }
            if (isAiUpgrade) {
                downRate = (int) (IPC_RELAUNCH_TIME / PERCENT_RELAUNCH / 1000);
            } else {
                downRate = (int) (IPC_RELAUNCH_TIME / (PERCENT_UPGRADE + PERCENT_RELAUNCH) / 1000);
            }
            if ((l / 1000) % downRate == 0) {
                setProgress++;
            }
            int textProgress;
            if (isAiUpgrade) {
                textProgress = PERCENT_DOWNLOAD + PERCENT_UPGRADE + setProgress;
            } else {
                textProgress = PERCENT_DOWNLOAD + setProgress;
            }
            if (textProgress > PERCENT_DOWNLOAD + PERCENT_UPGRADE + PERCENT_RELAUNCH) {
                return;
            }
            LogCat.e(TAG, "1111 textProgress=" + textProgress);
            if (((IPC_RELAUNCH_TIME - l) / 1000) < 55) {
                //写入固件的时间提示40+15s
                setText(IPC_UPGRADE_AI, textProgress);
            } else {
                //重启时间150+15s
                setText(IPC_RELAUNCH, textProgress);
            }
        }
    }

    /*
     *当倒计时完成时，提示更新失败
     */
    private boolean showUpgradeFail(long time, int status) {
        if (time / 1000 == 1) {
            stopTimerCountDown(status);
            upgradeVerFailDialog();
            return true;
        }
        return false;
    }

    @UiThread
    void setText(int status, int progress) {
        String strStatus = "", strStatusTip = "";
        if (status == IPC_DOWNLOAD) {
            strStatus = getString(R.string.ipc_setting_status_downloading, progress);
            strStatusTip = getString(R.string.ipc_setting_status_downloading_tip);
        } else if (status == IPC_UPGRADE_AI) {
            strStatus = getString(R.string.ipc_setting_status_upgrading, progress);
            strStatusTip = getString(R.string.ipc_setting_status_upgrading_tip);
        } else if (status == IPC_RELAUNCH) {
            strStatus = getString(R.string.ipc_setting_status_relaunch, progress);
            strStatusTip = getString(R.string.ipc_setting_status_relaunch_tip);
        }
        mProgress.setVisibility(View.VISIBLE);
        ipcSettingUpgradeGroup.setVisibility(View.GONE);
        mProgress.setProgress(progress);
        tvIpcStatus.setText(strStatus);
        tvIpcUpgradeTip.setText(strStatusTip);
    }

    /**
     * 倒计时函数
     */
    private class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            showProgress(mUpgradeStatus, l);
            LogCat.e(TAG, "onTick= " + l / 1000 + ", setProgress= " + setProgress);
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            LogCat.e(TAG, "onTick  onFinish");
            upgradeVerFailDialog();
        }
    }
}
