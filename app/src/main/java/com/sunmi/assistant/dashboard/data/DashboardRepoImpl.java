package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.bean.BundleServiceMsg;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.rpc.ServiceApi;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.AuthorizeInfoResp;
import sunmi.common.model.CompanyIpcListResp;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.IpcDevice;
import sunmi.common.model.SaasStatus;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class DashboardRepoImpl implements DashboardRepo {

    private static final String TAG = DashboardRepoImpl.class.getSimpleName();

    private SparseArray<ShopInfo> shopMap;
    private SparseArray<List<SaasStatus>> saasMap;
    private SparseArray<List<IpcDevice>> deviceMap;
    private CustomerHistoryResp customer;
    private ShopBundledCloudInfo bundledCloudInfo;

    private static final class Holder {
        private static final DashboardRepo INSTANCE = new DashboardRepoImpl();
    }

    public static DashboardRepo get() {
        return Holder.INSTANCE;
    }

    private DashboardRepoImpl() {
    }

    @Override
    public void getShopList(int companyId, Callback<SparseArray<ShopInfo>> callback) {
        if (shopMap != null) {
            callback.onLoaded(shopMap);
            return;
        }
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
                shopMap = null;
                callback.onFail();
            }
        });
    }

    @Override
    public void getSaasStatus(int companyId, Callback<SparseArray<List<SaasStatus>>> callback) {
        if (saasMap != null) {
            callback.onLoaded(saasMap);
            return;
        }
        SunmiStoreApi.getInstance().getAuthorizeInfo(companyId, new RetrofitCallback<AuthorizeInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, AuthorizeInfoResp data) {
                if (data == null || data.getList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<SaasStatus> list = data.getList();
                saasMap = new SparseArray<>();
                for (SaasStatus info : list) {
                    List<SaasStatus> statusList = saasMap.get(info.getShopId());
                    if (statusList == null) {
                        statusList = new ArrayList<>();
                        saasMap.put(info.getShopId(), statusList);
                    }
                    statusList.add(info);
                }
                callback.onLoaded(saasMap);
            }

            @Override
            public void onFail(int code, String msg, AuthorizeInfoResp data) {
                LogCat.e(TAG, "Load saas status Failed. " + code + ":" + msg);
                saasMap = null;
                callback.onFail();
            }
        });
    }

    @Override
    public void getIpcList(int companyId, Callback<SparseArray<List<IpcDevice>>> callback) {
        if (deviceMap != null) {
            callback.onLoaded(deviceMap);
            return;
        }
        IpcCloudApi.getInstance().getCompanyIpcList(companyId, new RetrofitCallback<CompanyIpcListResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyIpcListResp data) {
                if (data == null || data.getList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<IpcDevice> list = data.getList();
                deviceMap = new SparseArray<>();
                for (IpcDevice info : list) {
                    if (!DeviceTypeUtils.getInstance().isFS1(info.getModel())) {
                        continue;
                    }
                    List<IpcDevice> deviceList = deviceMap.get(info.getShopId());
                    if (deviceList == null) {
                        deviceList = new ArrayList<>();
                        deviceMap.put(info.getShopId(), deviceList);
                    }
                    deviceList.add(info);
                }
                callback.onLoaded(deviceMap);
            }

            @Override
            public void onFail(int code, String msg, CompanyIpcListResp data) {
                LogCat.e(TAG, "Load ipc device Failed. " + code + ":" + msg);
                deviceMap = null;
                callback.onFail();
            }
        });
    }

    @Override
    public void getCustomer(int companyId, int shopId, Callback<CustomerHistoryResp> callback) {
        if (customer != null) {
            callback.onLoaded(customer);
            return;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        String startTime = Utils.formatTime(Utils.FORMAT_DATE, c.getTimeInMillis());
        c.add(Calendar.MONTH, 2);
        c.add(Calendar.DATE, -1);
        String endTime = Utils.formatTime(Utils.FORMAT_DATE, c.getTimeInMillis());
        SunmiStoreApi.getInstance().getHistoryCustomer(companyId, shopId, startTime, endTime,
                new RetrofitCallback<CustomerHistoryResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerHistoryResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        success(code, msg, data);
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerHistoryResp data) {
                        if (code == Constants.NO_CUSTOMER_DATA) {
                            success(code, msg, data);
                        } else {
                            LogCat.e(TAG, "Load customer Failed. " + code + ":" + msg);
                            customer = null;
                            callback.onFail();
                        }
                    }

                    private void success(int code, String msg, CustomerHistoryResp data) {
                        customer = data == null ? new CustomerHistoryResp() : data;
                        callback.onLoaded(customer);
                    }
                });
    }

    @Override
    public void getBundledList(int companyId, int shopId, Callback<ShopBundledCloudInfo> callback) {
        if (bundledCloudInfo != null) {
            callback.onLoaded(bundledCloudInfo);
            return;
        }
        ServiceApi.getInstance().getBundledList(new RetrofitCallback<BundleServiceMsg>() {
            @Override
            public void onSuccess(int code, String msg, BundleServiceMsg data) {
                // 读取数据库缓存
                bundledCloudInfo = DataSupport
                        .where("shopId=?", String.valueOf(shopId))
                        .findFirst(ShopBundledCloudInfo.class);
                if (bundledCloudInfo == null) {
                    bundledCloudInfo = new ShopBundledCloudInfo(shopId);
                }
                // 检查API返回的数据
                boolean showFloating = bundledCloudInfo.isFloatingShow();
                List<BundleServiceMsg.SubscriptionListBean> beans = data.getSubscriptionList();
                Set<String> oldSet = bundledCloudInfo.getSnSet();
                Set<String> newSet = new HashSet<>();
                if (beans != null && beans.size() > 0) {
                    for (BundleServiceMsg.SubscriptionListBean bean : beans) {
                        if (bean.getActiveStatus() == CommonConstants.SERVICE_INACTIVATED) {
                            newSet.add(bean.getDeviceSn());
                            if (!oldSet.contains(bean.getDeviceSn())) {
                                showFloating = true;
                            }
                        }
                    }
                    if (newSet.size() == 0) {
                        showFloating = false;
                    }
                } else {
                    showFloating = false;
                }
                // 更新数据库缓存
                bundledCloudInfo.setSnSet(newSet);
                bundledCloudInfo.setFloatingShow(showFloating);
                ThreadPool.getCachedThreadPool().submit(() -> {
                    bundledCloudInfo.saveOrUpdate("shopId=?", String.valueOf(shopId));
                    BaseNotification.newInstance().postNotificationName(CommonNotifications.activeCloudChange);
                });
                callback.onLoaded(bundledCloudInfo);
            }

            @Override
            public void onFail(int code, String msg, BundleServiceMsg data) {
                LogCat.e(TAG, "Load bundled list Failed. " + code + ":" + msg);
                bundledCloudInfo = null;
                callback.onFail();
            }
        });
    }

    @Override
    public void clearCache(int flag) {
        if ((flag & Constants.FLAG_SHOP) != 0) {
            shopMap = null;
        }
        if ((flag & Constants.FLAG_SAAS) != 0) {
            saasMap = null;
        }
        if ((flag & Constants.FLAG_FS) != 0) {
            deviceMap = null;
        }
        if ((flag & Constants.FLAG_CUSTOMER) != 0) {
            customer = null;
        }
        if ((flag & Constants.FLAG_BUNDLED_LIST) != 0) {
            bundledCloudInfo = null;
        }
    }
}
