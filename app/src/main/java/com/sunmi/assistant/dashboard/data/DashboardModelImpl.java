package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class DashboardModelImpl implements DashboardModel {

    @Override
    public void getShopList(int companyId, Callback<SparseArray<ShopInfo>> callback) {

    }

    @Override
    public void hasSaasAuth(int companyId, Callback<Boolean> callback) {

    }

    @Override
    public void hasSaasAuth(int companyId, int shopId, Callback<Boolean> callback) {

    }

    @Override
    public void hasSaasImport(int companyId, int shopId, Callback<Boolean> callback) {

    }

    @Override
    public void hasFs(int companyId, Callback<Boolean> callback) {

    }

    @Override
    public void hasFs(int companyId, int shopId, Callback<Boolean> callback) {

    }

    @Override
    public void hasCustomer(int companyId, int shopId, Callback<Boolean> callback) {

    }

    @Override
    public void hasCloudService(int companyId, int shopId, Callback<Boolean> callback) {

    }
}
