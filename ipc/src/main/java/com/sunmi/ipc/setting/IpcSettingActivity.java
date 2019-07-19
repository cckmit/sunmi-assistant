package com.sunmi.ipc.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcConnectApResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.model.IpcNightModeResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.IpcDetectionConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.nio.charset.Charset;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

import static com.sunmi.ipc.setting.entity.IpcDetectionConfig.INTENT_EXTRA_DETECTION_CONFIG;

/**
 * @author YangShiJie
 * @date 2019/7/12
 */
@EActivity(resName = "activity_ipc_setting")
public class IpcSettingActivity extends BaseMvpActivity<IpcSettingPresenter>
        implements IpcSettingContract.View {

    private static final int IPC_NAME_MAX_LENGTH = 36;
    private static final int REQUEST_CODE_SOUND_DETECTION = 1001;
    private static final int REQUEST_CODE_ACTIVE_DETECTION = 1002;
    private static final int REQUEST_CODE_DETECTION_TIME = 1003;
    private static final int REQUEST_COMPLETE = 1000;
    private final int SWITCH_UNCHECK = 0;
    private final int SWITCH_CHECK = 1;

    @Extra
    SunmiDevice mDevice;

    @ViewById(resName = "sil_camera_name")
    SettingItemLayout mNameView;
    @ViewById(resName = "sil_voice_exception")
    SettingItemLayout mSoundDetection;
    @ViewById(resName = "sil_active_exception")
    SettingItemLayout mActiveDetection;
    @ViewById(resName = "sil_time_setting")
    SettingItemLayout mDetectionTime;

    @ViewById(resName = "sil_night_style")
    SettingItemLayout mNightStyle;
    @ViewById(resName = "sil_wifi")
    SettingItemLayout mWifiName;
    @ViewById(resName = "sil_ipc_version")
    SettingItemLayout mVersion;
    @ViewById(resName = "switch_light")
    Switch swLight;
    @ViewById(resName = "switch_view_rotate")
    Switch swRotate;

    IpcDetectionConfig mDetectionConfig;

    //夜视模式，指示灯，画面旋转
    private int nightMode, ledIndicator, rotation;
    private boolean isOnClickLight, isOnClickRotate, isSetLight, isSetRotate;
    private IpcNewFirmwareResp mResp;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new IpcSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.loadConfig(mDevice);
        mPresenter.currentVersion();

        mNameView.setRightText(mDevice.getName());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateNameView(String name) {
        mDevice.setName(name);
        mNameView.setRightText(name);
    }

    /**
     * ipc固件升级
     * upgrade_required是否需要更新，0-不需要，1-需要
     *
     * @param resp
     */
    @Override
    public void currentVersionView(IpcNewFirmwareResp resp) {
        mResp = resp;
        String version = resp.getLatest_bin_version();
        int upgradeRequired = resp.getUpgrade_required();
        String upgradeUrl = resp.getUrl();
        mVersion.setRightText(version);
        if (upgradeRequired == 1) {
            mVersion.setIvToTextLeftImage(R.mipmap.ic_ipc_new_ver);
        }
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

    @Click(resName = "sil_voice_exception")
    void soundAbnormalDetection() {
        if (mDetectionConfig == null) {
            return;
        }
        IpcSettingDetectionActivity_.intent(this)
                .mType(IpcSettingDetectionActivity.TYPE_SOUND)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_SOUND_DETECTION);
    }

    @OnActivityResult(REQUEST_CODE_SOUND_DETECTION)
    void onSoundDetectionResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @Click(resName = "sil_active_exception")
    void activeAbnormalDetection() {
        if (mDetectionConfig == null) {
            return;
        }
        IpcSettingDetectionActivity_.intent(this)
                .mType(IpcSettingDetectionActivity.TYPE_ACTIVE)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_ACTIVE_DETECTION);
    }

    @OnActivityResult(REQUEST_CODE_ACTIVE_DETECTION)
    void onActiveDetectionResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @Click(resName = "sil_time_setting")
    void detectionTimeSetting() {
        if (mDetectionConfig == null) {
            return;
        }
        IpcSettingDetectionTimeActivity_.intent(this)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_DETECTION_TIME);
    }

    @OnActivityResult(REQUEST_CODE_DETECTION_TIME)
    void onDetectionTimeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @Click(resName = "sil_night_style")
    void nightStyleClick() {
        IpcSettingNightStyleActivity_.intent(this)
                .mDevice(mDevice)
                .nightMode(nightMode)
                .ledIndicator(ledIndicator)
                .rotation(rotation)
                .startForResult(REQUEST_COMPLETE);
    }

    @OnActivityResult(REQUEST_COMPLETE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            nightMode = data.getExtras().getInt("nightMode");
            mNightStyle.setRightText(nightMode(nightMode));
        }
    }

    @Click(resName = "sil_ipc_version")
    void versionClick() {
        IpcSettingVersionActivity_.intent(this)
                .mDevice(mDevice)
                .start();
    }

    @Click(resName = "sil_wifi")
    void wifiClick() {
        //是否远程
        if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        IpcSettingWiFiActivity_.intent(this).mDevice(mDevice).start();
    }

    //指示灯
    @CheckedChange(resName = "switch_light")
    void setSwLight(CompoundButton buttonView, boolean isChecked) {
        LogCat.e(TAG, "66666  22");
        if (isSetLight == isChecked) {
            return;
        }
        isSetLight = isChecked;
        isOnClickLight = true;
        isOnClickRotate = false;
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(),
                mDevice.getDeviceid(), nightMode, isChecked ? SWITCH_CHECK : SWITCH_UNCHECK, rotation);
    }

    //画面旋转
    @CheckedChange(resName = "switch_view_rotate")
    void setSwRotate(CompoundButton buttonView, boolean isChecked) {
        if (isSetRotate == isChecked) {
            return;
        }
        isSetRotate = isChecked;
        isOnClickLight = false;
        isOnClickRotate = true;
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(),
                mDevice.getDeviceid(), nightMode, ledIndicator, isChecked ? SWITCH_CHECK : SWITCH_UNCHECK);
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.getIpcConnectApMsg, IpcConstants.getIpcNightIdeRotation,
                IpcConstants.setIpcNightIdeRotation, IpcConstants.getIpcDetection};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.getIpcConnectApMsg) {
            LogCat.e(TAG, "1111  11=" + res.getResult());
            getIpcConnectApMsg(res);
        } else if (id == IpcConstants.getIpcNightIdeRotation) {
            LogCat.e(TAG, "1111 22=" + res.getResult());
            getIpcNightIdeRotation(res);
        } else if (id == IpcConstants.setIpcNightIdeRotation) {
            LogCat.e(TAG, "1111 33=" + res.getResult());
            setIpcNightIdeRotation(res);
        } else if (id == IpcConstants.getIpcDetection) {
            if (TextUtils.equals("1", res.getErrCode())) {
                mDetectionConfig = new Gson().fromJson(res.getResult().toString(), IpcDetectionConfig.class);
                updateDetectionView();
            } else {
                shortTip(R.string.toast_network_Exception);
            }

        }
    }

    private void updateDetectionView() {
        if (mDetectionConfig == null) {
            return;
        }
        mSoundDetection.setRightText(mDetectionConfig.soundDetection != 0 ?
                getString(R.string.ipc_setting_open) : getString(R.string.ipc_setting_close));
        mActiveDetection.setRightText(mDetectionConfig.activeDetection != 0 ?
                getString(R.string.ipc_setting_open) : getString(R.string.ipc_setting_close));
        mDetectionTime.setRightText(mDetectionConfig.detectionDays == IpcDetectionConfig.DETECTION_ALL_TIME ?
                getString(R.string.ipc_setting_detection_time_all_time) : getString(R.string.ipc_setting_detection_time_custom));
    }

    //局域网获取wifi信息
    @UiThread
    void getIpcConnectApMsg(ResponseBean res) {
        if (TextUtils.isEmpty(res.getResult().toString())) {
            return;
        }
        IpcConnectApResp device = new GsonBuilder().create().fromJson(res.getResult().toString(), IpcConnectApResp.class);
        mWifiName.setRightText(device.getWireless().getSsid());
    }

    //夜视模式
    private String nightMode(int mode) {
        if (mode == 0) {
            return getString(R.string.ipc_setting_night_vision_mode_off);
        } else if (mode == 1) {
            return getString(R.string.ipc_setting_night_vision_mode_on);
        } else if (mode == 2) {
            return getString(R.string.ipc_setting_night_vision_mode_auto);
        }
        return "";
    }

    private void setIpcNightIdeRotationSuccess() {
        //指示灯
        if (isOnClickLight && !isOnClickRotate) {
            if (ledIndicator == 0) {
                ledIndicator = 1;
            } else {
                ledIndicator = 0;
            }
        }
        //画面
        if (!isOnClickLight && isOnClickRotate) {
            if (rotation == 0) {
                rotation = 1;
            } else {
                rotation = 0;
            }
        }
    }

    //请求error
    private void setIpcNightIdeRotationFail() {
        if (isOnClickLight && !isOnClickRotate) {
            if (swLight.isChecked()) {
                isSetLight = !swLight.isChecked();
                swLight.setChecked(isSetLight);
            } else {
                isSetLight = swLight.isChecked();
                swLight.setChecked(isSetLight);
            }
        }
        if (!isOnClickLight && isOnClickRotate) {
            if (swRotate.isChecked()) {
                isSetRotate = !swRotate.isChecked();
                swRotate.setChecked(isSetRotate);
            } else {
                isSetRotate = swRotate.isChecked();
                swRotate.setChecked(isSetRotate);
            }
        }
    }

    //led_indicator   rotation设置结果
    @UiThread
    void setIpcNightIdeRotation(ResponseBean res) {
        if (TextUtils.isEmpty(res.getResult().toString())) {
            return;
        }
        if (TextUtils.equals("1", res.getErrCode())) {
            shortTip(R.string.tip_set_complete);
            setIpcNightIdeRotationSuccess();
        } else {
            shortTip(R.string.tip_set_fail);
            setIpcNightIdeRotationFail();
        }
    }

    /**
     * led_indicator :   0:关闭/1:开启
     * night_mode :   夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * rotation :   0:关闭/1:开启
     */
    @UiThread
    void getIpcNightIdeRotation(ResponseBean res) {
        if (TextUtils.isEmpty(res.getResult().toString())) {
            return;
        }
        IpcNightModeResp resp = new GsonBuilder().create().fromJson(res.getResult().toString(), IpcNightModeResp.class);
        nightMode = resp.getNight_mode();
        ledIndicator = resp.getLed_indicator();
        rotation = resp.getRotation();
        mNightStyle.setRightText(nightMode(nightMode));

        isSetLight = ledIndicator != 0;
        swLight.setChecked(isSetLight);
        isSetRotate = rotation != 0;
        swRotate.setChecked(isSetRotate);
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
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
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
