package com.sunmi.assistant.ui.activity.contract;

import com.sunmi.assistant.ui.activity.model.CreateStoreInfo;

import sunmi.common.base.BaseView;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface AuthStoreCompleteContract {
    interface View extends BaseView {
        void editStoreSuccess(Object data);

        void editStoreFail(int code, String msg);

        void createStoreSuccess(CreateStoreInfo data);

        void createStoreFail(int code, String msg);

        void authStoreCompleteSuccess(Object data);

        void authStoreCompleteFail(int code, String msg);


    }

    interface Presenter {
        void authStoreCompleteInfo(int shop_id, int saas_source,
                                   String shop_no, String saas_name);

        void createStore(String shopName);

        void editStore(String shopName);
    }

}
