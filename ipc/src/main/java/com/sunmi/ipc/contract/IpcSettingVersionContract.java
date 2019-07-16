package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/7/15.
 */
public interface IpcSettingVersionContract {
    interface View extends BaseView {


        void getUpgradeSuccess(Object data);

        void getUpgradeFail(int code, String msg);

    }

    interface Presenter {

        void getUpgrade();
    }
}
