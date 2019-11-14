package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.customer.CustomerFragment;
import com.sunmi.assistant.dashboard.customer.CustomerFragment_;
import com.sunmi.assistant.dashboard.overview.OverviewFragment;
import com.sunmi.assistant.dashboard.overview.OverviewFragment_;
import com.sunmi.bean.BundleServiceMsg;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.rpc.ServiceApi;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.FilterItem;
import sunmi.common.model.ShopAuthorizeInfoResp;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_PARAMS = new SimpleDateFormat("yyyy-MM-dd");

    private static final int REFRESH_TIME_PERIOD = 120_000;

    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private SparseArray<PageContract.PagePresenter> mPages = new SparseArray<>(2);

    private int mCompanyId;
    private int mShopId;
    private int mSource = 0;
    private int mPageType = Constants.PAGE_NONE;

    private int mLoadFlag;
    private boolean mLoadAllPage;
    private boolean mShowLoading;

    private RefreshTask mTask;

    private ShopBundledCloudInfo info;

    private boolean mShowFloating;

    @Override
    public void init() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        load(Constants.FLAG_ALL_MASK, true, true);
        if (mTask == null) {
            mTask = new RefreshTask();
            mHandler.postDelayed(mTask, REFRESH_TIME_PERIOD);
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
        PageContract.PagePresenter current = getCurrent();
        if (current != null) {
            current.setPeriod(period);
        }
    }

    @Override
    public void setPage(int type) {
        scrollToTop();
        mPageType = type;
    }

    @Override
    public void scrollToTop() {
        PageContract.PagePresenter current = getCurrent();
        if (current != null) {
            current.scrollToTop();
        }
    }

    @Override
    public List<PageHost> createPages() {
        // TODO: 可以优化，采用工厂模式
        List<PageHost> pages = new ArrayList<>();

        OverviewFragment overviewFragment = new OverviewFragment_();
        pages.add(new PageHost(R.string.dashboard_page_overview, 0, overviewFragment, Constants.PAGE_OVERVIEW));

        CustomerFragment customerFragment = new CustomerFragment_();
        pages.add(new PageHost(R.string.dashboard_page_customer, 0, customerFragment, Constants.PAGE_CUSTOMER));
        mPageType = Constants.PAGE_OVERVIEW;

        return pages;
    }

    @Override
    public int getPageType() {
        return mPageType;
    }

    @Override
    public int getPeriod() {
        PageContract.PagePresenter current = getCurrent();
        if (current != null) {
            return current.getPeriod();
        } else {
            return Constants.TIME_PERIOD_INIT;
        }
    }

    @Override
    public void onChildCreate(int pageType, PageContract.PagePresenter presenter) {
        mPages.put(pageType, presenter);
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
            PageContract.PagePresenter current = getCurrent();
            if (current != null) {
                current.setSource(mSource, showLoading);
            }
        }
    }

    private PageContract.PagePresenter getCurrent() {
        PageContract.PagePresenter current = mPages.get(mPageType);
        if (current == null) {
            LogCat.e(TAG, "Page type of " + mPageType + " is ERROR.");
        }
        return current;
    }

    private void load(int loadFlag, boolean allPage, boolean showLoading) {
        this.mLoadFlag = loadFlag;
        this.mLoadAllPage = allPage;
        this.mShowLoading = showLoading;
        if ((loadFlag & Constants.FLAG_CUSTOMER) != 0) {
            loadCustomer();
        }
        if ((loadFlag & Constants.FLAG_SHOP) != 0) {
            loadShop();
        } else if ((loadFlag & Constants.FLAG_SAAS) != 0) {
            loadSaas();
        }
        if ((loadFlag & Constants.FLAG_FS) != 0) {
            loadFs();
        }

        if ((loadFlag & Constants.FLAG_BUNDLED_LIST) != 0) {
            loadBundledList();
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
                List<ShopInfo> shops = data.getShop_list();
                List<FilterItem> result = new ArrayList<>(shops.size());
                boolean isSaasDocked = true;
                for (ShopInfo shop : shops) {
                    if (shop.getShopId() == mShopId) {
                        FilterItem item = new FilterItem(shop.getShopId(), shop.getShopName());
                        item.setChecked(true);
                        isSaasDocked = shop.getSaasExist() == 1;
                        result.add(0, item);
                    } else {
                        result.add(new FilterItem(shop.getShopId(), shop.getShopName()));
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

    private void loadBundledList() {
        info = DataSupport.where("shopId=?", String.valueOf(mShopId)).findFirst(ShopBundledCloudInfo.class);
        if (info == null) {
            info = new ShopBundledCloudInfo(mShopId);
        }
        mShowFloating = info.isFloatingShow();
        ServiceApi.getInstance().getBundledList(new RetrofitCallback<BundleServiceMsg>() {
            @Override
            public void onSuccess(int code, String msg, BundleServiceMsg data) {
                List<BundleServiceMsg.SubscriptionListBean> beans = data.getSubscriptionList();
                Set<String> oldSet = info.getSnSet();
                Set<String> newSet = new HashSet<>();
                if (beans != null && beans.size() > 0) {
                    for (BundleServiceMsg.SubscriptionListBean bean : beans) {
                        if (bean.getActiveStatus() == CommonConstants.ACTIVE_CLOUD_INACTIVATED) {
                            newSet.add(bean.getDeviceSn());
                            if (!oldSet.contains(bean.getDeviceSn())) {
                                mShowFloating = true;
                            }
                        }
                    }
                    info.setSnSet(newSet);
                }
                mLoadFlag &= ~Constants.FLAG_BUNDLED_LIST;
                saveShopBundledCloudInfo(mShowFloating);
                loadComplete();
                if (isViewAttached()) {
                    mView.updateFloating(mShowFloating);
                }
            }

            @Override
            public void onFail(int code, String msg, BundleServiceMsg data) {
                mLoadFlag &= ~Constants.FLAG_BUNDLED_LIST;
                loadComplete();
                if (isViewAttached()) {
                    mView.updateFloating(mShowFloating);
                }
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
            for (int i = 0, size = mPages.size(); i < size; i++) {
                PageContract.PagePresenter page = mPages.valueAt(i);
                page.setSource(mSource, mShowLoading);
            }
        } else {
            PageContract.PagePresenter current = getCurrent();
            if (current != null) {
                current.setSource(mSource, mShowLoading);
            }
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mContext = null;
        mHandler.removeCallbacks(mTask);
        for (int i = 0, size = mPages.size(); i < size; i++) {
            PageContract.PagePresenter page = mPages.valueAt(i);
            page.release();
        }
        mPages.clear();
    }

    public void saveShopBundledCloudInfo(boolean isShowFloating) {
        if (isShowFloating) {
            mSource |= Constants.DATA_SOURCE_FLOATING;
        } else {
            mSource &= ~Constants.DATA_SOURCE_FLOATING;
        }
        info.setFloatingShow(isShowFloating);
        ThreadPool.getCachedThreadPool().submit(() -> {
            info.saveOrUpdate("shopId=?", String.valueOf(mShopId));
        });
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh(true, false);
            mHandler.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
