package com.sunmi.assistant.contract;

import com.sunmi.assistant.data.response.ShopListResp;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description: ChooseShopContract
 * Created by Bruce on 2019/7/1.
 */
public interface ChooseShopContract {

    interface View extends BaseView {
        void getShopListSuccess(List<ShopListResp.ShopInfo> shopList);
    }

    interface Presenter {
        void getShopList();
    }
}
