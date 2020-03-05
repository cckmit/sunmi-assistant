package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class DashboardModelImpl implements DashboardModel {

    private DashboardRepo repo;

    private static final class Holder {
        private static final DashboardModel INSTANCE = new DashboardModelImpl();
    }

    public static DashboardModel get() {
        return Holder.INSTANCE;
    }

    private DashboardModelImpl() {
        repo = DashboardRepoImpl.get();
    }

    @Override
    public void getShopList(int companyId, boolean forceLoad, Callback<SparseArray<ShopInfo>> callback) {

    }

    @Override
    public void hasSaasAuth(int companyId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasSaasAuth(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasSaasImport(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasFs(int companyId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasFs(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasCustomer(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback) {

    }

    @Override
    public void hasCloudService(int companyId, int shopId, boolean forceLoad, Callback<Boolean> callback) {

    }
}
