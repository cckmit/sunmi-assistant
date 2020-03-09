package com.sunmi.assistant.dashboard.subpage;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.TotalRealTimeShopSalesResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-03-09.
 */
public class PerformanceRankPresenter extends BasePresenter<PerformanceRankContract.View>
        implements PerformanceRankContract.Presenter  {

    @Override
    public void getTotalCustomerShopData() {
        SunmiStoreApi.getInstance().getTotalCustomerShopData(SpUtils.getCompanyId(), new RetrofitCallback<CustomerShopDataResp>() {
            @Override
            public void onSuccess(int code, String msg, CustomerShopDataResp data) {
                if (isViewAttached()){
                    mView.getCustomerSuccess(data.getList());
                }
            }

            @Override
            public void onFail(int code, String msg, CustomerShopDataResp data) {
                if (isViewAttached()){
                    mView.getCustomerFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getTotalSaleShopData() {
        SunmiStoreApi.getInstance().getTotalSaleShopData(SpUtils.getCompanyId(), new RetrofitCallback<TotalRealTimeShopSalesResp>() {
            @Override
            public void onSuccess(int code, String msg, TotalRealTimeShopSalesResp data) {
                if (isViewAttached()){
                    mView.getSaleSuccess(data.getList());
                }
            }

            @Override
            public void onFail(int code, String msg, TotalRealTimeShopSalesResp data) {
                if (isViewAttached()){
                    mView.getSaleFail(code, msg);
                }
            }
        });
    }
}
