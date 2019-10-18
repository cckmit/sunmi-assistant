package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.customer.CustomerFragment;
import com.sunmi.assistant.dashboard.customer.CustomerFragment_;
import com.sunmi.assistant.dashboard.customer.CustomerPresenter;
import com.sunmi.assistant.dashboard.overview.OverviewFragment;
import com.sunmi.assistant.dashboard.overview.OverviewFragment_;
import com.sunmi.assistant.dashboard.overview.OverviewPresenter;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.ShopAuthorizeInfoResp;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    public static final int FLAG_SHOP = 0x1;
    public static final int FLAG_SAAS = 0x2;
    public static final int FLAG_FS = 0x4;
    public static final int FLAG_CUSTOMER = 0x8;
    public static final int FLAG_ALL_MASK = 0xF;

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_PARAMS = new SimpleDateFormat("yyyy-MM-dd");

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final Handler WORK_HANDLER = new Handler(Looper.getMainLooper());

    private Context mContext;
    private List<DashboardContract.PagePresenter> mPages = new ArrayList<>(2);
    private List<ShopItem> mShops = new ArrayList<>();

    private int mCompanyId;
    private int mShopId;
    private int mSource = 0;
    private int mPageIndex = 0;

    private int mLoadFlag;

    private RefreshTask mTask;

    @Override
    public void init() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        mLoadFlag = FLAG_ALL_MASK;
        load();
        if (mTask == null) {
            mTask = new RefreshTask();
            WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
        }
    }

    @Override
    public void reloadCompanySwitch() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        mLoadFlag = FLAG_ALL_MASK;
        load();
    }

    @Override
    public void reloadShopSwitch() {
        mShopId = SpUtils.getShopId();
        updateShops();
        mLoadFlag = FLAG_SAAS | FLAG_FS | FLAG_CUSTOMER;
        load();
    }

    @Override
    public void reloadShopList() {
        mLoadFlag = FLAG_SHOP;
        load();
    }

    @Override
    public void reloadFs() {
        mLoadFlag = FLAG_FS;
        load();
    }

    @Override
    public void setShop(ShopItem shop) {
        SpUtils.setShopId(shop.getShopId());
        SpUtils.setShopName(shop.getShopName());
        SpUtils.setSaasExist(shop.isSaasExist() ? 1 : 0);
        BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
    }

    @Override
    public void setPeriod(int period) {
        mPages.get(mPageIndex).setPeriod(period);
    }

    @Override
    public void setPage(int index) {
        mPages.get(mPageIndex).scrollTo(0);
        mPageIndex = index;
    }

    @Override
    public List<PageHost> getPages() {
        List<PageHost> pages = new ArrayList<>();
        if (!isViewAttached()) {
            return pages;
        }
        mPages.clear();
        OverviewPresenter overviewPresenter = new OverviewPresenter();
        OverviewFragment overviewFragment = new OverviewFragment_();
        overviewFragment.inject(mView, overviewPresenter);
        mPages.add(overviewPresenter);
        pages.add(new PageHost(R.string.dashboard_page_overview, 0, overviewFragment));

        CustomerPresenter customerPresenter = new CustomerPresenter();
        CustomerFragment customerFragment = new CustomerFragment_();
        customerFragment.inject(mView, customerPresenter);
        mPages.add(customerPresenter);
        pages.add(new PageHost(R.string.dashboard_page_customer, 0, customerFragment));

        return pages;
    }

    @Override
    public int getPageIndex() {
        return mPageIndex;
    }

    @Override
    public int getPageType() {
        return mPages.get(mPageIndex).getType();
    }

    @Override
    public int getPeriod() {
        return mPages.get(mPageIndex).getPeriod();
    }

    private void updateShops() {
        if (mShops.isEmpty()) {
            mLoadFlag |= FLAG_SHOP;
            load();
        } else {
            ShopItem current = null;
            for (ShopItem shop : mShops) {
                shop.setChecked(shop.getShopId() == mShopId);
                if (shop.getShopId() == mShopId) {
                    current = shop;
                }
            }
            if (current == null) {
                // TODO:
                return;
            }
            mShops.remove(current);
            mShops.add(0, current);
            if (current.isSaasExist()) {
                mSource |= Constants.DATA_SOURCE_AUTH;
            } else {
                mSource &= ~Constants.DATA_SOURCE_AUTH;
            }
            if (isViewAttached()) {
                mView.setShopList(mShops);
            }

        }
    }

    private void load() {
        if ((mLoadFlag & FLAG_SHOP) != 0) {
            loadShop();
        } else if ((mLoadFlag & FLAG_SAAS) != 0) {
            loadSaas();
        }
        if ((mLoadFlag & FLAG_FS) != 0) {
            loadFs();
        }
        if ((mLoadFlag & FLAG_CUSTOMER) != 0) {
            loadCustomer();
        }
    }

    private void loadShop() {
        SunmiStoreApi.getInstance().getShopList(mCompanyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (data == null || data.getShop_list() == null) {
                    onFail(code, msg, null);
                    return;
                }
                mShops.clear();
                List<ShopListResp.ShopInfo> shops = data.getShop_list();
                ShopItem current = null;
                for (ShopListResp.ShopInfo shop : shops) {
                    if (shop.getShop_id() == mShopId) {
                        ShopItem item = new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1);
                        item.setChecked(true);
                        current = item;
                        mShops.add(0, item);
                    } else {
                        mShops.add(new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1));
                    }
                }
                if (current == null) {
                    // TODO:
                    return;
                }
                if (current.isSaasExist()) {
                    mSource |= Constants.DATA_SOURCE_AUTH;
                } else {
                    mSource &= ~Constants.DATA_SOURCE_AUTH;
                }
                mLoadFlag &= ~FLAG_SHOP;
                if (isViewAttached()) {
                    mView.setShopList(mShops);
                }

                if (current.isSaasExist() && (mLoadFlag & FLAG_SAAS) != 0) {
                    loadSaas();
                } else {
                    mSource &= ~Constants.DATA_SOURCE_IMPORT;
                    mLoadFlag &= ~FLAG_SAAS;
                }
                loadComplete();
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Load shop list Failed. " + code + ":" + msg);
                if (isViewAttached()) {
                    mView.loadDataFailed();
                }
            }
        });
    }

    private void loadSaas() {
        SunmiStoreApi.getInstance().getAuthorizeInfo(mCompanyId, mShopId,
                new RetrofitCallback<ShopAuthorizeInfoResp>() {
                    @Override
                    public void onSuccess(int code, String msg, ShopAuthorizeInfoResp data) {
                        if (data == null || data.getAuthorizedList() == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        List<ShopAuthorizeInfoResp.Info> list = data.getAuthorizedList();
                        if (list.isEmpty()) {
                            mSource &= ~Constants.DATA_SOURCE_AUTH;
                            mSource &= ~Constants.DATA_SOURCE_IMPORT;
                        } else {
                            mSource |= Constants.DATA_SOURCE_AUTH;
                            if (list.get(0).getImportStatus() == 2) {
                                // 导入成功
                                mSource |= Constants.DATA_SOURCE_IMPORT;
                            } else {
                                mSource &= ~Constants.DATA_SOURCE_IMPORT;
                            }
                        }
                        mLoadFlag &= ~FLAG_SAAS;
                        loadComplete();
                    }

                    @Override
                    public void onFail(int code, String msg, ShopAuthorizeInfoResp data) {
                        LogCat.e(TAG, "Load saas import Failed. " + code + ":" + msg);
                        if (isViewAttached()) {
                            mView.loadDataFailed();
                        }
                    }
                });
    }

    private void loadFs() {
        IpcCloudApi.getInstance().getDetailList(mCompanyId, mShopId,
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                            mSource |= Constants.DATA_SOURCE_FS;
                        } else {
                            mSource &= ~Constants.DATA_SOURCE_FS;
                        }
                        mLoadFlag &= ~FLAG_FS;
                        loadComplete();
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                        LogCat.e(TAG, "Load fs source Failed. " + code + ":" + msg);
                        if (isViewAttached()) {
                            mView.loadDataFailed();
                        }
                    }
                });
    }

    private void loadCustomer() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        String startTime = DATE_FORMAT_PARAMS.format(c.getTime());
        c.add(Calendar.MONTH, 2);
        c.add(Calendar.DATE, -1);
        String endTime = DATE_FORMAT_PARAMS.format(c.getTime());
        SunmiStoreApi.getInstance().getHistoryCustomer(mCompanyId, mShopId, startTime, endTime,
                new RetrofitCallback<CustomerHistoryResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerHistoryResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        mSource |= Constants.DATA_SOURCE_CUSTOMER;
                        success(code, msg, data);
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerHistoryResp data) {
                        if (code == Constants.NO_CUSTOMER_DATA) {
                            mSource &= ~Constants.DATA_SOURCE_CUSTOMER;
                            success(code, msg, data);
                        } else {
                            LogCat.e(TAG, "Load customer source Failed. " + code + ":" + msg);
                            if (isViewAttached()) {
                                mView.loadDataFailed();
                            }
                        }
                    }

                    private void success(int code, String msg, CustomerHistoryResp data) {
                        mLoadFlag &= ~FLAG_CUSTOMER;
                        loadComplete();
                    }
                });
    }

    private void loadComplete() {
        if (mLoadFlag != 0) {
            return;
        }
        if (isViewAttached()) {
            mView.setSource(mSource);
        }
        for (DashboardContract.PagePresenter page : mPages) {
            page.setSource(mSource);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mContext = null;
        WORK_HANDLER.removeCallbacks(mTask);
        for (DashboardContract.PagePresenter page : mPages) {
            page.release();
        }
        mPages.clear();
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            if (!Utils.hasAuth(mSource)) {
                mLoadFlag |= FLAG_SAAS;
                load();
            }
            mPages.get(mPageIndex).refresh(false);
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
