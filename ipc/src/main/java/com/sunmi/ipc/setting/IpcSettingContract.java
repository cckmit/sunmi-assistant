package com.sunmi.ipc.setting;

import android.content.Context;

import sunmi.common.base.BaseView;
import sunmi.common.model.SunmiDevice;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public interface IpcSettingContract {
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
