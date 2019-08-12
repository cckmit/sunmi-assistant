package com.sunmi.assistant.mine.shop;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.ShopCategoryResp;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public interface ShopCategoryContract {

    interface View extends BaseView {

        void showCategoryList(List<ShopCategoryResp.ShopTypeListBean> list);

        void complete();

        void getCategoryFailed();

        void updateCategoryFailed();
    }

    interface Presenter {

        void getCategory();

        void updateCategory(int type1, int type2);
    }
}
