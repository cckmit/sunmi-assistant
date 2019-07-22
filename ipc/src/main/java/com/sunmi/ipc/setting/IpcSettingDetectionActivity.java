package com.sunmi.ipc.setting;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.DetectionConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.TitleBarView;

import static com.sunmi.ipc.setting.entity.DetectionConfig.INTENT_EXTRA_DETECTION_CONFIG;

/**
 * @author yinhui
 * @date 2019-07-16
 */
@EActivity(resName = "ipc_setting_activity_detection")
public class IpcSettingDetectionActivity extends BaseActivity {

    static final int TYPE_SOUND = 0;
    static final int TYPE_ACTIVE = 1;

    private static final int DEFAULT_SENSITIVITY = 1;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;

    @ViewById(resName = "tv_ipc_setting_switch_title")
    TextView mDetectionTitle;
    @ViewById(resName = "switch_ipc_setting_detection")
    Switch mDetectionSwitch;
    @ViewById(resName = "tv_ipc_setting_sensitivity_title")
    TextView mSensitivityTitle;
    @ViewById(resName = "sb_ipc_setting_sensitivity")
    SeekBar mSensitivitySeekBar;
    @ViewById(resName = "tv_ipc_setting_sensitivity_low")
    TextView mSensitivityLow;
    @ViewById(resName = "tv_ipc_setting_sensitivity_mid")
    TextView mSensitivityMid;
    @ViewById(resName = "tv_ipc_setting_sensitivity_high")
    TextView mSensitivityHigh;

    @Extra
    int mType;
    @Extra
    SunmiDevice mDevice;
    @Extra
    DetectionConfig mConfig;

    private boolean mEnable;
    private int mSensitivity;

    @AfterViews
    void init() {
        initData(mType, mConfig);
        setViewType(mType);
        updateView(mEnable, mSensitivity);
        mDetectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEnable = isChecked;
                updateView(isChecked, mSensitivity);
                setEnable(isChecked);
            }
        });
        mSensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int sensitivity = seekBar.getProgress();
                if (mSensitivity != sensitivity) {
                    mSensitivity = sensitivity;
                    setSensitivity(sensitivity);
                }
            }
        });
    }

    private void initData(int type, DetectionConfig config) {
        if (type == TYPE_SOUND) {
            mEnable = config.soundDetection != 0;
            mSensitivity = mEnable ? config.soundDetection - 1 : DEFAULT_SENSITIVITY;
        } else if (type == TYPE_ACTIVE) {
            mEnable = config.activeDetection != 0;
            mSensitivity = mEnable ? config.activeDetection - 1 : DEFAULT_SENSITIVITY;
        }
    }

    private void setViewType(int type) {
        if (type == TYPE_SOUND) {
            mTitleBar.setAppTitle(R.string.ipc_setting_sound_abnormal_detection);
            mDetectionTitle.setText(R.string.ipc_setting_sound_abnormal_detection);
        } else if (type == TYPE_ACTIVE) {
            mTitleBar.setAppTitle(R.string.ipc_setting_active_abnormal_detection);
            mDetectionTitle.setText(R.string.ipc_setting_active_abnormal_detection);
        }
    }

    private void updateView(boolean enable, int sensitivity) {
        mDetectionSwitch.setChecked(enable);
        mSensitivitySeekBar.setEnabled(enable);
        mSensitivitySeekBar.setProgress(sensitivity);
        if (enable) {
            mSensitivityTitle.setTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSensitivityLow.setTextColor(ContextCompat.getColor(this, R.color.colorText_60));
            mSensitivityMid.setTextColor(ContextCompat.getColor(this, R.color.colorText_60));
            mSensitivityHigh.setTextColor(ContextCompat.getColor(this, R.color.colorText_60));
        } else {
            mSensitivityTitle.setTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSensitivityLow.setTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSensitivityMid.setTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSensitivityHigh.setTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
        }
    }

    private void setEnable(boolean enable) {
        updateView(enable, mSensitivity);
        if (mType == TYPE_SOUND) {
            mConfig.soundDetection = enable ? mSensitivity + 1 : 0;
        } else if (mType == TYPE_ACTIVE) {
            mConfig.activeDetection = enable ? mSensitivity + 1 : 0;
        }
        postSetConfig();
    }

    private void setSensitivity(int sensitivity) {
        updateView(mEnable, sensitivity);
        if (mType == TYPE_SOUND) {
            mConfig.soundDetection = sensitivity + 1;
        } else if (mType == TYPE_ACTIVE) {
            mConfig.activeDetection = sensitivity + 1;
        }
        postSetConfig();
    }

    private void postSetConfig() {
        showLoadingDialog();
        new IPCCall().setIpcDetection(this, mDevice.getModel(), mDevice.getDeviceid(),
                mConfig.activeDetection, mConfig.soundDetection, mConfig.detectionDays,
                mConfig.detectionTimeStart, mConfig.detectionTimeEnd);
        Intent intent = getIntent();
        intent.putExtra(INTENT_EXTRA_DETECTION_CONFIG, mConfig);
        setResult(RESULT_OK, intent);
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.setIpcDetection};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.setIpcDetection) {
            hideLoadingDialog();
            if (res.getDataErrCode() == 1) {
                shortTip(R.string.tip_set_complete);
            } else {
                shortTip(R.string.tip_set_fail);
            }
        }
    }

}
