package com.sunmi.assistant.mine.contract;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.ShopCategoryResp;


/**
 * @author yangShiJie
 * @date 2019/7/31
 */
public interface ShopCreateContract {

    interface View extends BaseView {

        void showCategoryList(List<ShopCategoryResp.ShopTypeListBean> list);

        void getCategoryFailed();

        void createShopSuccess(CreateShopInfo resp);

        void createShopFail(int code, String msg);

    }

    interface Presenter {

        void getCategory();

        void createShop(int companyId, String shopName, int province, int city, int area,
                        String address, int typeOne, int typeTwo,
                        float businessArea, String person, String tel);

    }
}
