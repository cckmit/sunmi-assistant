package com.sunmi.ipc.setting;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
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

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

import static sunmi.common.constant.CommonConstants.FS_UPGRADE_TIME;
import static sunmi.common.constant.CommonConstants.SS_UPGRADE_TIME;

/**
 * Created by YangShiJie on 2019/7/15.
 */
@EActivity(resName = "ipc_activity_version")
public class IpcSettingVersionActivity extends BaseActivity {

    @ViewById(resName = "tv_device_id")
    TextView tvDeviceId;
    @ViewById(resName = "tv_version")
    TextView tvVersion;
    @ViewById(resName = "btn_upgrade")
    Button btnUpgrade;

    @Extra
    SunmiDevice mDevice;
    @Extra
    IpcNewFirmwareResp mResp;

    private UpdateProgressDialog dialog;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private int countdown, endNum;

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
        countdown++;
        int countMinutes = DeviceTypeUtils.getInstance().isSS1(mDevice.getModel()) ? SS_UPGRADE_TIME : FS_UPGRADE_TIME;
        if (countdown == countMinutes) {
            stopTimer();
            dialog.progressDismiss();
            upgradeVerFailDialog(mResp.getLatest_bin_version());
        } else if (countdown <= 90) {
            dialog.setText(context, countdown);
        } else {
            if (DeviceTypeUtils.getInstance().isSS1(mDevice.getModel()) && countdown <= SS_UPGRADE_TIME) {
                if ((countdown - 90) % 6 == 0) {
                    endNum++;
                }
            } else if (DeviceTypeUtils.getInstance().isFS1(mDevice.getModel()) && countdown <= FS_UPGRADE_TIME) {
                if ((countdown - 90) % 37 == 0) {
                    endNum++;
                }
            }
            dialog.setText(context, 90 + endNum);
        }
    }

    // 停止计时
    private void stopTimer() {
        countdown = 0;
        endNum = 0;
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
        tvDeviceId.setText(mDevice.getDeviceid());
        //upgrade_required是否需要更新，0-不需要，1-需要
        if (mResp.getUpgrade_required() == 1) {
            tvVersion.setText(getString(R.string.ipc_setting_version_find_new, mResp.getLatest_bin_version()));
            btnUpgrade.setVisibility(View.VISIBLE);
        } else {
            tvVersion.setText(String.format("%s\n%s", mDevice.getFirmware(), getString(R.string.ipc_setting_version_no_new)));
            btnUpgrade.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     * 升级
     */
    @Click(resName = "btn_upgrade")
    void upgrade() {
        upgrading();
    }


    /**
     * 升级中
     */
    private void upgrading() {
        IPCCall.getInstance().ipcUpgrade(this, mDevice.getModel(),
                mDevice.getDeviceid(), mResp.getUrl(), mResp.getLatest_bin_version());
        dialog = new UpdateProgressDialog.Builder(this).create();
        dialog.canceledOnTouchOutside(false);
        startTimer();
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.ipcUpgrade};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.ipcUpgrade) {
            upgradeResult(res);
        }
    }

    @UiThread
    void upgradeResult(ResponseBean res) {
        if (res.getDataErrCode() == 1) {
            stopTimer();
            dialog.progressDismiss();
            tvVersion.setText(String.format("%s\n%s", mResp.getLatest_bin_version(), getString(R.string.ipc_setting_version_no_new)));
        } else {
            upgradeVerFailDialog(mResp.getLatest_bin_version());
        }
    }

    /**
     * 更新版本失败
     *
     * @param version
     */
    private void upgradeVerFailDialog(String version) {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade_fail)
                .setMessage(getString(R.string.ipc_setting_dialog_upgrade_fail_content, version))
                .setConfirmButton(R.string.str_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upgrading();
                    }
                })
                .setCancelButton(R.string.sm_cancel).create();
        commonDialog.showWithOutTouchable(false);
    }

}
