package com.sunmi.assistant.dashboard.data;

import com.sunmi.assistant.data.Callback;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface DashboardModel {

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
