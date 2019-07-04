package com.sunmi.assistant.ui.activity.contract;

import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;
import com.sunmi.assistant.ui.activity.model.PlatformInfo;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface SelectPlatformContract {
    interface View extends BaseView {
        void getPlatformInfoSuccess(PlatformInfo data);

        void getPlatformInfoFail(int code, String msg);

//        void createStoreSuccess(CreateStoreInfo data);
//
//        void createStoreFail(int code, String msg);

    }

    interface Presenter {
        void getPlatformInfo();

//        void createStore(String shopName);
    }

}
