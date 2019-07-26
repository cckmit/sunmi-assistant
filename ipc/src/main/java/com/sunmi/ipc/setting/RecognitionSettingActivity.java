package com.sunmi.ipc.setting;

import android.view.WindowManager;

import com.sunmi.ipc.view.IpcVideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;

/**
 * @author yinhui
 * @date 2019-07-25
 */
@EActivity(resName = "ipc_setting_recognition_activity")
public class RecognitionSettingActivity extends BaseActivity {

    @Extra
    String mDeviceId;
    @Extra
    String mDeviceModel;
    @Extra
    String mUid;

    @ViewById(resName = "content")
    IpcVideoView video;

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        video.init(mUid, 1, 1);
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }
}
