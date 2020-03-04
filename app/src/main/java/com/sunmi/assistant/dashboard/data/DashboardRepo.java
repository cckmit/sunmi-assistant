package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import java.util.List;

import sunmi.common.model.CompanyIpcList;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.SaasStatus;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardRepo {

    void getShopList(int companyId, boolean forceLoad, Callback<SparseArray<ShopInfo>> callback);

    void getSaasStatus(int companyId, boolean forceLoad, Callback<SparseArray<List<SaasStatus>>> callback);

    void getIpcList(int companyId, boolean forceLoad, Callback<List<CompanyIpcList>> callback);

    void getCustomer(int companyId, int shopId, boolean forceLoad, Callback<CustomerHistoryResp> callback);

    void getBundledList(int companyId, int shopId, boolean forceLoad, Callback<ShopBundledCloudInfo> callback);
}
