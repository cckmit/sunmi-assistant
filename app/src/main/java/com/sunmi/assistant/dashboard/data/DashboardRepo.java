package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import com.sunmi.assistant.data.Callback;

import java.util.List;

import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.IpcDevice;
import sunmi.common.model.SaasStatus;
import sunmi.common.model.ShopBundledCloudInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardRepo {

    void getSaasStatus(int companyId, Callback<SparseArray<List<SaasStatus>>> callback);

    void getIpcList(int companyId, Callback<SparseArray<List<IpcDevice>>> callback);

    void getCustomer(int companyId, int shopId, Callback<CustomerHistoryResp> callback);

    void getBundledList(int companyId, int shopId, Callback<ShopBundledCloudInfo> callback);

    void clearCache(int flag);
}
