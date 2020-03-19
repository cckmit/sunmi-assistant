package com.sunmi.assistant.data;

import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.assistant.utils.AppConstants;

import java.util.List;

import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class AppModelImpl implements AppModel, BaseNotification.NotificationCenterDelegate {

    private static final String TAG = AppModelImpl.class.getSimpleName();

    private SparseArray<ShopInfo> shopMap;
    private int authority = AppConstants.ACCOUNT_AUTH_NONE;

    private static final class Holder {
        private static final AppModel INSTANCE = new AppModelImpl();
    }

    public static AppModel get() {
        return Holder.INSTANCE;
    }

    private AppModelImpl() {
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.logout);
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.companySwitch);
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.importShop);
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.shopCreate);
    }

    @Override
    public void getShopList(int companyId, boolean force, Callback<SparseArray<ShopInfo>> callback) {
        if (!force && shopMap != null) {
            callback.onLoaded(shopMap);
            return;
        }
        this.shopMap = null;
        SunmiStoreApi.getInstance().getShopList(companyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (data == null || data.getShop_list() == null) {
                    onFail(code, msg, null);
                    return;
                }
                List<ShopInfo> list = data.getShop_list();
                shopMap = new SparseArray<>(list.size());
                for (ShopInfo shopInfo : list) {
                    shopMap.put(shopInfo.getShopId(), shopInfo);
                }
                callback.onLoaded(shopMap);
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Load shop list Failed. " + code + ":" + msg);
                callback.onFail();
            }
        });
    }

    @Override
    public void getShopListWithAuth(int companyId, boolean force, Callback<Pair<Integer, SparseArray<ShopInfo>>> callback) {
        if (!force && authority != AppConstants.ACCOUNT_AUTH_NONE && shopMap != null) {
            callback.onLoaded(new Pair<>(authority, shopMap));
            return;
        }
        this.shopMap = null;
        this.authority = AppConstants.ACCOUNT_AUTH_NONE;
        getShopList(companyId, force, new Callback<SparseArray<ShopInfo>>() {
            @Override
            public void onLoaded(SparseArray<ShopInfo> shopList) {
                shopMap = shopList;
                getTotalShopList(companyId, new Callback<List<ShopInfo>>() {
                    @Override
                    public void onLoaded(List<ShopInfo> result) {
                        for (ShopInfo info : result) {
                            if (shopList.indexOfKey(info.getShopId()) < 0) {
                                // 商户下存在门店不在授权门店列表中，故该账户没有该商户的总部权限
                                authority = AppConstants.ACCOUNT_AUTH_SHOP;
                                callback.onLoaded(new Pair<>(authority, shopMap));
                                return;
                            }
                        }
                        authority = AppConstants.ACCOUNT_AUTH_COMPANY;
                        callback.onLoaded(new Pair<>(authority, shopMap));
                    }

                    @Override
                    public void onFail() {
                        LogCat.e(TAG, "Load account authority Failed.");
                        callback.onFail();
                    }
                });
            }

            @Override
            public void onFail() {
                LogCat.e(TAG, "Load account authority Failed.");
                authority = AppConstants.ACCOUNT_AUTH_NONE;
                callback.onFail();
            }
        });
    }

    private void getTotalShopList(int companyId, Callback<List<ShopInfo>> callback) {
        SunmiStoreApi.getInstance().getTotalShopList(companyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (data == null || data.getShop_list() == null) {
                    onFail(code, msg, null);
                    return;
                }
                callback.onLoaded(data.getShop_list());
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Load total shop list Failed. " + code + ":" + msg);
                callback.onFail();
            }
        });
    }

    private void clearCache() {
        shopMap = null;
        authority = AppConstants.ACCOUNT_AUTH_NONE;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.logout
                || id == CommonNotifications.companySwitch
                || id == CommonNotifications.importShop
                || id == CommonNotifications.shopCreate) {
            clearCache();
        }
    }

}
