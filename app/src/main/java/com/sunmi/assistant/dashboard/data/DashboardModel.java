package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardModel {

    void getShopList(int companyId, Callback<SparseArray<ShopInfo>> callback);

    void hasSaasAuth(int companyId, Callback<Boolean> callback);

    void hasSaasAuth(int companyId, int shopId, Callback<Boolean> callback);

    void hasSaasImport(int companyId, int shopId, Callback<Boolean> callback);

    void hasFs(int companyId, Callback<Boolean> callback);

    void hasFs(int companyId, int shopId, Callback<Boolean> callback);

    void hasCustomer(int companyId, int shopId, Callback<Boolean> callback);

    void hasCloudService(int companyId, int shopId, Callback<Boolean> callback);
}
