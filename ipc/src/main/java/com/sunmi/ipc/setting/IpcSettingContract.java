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

    interface View extends BaseView {

        Context getContext();

        void updateNameView(String name);

        void currentVersionView(IpcNewFirmwareResp resp);

    }

    interface Presenter {

        void loadConfig(SunmiDevice device);

        void updateName(String name);

        void currentVersion();
    }
}
