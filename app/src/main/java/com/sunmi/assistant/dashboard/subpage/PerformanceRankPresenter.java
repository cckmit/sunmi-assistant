package com.sunmi.assistant.dashboard.subpage;

import android.util.SparseArray;

import com.sunmi.assistant.dashboard.data.Callback;
import com.sunmi.assistant.dashboard.data.DashboardModel;
import com.sunmi.assistant.dashboard.data.DashboardModelImpl;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.ShopInfo;
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
        implements PerformanceRankContract.Presenter {

    private DashboardModel model;

    public PerformanceRankPresenter() {
        model = DashboardModelImpl.get();
    }

    @Override
    public void getTotalCustomerShopData() {
        SunmiStoreApi.getInstance().getTotalRealtimeCustomerByShop(SpUtils.getCompanyId(), new RetrofitCallback<CustomerShopDataResp>() {
            @Override
            public void onSuccess(int code, String msg, CustomerShopDataResp data) {
                if (isViewAttached()) {
                    mView.getCustomerSuccess(data.getList());
                }
            }

            @Override
            public void onFail(int code, String msg, CustomerShopDataResp data) {
                if (isViewAttached()) {
                    mView.getCustomerFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getTotalSaleShopData() {
        SunmiStoreApi.getInstance().getTotalRealtimeSalesByShop(SpUtils.getCompanyId(), new RetrofitCallback<TotalRealTimeShopSalesResp>() {
            @Override
            public void onSuccess(int code, String msg, TotalRealTimeShopSalesResp data) {
                if (isViewAttached()) {
                    mView.getSaleSuccess(data.getList());
                }
            }

            @Override
            public void onFail(int code, String msg, TotalRealTimeShopSalesResp data) {
                if (isViewAttached()) {
                    mView.getSaleFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getShopList() {
        model.loadShopList(SpUtils.getCompanyId(), new Callback<SparseArray<ShopInfo>>() {
            @Override
            public void onLoaded(SparseArray<ShopInfo> result) {
                if (isViewAttached()){
                    mView.getShopListSuccess(result);
                }
            }

            @Override
            public void onFail() {
                if (isViewAttached()){
                    mView.getShopListFail();
                }
            }
        });
    }
}
