package com.sunmi.assistant.dashboard.subpage;

import android.util.SparseArray;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.TotalRealTimeShopSalesResp;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-03-09.
 */
public interface PerformanceRankContract {

    interface View extends BaseView {

        void getCustomerSuccess(List<CustomerShopDataResp.Item> customers);

        void getCustomerFail(int code, String msg);

        void getSaleSuccess(List<TotalRealTimeShopSalesResp.Item> sales);

        void getSaleFail(int code, String msg);

        void getShopListSuccess(SparseArray<ShopInfo> result);

        void getShopListFail();
    }

    interface Presenter {
        void getTotalCustomerShopData();

        void getTotalSaleShopData();

        void getShopList();
    }

}
