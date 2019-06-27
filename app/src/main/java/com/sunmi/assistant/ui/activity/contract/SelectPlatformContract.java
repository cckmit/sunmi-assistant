package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface SelectPlatformContract {
    interface View extends BaseView {
        void getPlatformInfoSuccess(String data);

        void getPlatformInfoFail(int code, String msg);

    }

    interface Presenter {
        void getPlatformInfo();
    }

}
