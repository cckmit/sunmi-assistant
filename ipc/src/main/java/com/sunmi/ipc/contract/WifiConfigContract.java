package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public interface WifiConfigContract {
    interface Model {
    }

    interface View extends BaseView {
        void getWifiListSuccess();

        void getWifiListFail();

        void connectWifiSuccess();

        void connectWifiFail();
    }

    interface Presenter {
        void getWifiList();

        void connectWifi();
    }

}
