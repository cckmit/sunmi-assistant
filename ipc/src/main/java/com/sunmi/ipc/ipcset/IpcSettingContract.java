package com.sunmi.ipc.ipcset;

import android.content.Context;

import sunmi.common.base.BaseView;
import sunmi.common.model.SunmiDevice;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public interface IpcSettingContract {

    int ABNORMAL_DETECTION_DISABLE = -1;
    int ABNORMAL_DETECTION_LOW = 0;
    int ABNORMAL_DETECTION_MIDDLE = 1;
    int ABNORMAL_DETECTION_HIGH = 2;

    int NIGHT_VISION_AUTO = 0;
    int NIGHT_VISION_ON = 1;
    int NIGHT_VISION_OFF = 2;

    interface View extends BaseView {

        Context getContext();

        void updateAllView(IpcSettingModel info);

        void updateNameView(String name);

    }

    interface Presenter {

        void loadConfig(SunmiDevice device);

        void updateName(String name);
    }
}
