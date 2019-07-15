package com.sunmi.ipc.ipcset;

import android.content.Context;

import sunmi.common.base.BaseView;

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

        void loadConfig();

        void updateName(String name);
    }
}
