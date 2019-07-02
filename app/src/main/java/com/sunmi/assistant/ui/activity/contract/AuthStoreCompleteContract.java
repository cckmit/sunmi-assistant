package com.sunmi.assistant.ui.activity.contract;

import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface AuthStoreCompleteContract {
    interface View extends BaseView {
        void createStoreSuccess(CreateStoreInfo data);

        void createStoreFail(int code, String msg);

        void authStoreCompleteSuccess(String data);

        void authStoreCompleteFail(int code, String msg);


    }

    interface Presenter {
        void authStoreCompleteInfo(String shop_id, String saas_source,
                                   String shop_no, String saas_name);

        void createStore(String shopName);
    }

}
