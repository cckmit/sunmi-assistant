package com.sunmi.ipc.view.activity.setting;

import android.content.Intent;

import com.google.gson.Gson;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.DetectionConfig;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.view.SettingItemLayout;

import static com.sunmi.ipc.model.DetectionConfig.INTENT_EXTRA_DETECTION_CONFIG;

/**
 * Description:
 * Created by bruce on 2019/12/9.
 */
@EActivity(resName = "activity_setting_md")
public class MDSettingActivity extends BaseActivity {

    private static final int REQUEST_CODE_SOUND_DETECTION = 1001;
    private static final int REQUEST_CODE_ACTIVE_DETECTION = 1002;
    private static final int REQUEST_CODE_DETECTION_TIME = 1003;

    @ViewById(resName = "sil_voice_exception")
    SettingItemLayout mSoundDetection;
    @ViewById(resName = "sil_active_exception")
    SettingItemLayout mActiveDetection;
    @ViewById(resName = "sil_time_setting")
    SettingItemLayout mDetectionTime;

    @Extra
    SunmiDevice mDevice;

    private DetectionConfig mDetectionConfig;

    @AfterViews
    void init() {
        IPCCall.getInstance().getIpcDetection(context, mDevice.getModel(), mDevice.getDeviceid());
    }

    @OnActivityResult(REQUEST_CODE_SOUND_DETECTION)
    void onSoundDetectionResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @OnActivityResult(REQUEST_CODE_ACTIVE_DETECTION)
    void onActiveDetectionResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @OnActivityResult(REQUEST_CODE_DETECTION_TIME)
    void onDetectionTimeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mDetectionConfig = data.getParcelableExtra(INTENT_EXTRA_DETECTION_CONFIG);
            updateDetectionView();
        }
    }

    @Click(resName = "sil_voice_exception")
    void soundAbnormalDetection() {
        if (unclickable()) {
            return;
        }
        IpcSettingDetectionActivity_.intent(context)
                .mType(IpcSettingDetectionActivity.TYPE_SOUND)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_SOUND_DETECTION);
    }

    @Click(resName = "sil_active_exception")
    void activeAbnormalDetection() {
        if (unclickable()) {
            return;
        }
        IpcSettingDetectionActivity_.intent(context)
                .mType(IpcSettingDetectionActivity.TYPE_ACTIVE)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_ACTIVE_DETECTION);
    }

    @Click(resName = "sil_time_setting")
    void detectionTimeSetting() {
        if (unclickable()) {
            return;
        }
        IpcSettingDetectionTimeActivity_.intent(context)
                .mDevice(mDevice)
                .mConfig(mDetectionConfig)
                .startForResult(REQUEST_CODE_DETECTION_TIME);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.getIpcDetection};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args[0] instanceof ResponseBean) {
            ResponseBean res = (ResponseBean) args[0];
            if (id == OpcodeConstants.getIpcDetection) {
                if (res.getDataErrCode() == 1) {
                    mDetectionConfig = new Gson().fromJson(res.getResult().toString(), DetectionConfig.class);
                    updateDetectionView();
                } else {
                    shortTip(R.string.toast_network_Exception);
                }
            }
        }
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

    private boolean unclickable() {
        if (mDetectionConfig == null) {
            return true;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            if (CommonConstants.SUNMI_DEVICE_MAP.containsKey(mDevice.getDeviceid())) {
                return false;
            }
            shortTip(R.string.str_net_exception);
            return true;
        }
        return false;
    }

}
