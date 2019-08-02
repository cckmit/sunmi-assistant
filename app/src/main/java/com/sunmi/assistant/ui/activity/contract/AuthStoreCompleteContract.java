package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;
import sunmi.common.model.CreateShopInfo;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public interface AuthStoreCompleteContract {
    interface View extends BaseView {
        void editStoreSuccess(Object data);

        void editStoreFail(int code, String msg);

        void createStoreSuccess(CreateShopInfo data);

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
