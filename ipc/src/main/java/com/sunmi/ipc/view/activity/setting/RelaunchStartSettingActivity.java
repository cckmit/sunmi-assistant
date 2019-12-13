package com.sunmi.ipc.view.activity.setting;

import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yangShiJie
 * @date 2019-11-15
 */
@EActivity(resName = "ipc_activity_relaunch")
public class RelaunchStartSettingActivity extends BaseActivity {
    @ViewById(resName = "tv_device_id")
    TextView tvDeviceId;
    @ViewById(resName = "iv_ipc")
    ImageView ivIpc;
    @Extra
    SunmiDevice mDevice;

    private CommonDialog successDialog;
    private boolean isSS;
    private boolean isRelaunchSuccess;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        isSS = DeviceTypeUtils.getInstance().isSS1(mDevice.getModel());
        if (!isSS) {
            ivIpc.setImageResource(R.mipmap.ic_no_fs);
        }
        tvDeviceId.setText(mDevice.getDeviceid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRelaunchSuccess) {
            isRelaunchSuccess = false;
            successDialog();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcRelaunchSuccess};
    }

    /**
     * @param id   id
     * @param args args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == IpcConstants.ipcRelaunchSuccess) {
            isRelaunchSuccess = true;
            successDialog();
        }
    }

    /**
     * 成功
     */
    @UiThread
    void successDialog() {
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
        successDialog = new CommonDialog.Builder(this)
                .setTitle(getString(R.string.ipc_setting_online_success))
                .setConfirmButton(R.string.str_confirm, (dialog, which) -> {
                    finish();
                }).create();
        successDialog.showWithOutTouchable(false);
        successDialog.setCancelable(false);
    }
}
