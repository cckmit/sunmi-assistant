package com.sunmi.ipc.contract;

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

        void updateNameView(String name);

        void currentVersionView(IpcNewFirmwareResp resp);

        void currentVersionFailView();

    }

    interface Presenter {

        void loadConfig(Context context, SunmiDevice device);

        void updateName(String name);

        void currentVersion();
    }
}
