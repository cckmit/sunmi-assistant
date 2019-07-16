package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/7/15.
 */
public interface IpcSettingNightStyleContract {
    interface View extends BaseView {


        void setNightStyleSuccess(Object data);

        void setNightStyleFail(int code, String msg);

    }

    interface Presenter {

        void setNightStyle();
    }
}
