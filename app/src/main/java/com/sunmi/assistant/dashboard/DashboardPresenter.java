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
import sunmi.common.model.FilterItem;
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

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_PARAMS = new SimpleDateFormat("yyyy-MM-dd");

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final Handler WORK_HANDLER = new Handler(Looper.getMainLooper());

    private Context mContext;
    private List<PageContract.PagePresenter> mPages = new ArrayList<>(2);

    private int mCompanyId;
    private int mShopId;
    private int mSource = 0;
    private int mPageIndex = 0;

    private int mLoadFlag;
    private boolean mLoadAllPage;
    private boolean mShowLoading;

    private RefreshTask mTask;

    @Override
    public void init() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        load(Constants.FLAG_ALL_MASK, true, true);
        if (mTask == null) {
            mTask = new RefreshTask();
            WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
        }
    }

    @Override
    public void reload(int flag) {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        load(flag, true, true);
    }

    @Override
    public void setShop(FilterItem shop) {
        SpUtils.setShopId(shop.getId());
        SpUtils.setShopName(shop.getItemName());
        BaseNotification.newInstance().postNotificationName(CommonNotifications.shopSwitched);
    }

    @Override
    public void setPeriod(int period) {
        mPages.get(mPageIndex).setPeriod(period);
    }

    @Override
    public void setPage(int index) {
        scrollToTop();
        mPageIndex = index;
    }

    @Override
    public void scrollToTop() {
        mPages.get(mPageIndex).scrollToTop();
    }

    @Override
    public void refresh(boolean forceReload, boolean showLoading) {
        if (forceReload) {
            int flag = 0;
            flag |= Constants.FLAG_SAAS;
            flag |= Constants.FLAG_FS;
            flag |= Constants.FLAG_CUSTOMER;
            load(flag, false, showLoading);
        } else {
            mPages.get(mPageIndex).setSource(mSource, showLoading);
        }
    }

    @Override
    public List<PageHost> getPages() {
        List<PageHost> pages = new ArrayList<>();
        if (!isViewAttached()) {
            return pages;
        }
        mPages.clear();
        OverviewPresenter overviewPresenter = new OverviewPresenter(this, 0);
        OverviewFragment overviewFragment = new OverviewFragment_();
        overviewFragment.inject(mView, overviewPresenter);
        mPages.add(overviewPresenter);
        pages.add(new PageHost(R.string.dashboard_page_overview, 0, overviewFragment));

        CustomerPresenter customerPresenter = new CustomerPresenter(this, 1);
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
    public int getPeriod() {
        return mPages.get(mPageIndex).getPeriod();
    }

    private void load(int loadFlag, boolean allPage, boolean showLoading) {
        this.mLoadFlag = loadFlag;
        this.mLoadAllPage = allPage;
        this.mShowLoading = showLoading;
        if ((loadFlag & Constants.FLAG_SHOP) != 0) {
            loadShop();
        } else if ((loadFlag & Constants.FLAG_SAAS) != 0) {
            loadSaas();
        }
        if ((loadFlag & Constants.FLAG_FS) != 0) {
            loadFs();
        }
        if ((loadFlag & Constants.FLAG_CUSTOMER) != 0) {
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
                List<ShopListResp.ShopInfo> shops = data.getShop_list();
                List<FilterItem> result = new ArrayList<>(shops.size());
                boolean isSaasDocked = true;
                for (ShopListResp.ShopInfo shop : shops) {
                    if (shop.getShop_id() == mShopId) {
                        FilterItem item = new FilterItem(shop.getShop_id(), shop.getShop_name());
                        item.setChecked(true);
                        isSaasDocked = shop.getSaas_exist() == 1;
                        result.add(0, item);
                    } else {
                        result.add(new FilterItem(shop.getShop_id(), shop.getShop_name()));
                    }
                }
                mLoadFlag &= ~Constants.FLAG_SHOP;
                if (isViewAttached()) {
                    mView.setShopList(result);
                }

                if (isSaasDocked && (mLoadFlag & Constants.FLAG_SAAS) != 0) {
                    loadSaas();
                } else {
                    mSource &= ~Constants.DATA_SOURCE_IMPORT;
                    mLoadFlag &= ~Constants.FLAG_SAAS;
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
                            if (list.get(0).getImportStatus() == Constants.IMPORT_SUCCESS) {
                                // 导入成功
                                mSource |= Constants.DATA_SOURCE_IMPORT;
                            } else {
                                mSource &= ~Constants.DATA_SOURCE_IMPORT;
                            }
                        }
                        mLoadFlag &= ~Constants.FLAG_SAAS;
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
                        mLoadFlag &= ~Constants.FLAG_FS;
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
                        mLoadFlag &= ~Constants.FLAG_CUSTOMER;
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
        if (mLoadAllPage) {
            for (PageContract.PagePresenter page : mPages) {
                page.setSource(mSource, mShowLoading);
            }
        } else {
            mPages.get(mPageIndex).setSource(mSource, mShowLoading);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mContext = null;
        WORK_HANDLER.removeCallbacks(mTask);
        for (PageContract.PagePresenter page : mPages) {
            page.release();
        }
        mPages.clear();
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh(true, false);
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
