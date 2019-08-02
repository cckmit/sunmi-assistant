package com.sunmi.assistant.ui.activity.contract;

import sunmi.common.base.BaseView;
import sunmi.common.model.CreateShopInfo;

/**
 * @author yangShiJie
 * @date 2019/8/1
 */
public interface CreateShopContract {
    interface View extends BaseView {

        void createShopSuccessView(CreateShopInfo resp);

    }

    interface Presenter {

        void createShop(String name, String contact, String mobile);
    }
}
