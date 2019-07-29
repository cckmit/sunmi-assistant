package com.sunmi.ipc.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcConnectApResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.model.IpcNightModeResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.DetectionConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;

import static com.sunmi.ipc.setting.entity.DetectionConfig.INTENT_EXTRA_DETECTION_CONFIG;

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
    private static final int REQUEST_CODE_WIFI = 1004;
    private static final int REQUEST_CODE_ROTATE = 1005;
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
    @ViewById(resName = "sil_view_rotate")
    SettingItemLayout silViewRotate;
    @ViewById(resName = "sil_wifi")
    SettingItemLayout mWifiName;
    @ViewById(resName = "sil_ipc_version")
    SettingItemLayout mVersion;
    @ViewById(resName = "switch_light")
    Switch swLight;

    DetectionConfig mDetectionConfig;

    //夜视模式，指示灯，画面旋转
    private int nightMode, ledIndicator, rotation;
    private boolean isOnClickLight, isSetLight;
    private IpcNewFirmwareResp mResp;
    private String wifiSsid, wifiMgmt;

    // 升级
    private UpdateProgressDialog progressDialog;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private int countdown, endNum;
    private boolean isRun;

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
        int countMinutes = 151;
        if (countdown == countMinutes) {
            stopTimer();
            progressDialog.progressDismiss();
            upgradeVerFailDialog(mResp.getLatest_bin_version());
        } else if (countdown <= 90) {
            progressDialog.setText(context, countdown);
        } else if (countdown <= 150) {
            if ((countdown - 90) % 6 == 0) {
                endNum++;
                progressDialog.setText(context, 90 + endNum);
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
        mPresenter = new IpcSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.loadConfig(mDevice);
        mPresenter.currentVersion();
        mNameView.setRightText(mDevice.getName());
        TextView tvName = mNameView.getRightText();
        tvName.setSingleLine();
        tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
        SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (bean == null) {
            mWifiName.setRightText(getString(R.string.ipc_setting_unknown));
            mWifiName.setLeftTextColor(ContextCompat.getColor(this, R.color.colorText_40));
            mWifiName.setRightTextColor(ContextCompat.getColor(this, R.color.colorText_40));
        } else {
            IPCCall.getInstance().getIpcConnectApMsg(this, bean.getIp());
            mWifiName.setLeftTextColor(ContextCompat.getColor(this, R.color.colorText));
            mWifiName.setRightTextColor(ContextCompat.getColor(this, R.color.colorText_60));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
    }

    @Override
    public void onBackPressed() {
        if (countdown == 0) {
            super.onBackPressed();
        }
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
        int upgradeRequired = resp.getUpgrade_required();
        mVersion.setRightText(mDevice.getFirmware());
        if (upgradeRequired == 1) {
            mVersion.setIvToTextLeftImage(R.mipmap.ic_ipc_new_ver);
            newVersionDialog();
        }
    }

    private boolean noNetCannotClick(boolean isOnlyHttp) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            if (isOnlyHttp) {
                shortTip(R.string.str_net_exception);
                return true;
            }
            if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
                shortTip(R.string.str_net_exception);
                return true;
            }
        }
        return false;
    }

    @Click(resName = "btn_refresh")
    void btnRefreshClick() {
        mPresenter.loadConfig(mDevice);
        mPresenter.currentVersion();
    }

    @Click(resName = "sil_camera_name")
    void cameraNameClick() {
        if (noNetCannotClick(true)) {
            return;
        }
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
                        if (input.trim().length() == 0) {
                            shortTip(R.string.ipc_setting_tip_name_empty);
                            return;
                        }
                        showLoadingDialog();
                        mPresenter.updateName(input);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Click(resName = "sil_camera_detail")
    void cameraDetailClick() {
        if (noNetCannotClick(true)) return;
        IpcSettingDetailActivity_.intent(this)
                .mDevice(mDevice)
                .start();
    }

    @Click(resName = "sil_voice_exception")
    void soundAbnormalDetection() {
        if (noNetCannotClick(false)) return;
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
        if (noNetCannotClick(false)) return;
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
        if (noNetCannotClick(false)) return;
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
        if (noNetCannotClick(false)) return;
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
            nightMode = Objects.requireNonNull(data.getExtras()).getInt("nightMode");
            mNightStyle.setRightText(nightMode(nightMode));
        }
    }

    @Click(resName = "sil_ipc_version")
    void versionClick() {
        if (noNetCannotClick(true)) return;
        if (mResp == null) {
            mPresenter.currentVersion();
            return;
        }
        IpcSettingVersionActivity_.intent(this)
                .mResp(mResp)
                .mDevice(mDevice)
                .start();
    }

    @Click(resName = "sil_wifi")
    void wifiClick() {
        //是否远程
        SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (bean == null) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        showLoadingDialog();
        IPCCall.getInstance().getIsWire(context, bean.getIp());
    }

    private void gotoIpcSettingWiFiActivity() {
        IpcSettingWiFiActivity_.intent(this)
                .mDevice(mDevice)
                .wifiSsid(wifiSsid)
                .wifiMgmt(wifiMgmt)
                .startForResult(REQUEST_CODE_WIFI);
    }

    @OnActivityResult(REQUEST_CODE_WIFI)
    void onWiFiResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            wifiSsid = data.getStringExtra("ssid");
            wifiMgmt = data.getStringExtra("mgmt");
            mWifiName.setRightText(wifiSsid);
        }
    }

    //指示灯
    @CheckedChange(resName = "switch_light")
    void setSwLight(CompoundButton buttonView, boolean isChecked) {
        if (noNetCannotClick(false)) {
            swLight.setChecked(!isChecked);
            return;
        }
        if (isSetLight == isChecked) {
            return;
        }
        isSetLight = isChecked;
        isOnClickLight = true;
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(),
                mDevice.getDeviceid(), nightMode, isChecked ? SWITCH_CHECK : SWITCH_UNCHECK, rotation);
    }

    //画面旋转
    @Click(resName = "sil_view_rotate")
    void rotateClick() {
        if (noNetCannotClick(false)) {
            return;
        }
        IpcSettingRotateActivity_.intent(this)
                .mDevice(mDevice)
                .nightMode(nightMode)
                .ledIndicator(ledIndicator)
                .rotation(rotation)
                .startForResult(REQUEST_CODE_ROTATE);
    }

    @OnActivityResult(REQUEST_CODE_ROTATE)
    void onRotateResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            rotation = data.getIntExtra("rotate", 0);
            rotateDegree(rotation);
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.getIpcConnectApMsg, IpcConstants.getIpcNightIdeRotation,
                IpcConstants.setIpcNightIdeRotation, IpcConstants.getIpcDetection,
                IpcConstants.ipcUpgrade, IpcConstants.getIsWire};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (!isRun || args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.getIpcConnectApMsg) {
            getIpcConnectApMsg(res);
        } else if (id == IpcConstants.getIpcNightIdeRotation) {
            getIpcNightIdeRotation(res);
        } else if (id == IpcConstants.setIpcNightIdeRotation) {
            setIpcNightIdeRotation(res);
        } else if (id == IpcConstants.getIpcDetection) {
            if (res.getDataErrCode() == 1) {
                mDetectionConfig = new Gson().fromJson(res.getResult().toString(), DetectionConfig.class);
                updateDetectionView();
            } else {
                shortTip(R.string.toast_network_Exception);
            }
        } else if (id == IpcConstants.ipcUpgrade) {
            upgradeResult(res);
        } else if (id == IpcConstants.getIsWire) {
            checkWire(res);
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
        mDetectionTime.setRightText(mDetectionConfig.detectionDays == DetectionConfig.DETECTION_ALL_TIME ?
                getString(R.string.ipc_setting_detection_time_all_time) : getString(R.string.ipc_setting_detection_time_custom));
    }

    //局域网获取wifi信息
    @UiThread
    void getIpcConnectApMsg(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        IpcConnectApResp device = new GsonBuilder().create().fromJson(res.getResult().toString(), IpcConnectApResp.class);
        wifiSsid = device.getWireless().getSsid();
        wifiMgmt = device.getWireless().getKey_mgmt();
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
        if (isOnClickLight) {
            if (ledIndicator == 0) {
                ledIndicator = 1;
            } else {
                ledIndicator = 0;
            }
        }
    }

    //请求error
    private void setIpcNightIdeRotationFail() {
        if (isOnClickLight) {
            if (swLight.isChecked()) {
                isSetLight = !swLight.isChecked();
                swLight.setChecked(isSetLight);
            } else {
                isSetLight = swLight.isChecked();
                swLight.setChecked(isSetLight);
            }
        }
    }

    //led_indicator   rotation设置结果
    @UiThread
    void setIpcNightIdeRotation(ResponseBean res) {
        if (res.getDataErrCode() == 1) {
            shortTip(R.string.tip_set_complete);
            setIpcNightIdeRotationSuccess();
        } else {
            shortTip(R.string.tip_set_fail);
            setIpcNightIdeRotationFail();
        }
    }

    private void rotateDegree(int rotation) {
        String degree = "";
        if (DeviceTypeUtils.getInstance().isSS1(mDevice.getModel())) {
            if (rotation == 0) {
                degree = "0";
            } else if (rotation == 1) {
                degree = "90";
            } else if (rotation == 2) {
                degree = "180";
            } else if (rotation == 3) {
                degree = "270";
            }
        } else if (DeviceTypeUtils.getInstance().isFS1(mDevice.getModel())) {
            if (rotation == 0) {
                degree = "0";
            } else if (rotation == 1) {
                degree = "180";
            }
        }
        silViewRotate.setRightText(getString(R.string.ipc_setting_degree, degree));
    }

    /**
     * led_indicator :   0:关闭/1:开启
     * night_mode :   夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * rotation :   0:关闭/1:开启
     */
    @UiThread
    void getIpcNightIdeRotation(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        IpcNightModeResp resp = new GsonBuilder().create().fromJson(res.getResult().toString(), IpcNightModeResp.class);
        nightMode = resp.getNight_mode();
        ledIndicator = resp.getLed_indicator();
        rotation = resp.getRotation();
        mNightStyle.setRightText(nightMode(nightMode));

        isSetLight = ledIndicator != 0;
        swLight.setChecked(isSetLight);
        rotateDegree(rotation);
    }

    @UiThread
    void upgradeResult(ResponseBean res) {
        if (res.getDataErrCode() == 1) {
            stopTimer();
            progressDialog.progressDismiss();
            upgradeVerSuccessDialog();
        } else {
            upgradeVerFailDialog(mResp.getLatest_bin_version());
        }
    }

    /**
     * wire	int	有线口物理连接状态 0:未连接, 1:已连接
     * wireless	int	无线配置状态 0:未配置过无线连接, 1:已配置过无线（之前无线连接至少成功过一次）
     * online	int	网络连接状态 0:不能访问internet, 1:可以访问internet
     */
    @UiThread
    void checkWire(ResponseBean res) {
        try {
            if (res.getResult() != null && res.getResult().has("wire")
                    && res.getResult().getInt("wire") == 1) {
                wifiSsid = getString(R.string.ipc_setting_wire_net);
                mWifiName.setRightText(wifiSsid);
                checkWirelessDialog();
            } else {
                gotoIpcSettingWiFiActivity();
            }
        } catch (JSONException e) {
            hideLoadingDialog();
            e.printStackTrace();
        }
    }

    /**
     * 有新版本
     */
    private void newVersionDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade)
                .setMessage(getString(R.string.ipc_setting_version_current, mDevice.getFirmware()) + "\n" +
                        getString(R.string.ipc_setting_dialog_upgrade_download_time))
                .setConfirmButton(R.string.ipc_setting_dialog_upgrade_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        upgrading();
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
                        upgrading();
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
                        dialog.dismiss();
                    }
                }).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 升级中
     */
    private void upgrading() {
        IPCCall.getInstance().ipcUpgrade(IpcSettingActivity.this, mDevice.getModel(),
                mDevice.getDeviceid(), mResp.getUrl(), mResp.getLatest_bin_version());
        progressDialog = new UpdateProgressDialog.Builder(this)
                .create();
        progressDialog.canceledOnTouchOutside(false);
        startTimer();
    }


    /**
     * 检测wifi是否有线连接
     */
    private void checkWirelessDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(getString(R.string.ipc_setting_tip))
                .setMessage(getString(R.string.ipc_setting_check_wireless))
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoIpcSettingWiFiActivity();
                    }
                }).setCancelButton(R.string.sm_cancel).create();
        commonDialog.showWithOutTouchable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
