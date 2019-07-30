package com.sunmi.ipc.setting;

import android.content.DialogInterface;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;

/**
 * @author yinhui
 * @date 2019-07-15
 */
@EActivity(resName = "ipc_setting_activity_detail")
public class IpcSettingDetailActivity extends BaseActivity {

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "sil_camera_model")
    SettingItemLayout mCameraModel;
    @ViewById(resName = "sil_camera_sn")
    SettingItemLayout mCameraSn;

    @Extra
    SunmiDevice mDevice;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mCameraModel.setRightText(mDevice.getModel());
        mCameraSn.setRightText(mDevice.getDeviceid());
        mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDevice(mDevice);
            }
        });
    }

    private void deleteDevice(final SunmiDevice device) {
        String msg = getString(R.string.tip_delete_ipc);
        new CommonDialog.Builder(this).setMessage(msg)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_setting_delete, R.color.read_deep_more,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (-1 == NetworkUtils.getNetStatus(IpcSettingDetailActivity.this)) {
                                    unBindNetDisConnected();
                                    return;
                                }
                                unbindIpc(device.getId());
                            }
                        }).create().show();
    }

    private void unBindNetDisConnected() {
        new CommonDialog.Builder(this)
                .setMessage(getString(R.string.str_dialog_net_disconnected))
                .setCancelButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void unbindIpc(int deviceId) {
        IPCCloudApi.unbindIPC(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId,
                new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        shortTip(R.string.str_delete_success);
                        BaseNotification.newInstance().postNotificationName(IpcConstants.refreshIpcList);
                        GotoActivityUtils.gotoMainActivity(IpcSettingDetailActivity.this);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        shortTip(R.string.str_delete_fail);
                    }
                });
    }

}
