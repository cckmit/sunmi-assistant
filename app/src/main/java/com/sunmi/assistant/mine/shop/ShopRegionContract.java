package com.sunmi.assistant.mine.shop;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.ShopRegionResp;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public interface ShopRegionContract {

    interface View extends BaseView {

        void showRegionList(List<ShopRegionResp.Province> list);

        void complete();

        void getRegionFailed();

        void updateRegionFailed();
    }

    interface Presenter {

        void getRegion();

        void updateRegion(int province, int city, int area);
    }
}
