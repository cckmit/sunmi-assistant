package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public interface IpcConfiguringContract {

    interface View extends BaseView {

        void ipcBindWifiSuccess(String sn);

        void ipcBindWifiFail(String sn, int code, String msg);
    }

    interface Presenter {
        void ipcBind(String shopId, String sn, String token, float longitude, float latitude);
    }

}
