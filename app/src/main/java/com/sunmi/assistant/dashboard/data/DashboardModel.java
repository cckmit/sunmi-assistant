package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardModel {

    void getShopList(int companyId, Callback<SparseArray<ShopInfo>> callback);

    void hasSaasState(int companyId, Callback<Integer> callback);

    void hasSaasState(int companyId, int shopId, Callback<Integer> callback);

    void hasFs(int companyId, Callback<Boolean> callback);

    void hasFs(int companyId, int shopId, Callback<Boolean> callback);

    void hasCustomer(int companyId, int shopId, Callback<Boolean> callback);

    void isFloatingShow(int companyId, int shopId, Callback<Boolean> callback);

    void loadCondition(int flag, DashboardCondition condition,
                       int companyId, Callback<DashboardCondition> callback);

    void loadCondition(int flag, DashboardCondition condition,
                       int companyId, int shopId, Callback<DashboardCondition> callback);

    void clearCache(int flag);

}
