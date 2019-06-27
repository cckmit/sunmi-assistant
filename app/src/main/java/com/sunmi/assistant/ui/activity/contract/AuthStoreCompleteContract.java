package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface AuthStoreCompleteContract {
    interface View extends BaseView {
        void getAuthStoreCompleteSuccess(String data);

        void getAuthStoreCompleteFail(int code, String msg);

    }

    interface Presenter {
        void getAuthStoreCompleteInfo();
    }

}
