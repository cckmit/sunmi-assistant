package com.sunmi.assistant.data;

import android.util.Pair;
import android.util.SparseArray;

import sunmi.common.model.ShopInfo;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface AppModel {

    void getShopList(int companyId, boolean force, Callback<SparseArray<ShopInfo>> callback);

    void getShopListWithAuth(int companyId, boolean force, Callback<Pair<Integer, SparseArray<ShopInfo>>> callback);
}
