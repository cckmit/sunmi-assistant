package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardModel {

    void getShopList(int companyId, boolean forceLoad, Callback<SparseArray<ShopInfo>> callback);

    void hasSaasAuth(int companyId, boolean forceLoad, Callback<Boolean> callback);

    void hasSaasAuth(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback);

    void hasSaasImport(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback);

    void hasFs(int companyId, boolean forceLoad, Callback<Boolean> callback);

    void hasFs(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback);

    void hasCustomer(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback);

    void hasCloudService(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback);
}
