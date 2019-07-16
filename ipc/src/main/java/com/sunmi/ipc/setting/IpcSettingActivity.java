package com.sunmi.ipc.setting;

import android.content.Context;
import android.content.DialogInterface;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.nio.charset.Charset;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

/**
 * @author YangShiJie
 * @date 2019/7/12
 */
@EActivity(resName = "activity_ipc_setting")
public class IpcSettingActivity extends BaseMvpActivity<IpcSettingPresenter>
        implements IpcSettingContract.View {

    private static final int IPC_NAME_MAX_LENGTH = 36;

    @Extra
    SunmiDevice mDevice;

    @ViewById(resName = "sil_camera_name")
    SettingItemLayout mNameView;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new IpcSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.loadConfig(mDevice);

        mNameView.setRightText(mDevice.getName());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateAllView(IpcSettingModel info) {
        // TODO: 更新所有View或者显示网络异常
    }

    @Override
    public void updateNameView(String name) {
        mDevice.setName(name);
        mNameView.setRightText(name);
    }

    @Click(resName = "sil_camera_name")
    void cameraNameClick() {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_setting_name)
                .setInitInputContent(mDevice.getName())
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_setting_save, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        if (input.trim().getBytes(Charset.defaultCharset()).length > IPC_NAME_MAX_LENGTH) {
                            shortTip(R.string.ipc_setting_tip_name_length);
                            return;
                        }
                         mPresenter.updateName(input);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Click(resName = "sil_camera_detail")
    void cameraDetailClick() {
        IpcSettingDetailActivity_.intent(this)
                .mDevice(mDevice)
                .start();
    }
    @Click(resName = "sil_night_style")
    void nightStyleClick() {
        IpcSettingNightStyleActivity_.intent(this).start();
    }

    @Click(resName = "sil_ipc_version")
    void versionClick() {
        IpcSettingVersionActivity_.intent(this).start();
    }

    @Click(resName = "sil_wifi")
    void wifiClick() {
        IpcSettingWiFiActivity_.intent(this).start();
    }

    /**
     * 有新版本
     *
     * @param version
     */
    private void newVersionDialog(String version) {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade)
                .setMessage(getString(R.string.ipc_setting_version_current, version) + "\n" +
                        getString(R.string.ipc_setting_dialog_upgrade_download_time))
                .setConfirmButton(R.string.ipc_setting_dialog_upgrade_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelButton(R.string.ipc_setting_dialog_upgrade_cancel).create();
        commonDialog.showWithOutTouchable(false);
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

                    }
                })
                .setCancelButton(R.string.sm_cancel).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 更新版本成功
     */
    private void upgradeVerSuccessDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade_fail)
                .setMessage(getString(R.string.ipc_setting_dialog_upgrade_success_content))
                .setConfirmButton(R.string.str_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        commonDialog.showWithOutTouchable(false);
    }

    private UpdateProgressDialog dialog;

    /**
     * 升级中
     */
    private void upgrading() {
        dialog = new UpdateProgressDialog.Builder(this)
                .create();
        dialog.canceledOnTouchOutside(true);
    }

}
