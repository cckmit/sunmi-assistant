package com.sunmi.ipc.setting;

import android.widget.TextView;

import com.sunmi.ipc.contract.IpcSettingVersionContract;
import com.sunmi.ipc.presenter.IpcSettingVersionPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;

/**
 * Created by YangShiJie on 2019/7/15.
 */
@EActivity(resName = "ipc_activity_version")
public class IpcSettingVersionActivity extends BaseMvpActivity<IpcSettingVersionPresenter> implements
        IpcSettingVersionContract.View {

    @ViewById(resName = "tv_device_id")
    TextView tvDeviceId;
    @ViewById(resName = "tv_version")
    TextView tvVersion;

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
        int countMinutes = 121;
        if (countdown == countMinutes) {
            stopTimer();
            dialog.progressDismiss();
        } else if (countdown <= 90) {
            dialog.setText(context, countdown);
        } else if (countdown <= 120) {
            if ((countdown - 90) % 3 == 0) {
                endNum++;
                dialog.setText(context, 90 + endNum);
            }
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
        startTimer();//test

        dialog = new UpdateProgressDialog.Builder(this)
                .create();
        dialog.canceledOnTouchOutside(false);
    }

    @Override
    public void getUpgradeSuccess(Object data) {

    }

    @Override
    public void getUpgradeFail(int code, String msg) {

    }

}
