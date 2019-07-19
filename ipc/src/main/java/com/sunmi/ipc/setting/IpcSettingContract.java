package com.sunmi.ipc.setting;

import android.content.Context;

import com.sunmi.ipc.model.IpcNewFirmwareResp;

import sunmi.common.base.BaseView;
import sunmi.common.model.SunmiDevice;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public interface IpcSettingContract {

    int ABNORMAL_DETECTION_DISABLE = 0;
    int ABNORMAL_DETECTION_LOW = 1;
    int ABNORMAL_DETECTION_MIDDLE = 2;
    int ABNORMAL_DETECTION_HIGH = 3;

    int NIGHT_VISION_AUTO = 0;
    int NIGHT_VISION_ON = 1;
    int NIGHT_VISION_OFF = 2;

    interface View extends BaseView {

        Context getContext();

        void updateAllView(IpcSettingModel info);

        void updateNameView(String name);

        void currentVersionView(IpcNewFirmwareResp resp);

    }

    interface Presenter {

        void loadConfig(SunmiDevice device);

        void updateName(String name);

        void currentVersion();
    }
}
