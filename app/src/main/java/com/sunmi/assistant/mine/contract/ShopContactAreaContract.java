package com.sunmi.assistant.mine.contract;

import sunmi.common.base.BaseView;
import sunmi.common.model.ShopInfo;

/**
 * @author yangShiJie
 * @date 2019/8/16
 */
public interface ShopContactAreaContract {
    interface View extends BaseView {

        /**
         * 门店名
         */
        void shopNameChanged();

        /**
         * 联系人
         */
        void contactView();

        /**
         * 联系方式
         */
        void contactTelView();

        /**
         * 面积
         */
        void areaView();

    }

    interface Presenter {

        /**
         * 编辑店铺信息
         */
        void editShopMessage(int type, ShopInfo shopInfo);

    }

}
