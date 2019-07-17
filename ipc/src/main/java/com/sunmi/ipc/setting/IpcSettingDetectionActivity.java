package com.sunmi.ipc.setting;

import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-07-16
 */
@EActivity(resName = "ipc_setting_activity_detection")
public class IpcSettingDetectionActivity extends BaseActivity {

    static final int TYPE_SOUND = 0;
    static final int TYPE_ACTIVE = 1;

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
    boolean mEnable;
    @Extra
    int mSensitivity;

    @AfterViews
    void init() {
        setViewType(mType);
        setViewEnable(mEnable);
        mSensitivitySeekBar.setProgress(mSensitivity);
        mDetectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setViewEnable(isChecked);
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
                setSensitivity(seekBar.getProgress());
            }
        });
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

    private void setViewEnable(boolean enable) {
        mDetectionSwitch.setChecked(enable);
        mSensitivitySeekBar.setEnabled(enable);
        if (enable) {
            mSensitivityTitle.setTextColor(getResources().getColor(R.color.colorText));
            mSensitivityLow.setTextColor(getResources().getColor(R.color.colorText_60));
            mSensitivityMid.setTextColor(getResources().getColor(R.color.colorText_60));
            mSensitivityHigh.setTextColor(getResources().getColor(R.color.colorText_60));
        } else {
            mSensitivityTitle.setTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSensitivityLow.setTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSensitivityMid.setTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSensitivityHigh.setTextColor(getResources().getColor(R.color.color_BBBBC7));
        }
    }

    private void setEnable(boolean enable) {
        // TODO: API
        if (mType == TYPE_SOUND) {

        } else if (mType == TYPE_ACTIVE) {

        }
    }

    private void setSensitivity(int sensitivity) {
        // TODO: API
        if (mType == TYPE_SOUND) {

        } else if (mType == TYPE_ACTIVE) {

        }
    }

}
