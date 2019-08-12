package com.sunmi.assistant.mine.shop;

import com.sunmi.assistant.mine.model.RegionProvince;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public interface ShopRegionContract {

    interface View extends BaseView {

        void showRegionList(List<RegionProvince> list);

        void complete();

        void getRegionFailed();

        void updateRegionFailed();
    }

    interface Presenter {

        void updateRegion(int province, int city, int area);
    }
}
