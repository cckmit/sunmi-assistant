package com.sunmi.ipc.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcSettingContract;
import com.sunmi.ipc.model.IpcConnectApResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.model.IpcNightModeResp;
import com.sunmi.ipc.presenter.IpcSettingPresenter;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.setting.entity.DetectionConfig;
import com.sunmi.ipc.utils.TimeoutTimer;
import com.sunmi.ipc.utils.IpcUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
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
    private static final int REQUEST_VERSION = 1006;
    private final int SWITCH_UNCHECK = 0;
    private final int SWITCH_CHECK = 1;
    private final int WIFI_WIRE_DEFAULT = -1;
    @ViewById(resName = "sil_camera_name")
    SettingItemLayout mNameView;
    @ViewById(resName = "sil_camera_adjust")
    SettingItemLayout mAdjustScreen;
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
    @ViewById(resName = "sil_light")
    SettingItemLayout silLight;
    @ViewById(resName = "sil_wdr")
    SettingItemLayout silWdr;

    @Extra
    SunmiDevice mDevice;
    @Extra
    boolean disableAdjustScreen;
    DetectionConfig mDetectionConfig;

    //夜视模式，指示灯，画面旋转
    private int nightMode, wdrMode, ledIndicator, rotation;
    private boolean isOnClickLight, isSetLight, isOnClickWdr, isSetWdr, isAuthSetWdr;
    private IpcNewFirmwareResp mResp;
    private String wifiSsid, wifiMgmt;
    private int wifiIsWire = WIFI_WIRE_DEFAULT; //-1默认 0无线 1有线
    private boolean isShowWireDialog;//是否显示有线dialog

    private boolean isRun;
    private boolean isClickVersionUpgrade;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        TimeoutTimer.getInstance().start();
        mPresenter = new IpcSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.loadConfig(mDevice);
        mPresenter.currentVersion();
        mVersion.setContent(mDevice.getFirmware());
        mNameView.setContent(mDevice.getName());
        if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
            setWifiUnknown();
        }
        if (!DeviceTypeUtils.getInstance().isFS1(mDevice.getModel()) || disableAdjustScreen) {
            mAdjustScreen.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mSoundDetection.getLayoutParams();
            lp.topMargin = (int) getResources().getDimension(R.dimen.dp_16);
            mSoundDetection.setLayoutParams(lp);
        } else if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
            mAdjustScreen.setEnabled(false);
        }
        silWdr.setOnCheckedChangeListener((buttonView, isChecked) -> setWDR(isChecked));
        silLight.setOnCheckedChangeListener((buttonView, isChecked) -> setSwLight(isChecked));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
        //查询升级状态
        IPCCall.getInstance().ipcQueryUpgradeStatus(context, mDevice.getModel(), mDevice.getDeviceid());
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
    }

    private void timeoutStop() {
        TimeoutTimer.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutStop();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void updateNameView(String name) {
        mDevice.setName(name);
        mNameView.setContent(name);
        BaseNotification.newInstance().postNotificationName(
                IpcConstants.ipcNameChanged, mDevice);
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
        if (upgradeRequired == 1) {
            mVersion.setTagText(R.string.ipc_setting_new);
            mVersion.setContent(mDevice.getFirmware());
            if (!isClickVersionUpgrade) {
                newVersionDialog();
            }
        } else {
            //当前没有升级情况下
            if (TextUtils.isEmpty(mDevice.getFirmware())) {
                mVersion.setContent(resp.getLatest_bin_version());
            } else if (TextUtils.isEmpty(resp.getLatest_bin_version())) {
                mVersion.setContent(mDevice.getFirmware());
            } else {
                if (IpcUtils.getVersionCode(mDevice.getFirmware()) >=
                        IpcUtils.getVersionCode(mResp.getLatest_bin_version())) {
                    mVersion.setContent(mDevice.getFirmware());
                } else {
                    mVersion.setContent(resp.getLatest_bin_version());
                }
            }
            mVersion.setTagText(null);
        }
        if (isClickVersionUpgrade) {
            isClickVersionUpgrade = false;
            gotoIpcSettingVersionActivity();
        }
    }

    @Override
    public void currentVersionFailView() {
        if (isClickVersionUpgrade) {
            isClickVersionUpgrade = false;
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

    /**
     * 获取wifi信息 有线时显示有线  无线时获取无线wifi name
     * 是否远程
     */
    private void getWifiMessage() {
        SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (bean == null) {
            setWifiUnknown();
        } else {
            showLoadingDialog();
            IPCCall.getInstance().getIpcConnectApMsg(context, bean.getIp());
        }
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
                .setInputWatcher(new InputDialog.TextChangeListener() {
                    @Override
                    public void onTextChange(EditText view, Editable s) {
                        if (TextUtils.isEmpty(s.toString())) {
                            return;
                        }
                        String name = s.toString().trim();
                        if (name.getBytes(Charset.defaultCharset()).length > IPC_NAME_MAX_LENGTH) {
                            shortTip(R.string.ipc_setting_tip_name_length);
                            do {
                                LogCat.d(TAG, "name=\"" + name + "\"");
                                name = name.substring(0, name.length() - 1);
                            }
                            while (name.getBytes(Charset.defaultCharset()).length > IPC_NAME_MAX_LENGTH);
                            view.setText(name);
                            view.setSelection(name.length());
                        }
                    }
                })
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_setting_save, (dialog, input) -> {
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
                })
                .create()
                .show();
    }

    @Click(resName = "sil_camera_detail")
    void cameraDetailClick() {
        if (noNetCannotClick(true)) {
            return;
        }
        IpcSettingDetailActivity_.intent(this)
                .mDevice(mDevice)
                .start();
    }

    @Click(resName = "sil_camera_adjust")
    void cameraAdjust() {
        if (disableAdjustScreen) {
            return;
        }
        if (!DeviceTypeUtils.getInstance().isFS1(mDevice.getModel())) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.str_net_exception);
            return;
        }
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device == null) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        showLoadingDialog();
        fsAdjust(mDevice);
    }

    @Click(resName = "sil_voice_exception")
    void soundAbnormalDetection() {
        if (noNetCannotClick(false)) {
            return;
        }
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
        if (noNetCannotClick(false)) {
            return;
        }
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
        if (noNetCannotClick(false)) {
            return;
        }
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
        if (noNetCannotClick(false)) {
            return;
        }
        IpcSettingNightStyleActivity_.intent(this)
                .mDevice(mDevice)
                .nightMode(nightMode)
                .wdrMode(wdrMode)
                .ledIndicator(ledIndicator)
                .rotation(rotation)
                .startForResult(REQUEST_COMPLETE);
    }

    @OnActivityResult(REQUEST_COMPLETE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            nightMode = Objects.requireNonNull(data.getExtras()).getInt("nightMode");
            wdrMode = Objects.requireNonNull(data.getExtras()).getInt("wdrMode");
            mNightStyle.setContent(nightMode(nightMode));
            //夜视模式开启，此时wrd关闭
            if (wdrMode == 0 && silWdr.isChecked()) {
                isAuthSetWdr = true;
                isSetWdr = false;
                silWdr.setChecked(false);
            }
            isCanSetWdr(nightMode);
        }
    }

    @Click(resName = "sil_ipc_version")
    void versionClick() {
        if (noNetCannotClick(true)) {
            return;
        }
        isClickVersionUpgrade = true;
        showLoadingDialog();
        mPresenter.currentVersion();
    }

    private void gotoIpcSettingVersionActivity() {
        IpcSettingVersionActivity_.intent(this)
                .mResp(mResp)
                .mDevice(mDevice)
                .startForResult(REQUEST_VERSION);
    }

    @OnActivityResult(REQUEST_VERSION)
    void onVersionUpgradeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (mResp != null) {
                mDevice.setFirmware(mResp.getLatest_bin_version());
                mVersion.setContent(mResp.getLatest_bin_version());
                mVersion.setTagText(null);
                isClickVersionUpgrade = false;
                mPresenter.currentVersion();
            }
            BaseNotification.newInstance().postNotificationName(CommonNotifications.ipcUpgradeComplete);
        }
    }

    @Click(resName = "sil_wifi")
    void wifiClick() {
        if (noNetCannotClick(true)) {
            return;
        }
        isShowWireDialog = true;
        shortTip(R.string.ipc_setting_tip_network_detection);
        new android.os.Handler().postDelayed(() -> {
            //是否远程
            SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
            if (bean == null) {
                setWifiUnknown();
                shortTip(R.string.ipc_setting_tip_network_dismatch);
                return;
            }
            showLoadingDialog();
            IPCCall.getInstance().getIsWire(context, bean.getIp());
        }, 2000);
    }

    private void gotoIpcSettingWiFiActivity() {
        IpcSettingWiFiActivity_.intent(this)
                .mDevice(mDevice)
                .wifiSsid(wifiSsid)
                .wifiMgmt(wifiMgmt)
                .wifiIsWire(wifiIsWire)
                .startForResult(REQUEST_CODE_WIFI);
    }

    @OnActivityResult(REQUEST_CODE_WIFI)
    void onWiFiResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            wifiSsid = data.getStringExtra("ssid");
            wifiMgmt = data.getStringExtra("mgmt");
            wifiIsWire = data.getIntExtra("isWire", WIFI_WIRE_DEFAULT);
            mWifiName.setContent(wifiSsid);
            if (wifiIsWire == 0) {
                mWifiName.setContent(getString(R.string.ipc_setting_unknown));
                mWifiName.setEnabled(false);
            }
        }
    }

    //指示灯
    private void setSwLight(boolean isChecked) {
        if (noNetCannotClick(false)) {
            silWdr.setChecked(!isChecked);
            return;
        }
        if (isSetLight == isChecked) {
            return;
        }
        isSetLight = isChecked;
        isOnClickLight = true;
        isOnClickWdr = false;
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(),
                mDevice.getDeviceid(), nightMode, wdrMode, isChecked ? SWITCH_CHECK : SWITCH_UNCHECK, rotation);
    }

    /**
     * 夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * 当夜视模式开启不能设置wdr
     */
    @UiThread
    void isCanSetWdr(int nightMode) {
        silWdr.setEnabled(nightMode != 1);
    }

    //宽动态WDR
    private void setWDR(boolean isChecked) {
        if (isAuthSetWdr) {
            isAuthSetWdr = false;
            return;
        }
        if (nightMode == 1) {
            silWdr.setChecked(!isChecked);
            return;
        }
        if (noNetCannotClick(false)) {
            silLight.setChecked(!isChecked);
            return;
        }
        if (isSetWdr == isChecked) {
            return;
        }
        isSetWdr = isChecked;
        isOnClickWdr = true;
        isOnClickLight = false;
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(), mDevice.getDeviceid(),
                nightMode, isChecked ? SWITCH_CHECK : SWITCH_UNCHECK, ledIndicator, rotation);
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
                .wdrMode(wdrMode)
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

    @Click(resName = "sil_sd_manager")
    void sdManagerClick() {
        showLoadingDialog();
        IPCCall.getInstance().getSdStatus(context, mDevice.getModel(), mDevice.getDeviceid());
    }

    /**
     * FS画面调整入口，需要判断版本，新版无需SD卡就绪
     */
    private void fsAdjust(SunmiDevice device) {
        String versionName = device.getFirmware();
        if (IpcUtils.getVersionCode(versionName) < IpcConstants.IPC_VERSION_NO_SDCARD_CHECK) {
            getSdCardStatus(device);
        } else {
            startFsAdjust(device);
        }
    }

    private void getSdCardStatus(SunmiDevice device) {
        IPCCall.getInstance().getSdState(context, device.getModel(), device.getDeviceid());
    }

    private void startFsAdjust(SunmiDevice device) {
        hideLoadingDialog();
        if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(device.getDeviceid())) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        RecognitionSettingActivity_.intent(this)
                .mDevice(device)
                .mVideoRatio(16f / 9f)
                .start();
    }

    /**
     * 重启
     */
    @Click(resName = "sil_ipc_relaunch")
    void relaunchClick() {
        RelaunchSettingActivity_.intent(this)
                .mDevice(mDevice)
                .start();
    }


    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.getIpcConnectApMsg, OpcodeConstants.getIpcNightIdeRotation,
                OpcodeConstants.setIpcNightIdeRotation, OpcodeConstants.getIpcDetection,
                OpcodeConstants.getIsWire, CommonNotifications.netConnected,
                CommonNotifications.netDisconnection, CommonNotifications.ipcUpgrade,
                CommonNotifications.mqttResponseTimeout, OpcodeConstants.ipcQueryUpgradeStatus};
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{OpcodeConstants.getSdStatus, IpcConstants.getSdcardStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (id == CommonNotifications.netDisconnection) { //网络断开
            isShowWireDialog = false;
            setWifiUnknown();
        } else if (id == CommonNotifications.netConnected) { //网络连接
            isShowWireDialog = false;
            connectedNet();
        } else if (id == CommonNotifications.ipcUpgrade) { //ipc升级
            mDevice.setFirmware(mResp.getLatest_bin_version());
            mPresenter.currentVersion();
        } else if (id == CommonNotifications.mqttResponseTimeout) { //连接超时
            if (!isRun) {
                return;
            }
            shortTip(R.string.str_server_exception);
        }
        if (!isRun || args == null || args.length < 1) {
            return;
        }
        if (args[0] instanceof ResponseBean) {
            ResponseBean res = (ResponseBean) args[0];
            if (id == OpcodeConstants.getIpcConnectApMsg) {
                getIpcConnectApMsg(res);
            } else if (id == OpcodeConstants.getIpcNightIdeRotation) {
                timeoutStop();
                getIpcNightIdeRotation(res);
            } else if (id == OpcodeConstants.setIpcNightIdeRotation) {
                setIpcNightIdeRotation(res);
            } else if (id == OpcodeConstants.getIpcDetection) {
                if (res.getDataErrCode() == 1) {
                    mDetectionConfig = new Gson().fromJson(res.getResult().toString(), DetectionConfig.class);
                    updateDetectionView();
                } else {
                    shortTip(R.string.toast_network_Exception);
                }
            } else if (id == OpcodeConstants.getIsWire) {
                checkWire(res);
            } else if (id == OpcodeConstants.getSdStatus) {
                // 进sd卡管理获取SD卡状态状态
                switch (getSdcardStatus(res)) {
                    case 1:
                    case 2:
                        IpcSettingSdcardActivity_.intent(this).mDevice(mDevice).start();
                        break;
                    case 0:
                        showErrorDialog(R.string.tip_no_tf_card, R.string.ipc_recognition_sd_none);
                        break;
                    case 3:
                        showErrorDialog(R.string.tip_unrecognition_tf_card,
                                R.string.ipc_recognition_sd_unknown);
                        break;
                    case 4:
                        showErrorDialog(R.string.tip_unrecognition_tf_card,
                                R.string.tip_tf_card_removed_software);
                        break;
                    default:
                        shortTip(R.string.network_wifi_low);
                        break;
                }
            } else if (IpcConstants.getSdcardStatus == id) {
                // 画面调整前进行SD卡状态获取
                switch (getSdcardStatus(res)) {
                    case 2:
                        startFsAdjust(mDevice);
                        break;
                    case 0:
                        showErrorDialog(R.string.tip_no_tf_card, R.string.ipc_recognition_sd_none);
                        break;
                    case 1:
                        showFormatDialog(mDevice);
                        break;
                    case 3:
                        showErrorDialog(R.string.tip_unrecognition_tf_card,
                                R.string.ipc_recognition_sd_unknown);
                        break;
                    case 4:
                        showErrorDialog(R.string.tip_unrecognition_tf_card,
                                R.string.tip_tf_card_removed_software);
                        break;
                    default:
                        shortTip(R.string.network_wifi_low);
                        break;
                }
            } else if (id == OpcodeConstants.ipcQueryUpgradeStatus) {
                if (res.getDataErrCode() == 1) {
                    try {
                        JSONObject object = res.getResult();
                        int status = object.getInt("status");
                        showDevStatus(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @UiThread
    void showDevStatus(int status) {
        switch (status) {
            case 0:
            case 1:
            case 2:
                mVersion.setTagText(R.string.ipc_setting_upgrading);
                break;
            default:
                break;
        }
    }

    private int getSdcardStatus(ResponseBean res) {
        int status = -1;
        if (res.getDataErrCode() == 1) {
            try {
                status = res.getResult().getInt("sd_status_code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    @UiThread
    public void showErrorDialog(@StringRes int title, @StringRes int msgResId) {
        hideLoadingDialog();
        new CommonDialog.Builder(context)
                .setTitle(title)
                .setMessage(msgResId)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

    @UiThread
    public void showFormatDialog(SunmiDevice device) {
        hideLoadingDialog();
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_sdcard_unformat)
                .setMessage(R.string.msg_sdcard_should_format)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_sd_format, (dialog, which) -> {
                    IpcSettingSdcardActivity_.intent(this).mDevice(device).start();
                }).create().show();
    }

    @UiThread
    void updateDetectionView() {
        if (mDetectionConfig == null) {
            return;
        }
        mSoundDetection.setContent(mDetectionConfig.soundDetection != 0 ?
                getString(R.string.ipc_setting_open) : getString(R.string.ipc_setting_close));
        mActiveDetection.setContent(mDetectionConfig.activeDetection != 0 ?
                getString(R.string.ipc_setting_open) : getString(R.string.ipc_setting_close));
        mDetectionTime.setContent(mDetectionConfig.detectionDays == DetectionConfig.DETECTION_ALL_TIME ?
                getString(R.string.ipc_setting_detection_time_all_time) : getString(R.string.ipc_setting_detection_time_custom));
    }

    @UiThread
    void setWifiUnknown() {
        mWifiName.setContent(getString(R.string.ipc_setting_unknown));
        mWifiName.setEnabled(false);
        mAdjustScreen.setEnabled(false);
    }

    @UiThread
    void showWifiName(String ssid) {
        mWifiName.setContent(ssid);
        mWifiName.setEnabled(true);
    }

    @UiThread
    void connectedNet() {
        new Handler().postDelayed(() -> {
            SunmiDevice bean = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
            if (bean == null) {
                setWifiUnknown();
            } else {
                IPCCall.getInstance().getIsWire(IpcSettingActivity.this, bean.getIp());
                if (CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
                    mWifiName.setEnabled(true);
                    mAdjustScreen.setEnabled(true);
                }
            }
        }, 1200);

    }

    //局域网获取wifi信息
    @UiThread
    void getIpcConnectApMsg(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        IpcConnectApResp device = new GsonBuilder().create().fromJson(
                res.getResult().toString(), IpcConnectApResp.class);
        wifiSsid = device.getWireless().getSsid();
        wifiMgmt = device.getWireless().getKey_mgmt();
        showWifiName(device.getWireless().getSsid());
        if (isShowWireDialog) {
            gotoIpcSettingWiFiActivity();
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
            if (res.getResult() != null && res.getResult().has("wire")) {
                wifiIsWire = res.getResult().getInt("wire");
                if (wifiIsWire == 1) {
                    wifiSsid = getString(R.string.ipc_setting_wire_net);
                    showWifiName(wifiSsid);
                    if (isShowWireDialog) {
                        checkWirelessDialog();
                    }
                } else {
                    getWifiMessage();
                }
            }
        } catch (JSONException e) {
            hideLoadingDialog();
            e.printStackTrace();
        }
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
        //指示灯
        if (isOnClickWdr) {
            if (wdrMode == 0) {
                wdrMode = 1;
            } else {
                wdrMode = 0;
            }
        }
    }

    //请求error
    private void setIpcNightIdeRotationFail() {
        if (isOnClickLight) {
            if (silLight.isChecked()) {
                isSetLight = !silLight.isChecked();
                silLight.setChecked(isSetLight);
            } else {
                isSetLight = silLight.isChecked();
                silLight.setChecked(isSetLight);
            }
        }
        if (isOnClickWdr) {
            if (silWdr.isChecked()) {
                isSetWdr = !silWdr.isChecked();
                silWdr.setChecked(isSetWdr);
            } else {
                isSetWdr = silWdr.isChecked();
                silWdr.setChecked(isSetWdr);
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
        silViewRotate.setContent(getString(R.string.ipc_setting_degree, degree));
    }

    /**
     * led_indicator :   0:关闭/1:开启
     * wdr_mode :   0:关闭/1:开启
     * night_mode :   夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * rotation :   0:关闭/1:开启
     */
    @UiThread
    void getIpcNightIdeRotation(ResponseBean res) {
        if (res.getResult() == null) {
            return;
        }
        IpcNightModeResp resp = new GsonBuilder().create().fromJson(res.getResult().toString(),
                IpcNightModeResp.class);
        nightMode = resp.getNight_mode();
        ledIndicator = resp.getLed_indicator();
        rotation = resp.getRotation();
        wdrMode = resp.getWdr_mode();
        mNightStyle.setContent(nightMode(nightMode));

        isSetLight = ledIndicator != 0;
        silLight.setChecked(isSetLight);
        isSetWdr = wdrMode != 0;
        silWdr.setChecked(isSetWdr);
        rotateDegree(rotation);
        isCanSetWdr(nightMode);
    }

    /**
     * 有新版本
     */
    private void newVersionDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.ipc_setting_dialog_upgrade)
                .setMessage(getString(R.string.ipc_setting_version_current, mDevice.getFirmware()) + "\n" +
                        getString(DeviceTypeUtils.getInstance().isSS1(mDevice.getModel()) ?
                                R.string.ipc_setting_dialog_upgrade_download_time_ss :
                                R.string.ipc_setting_dialog_upgrade_download_time_fs))
                .setConfirmButton(R.string.ipc_setting_dialog_upgrade_ok, (dialog, which) -> {
                    gotoIpcSettingVersionActivity();
                })
                .setCancelButton(R.string.str_in_later).create();
        commonDialog.showWithOutTouchable(false);
    }

    /**
     * 检测wifi是否有线连接
     */
    private void checkWirelessDialog() {
        CommonDialog commonDialog = new CommonDialog.Builder(context)
                .setTitle(getString(R.string.ipc_setting_tip))
                .setMessage(R.string.ipc_setting_check_wireless)
                .setConfirmButton(R.string.str_confirm, R.color.text_main, (dialog, which) -> {
                    dialog.dismiss();
                    gotoIpcSettingWiFiActivity();
                }).setCancelButton(R.string.sm_cancel, R.color.common_orange).create();
        commonDialog.showWithOutTouchable(false);
    }

}
