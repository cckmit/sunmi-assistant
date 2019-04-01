package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public interface WifiConfiguringContract {
    interface Model {
    }

    interface View extends BaseView {

        void ipcBindWifiSuccess();

        void ipcBindWifiFail();
    }

    interface Presenter {
        void ipcBind(String shopId, String token, float longitude, float latitude);
    }

}
