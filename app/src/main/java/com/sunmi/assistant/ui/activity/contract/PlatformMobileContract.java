package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/7/3.
 */
public interface PlatformMobileContract {
    interface View extends BaseView {
        void sendMobileCodeSuccess(Object data);

        void sendMobileCodeFail(int code, String msg);

        void checkMobileCodeSuccess(Object data);

        void checkMobileCodeFail(int code, String msg);

        void getSaasInfoSuccess(Object data);

        void getSaasInfoFail(int code, String msg);

//        void createStoreSuccess(CreateStoreInfo data);
//
//        void createStoreFail(int code, String msg);

    }

    interface Presenter {
        void sendMobileCode(String mobile);

        void checkMobileCode(String mobile, String code);

        void getSaasInfo(String mobile);

//        void createStore(String shopName);
    }
}
