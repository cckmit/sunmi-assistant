package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.customer.CustomerFragment;
import com.sunmi.assistant.dashboard.customer.CustomerFragment_;
import com.sunmi.assistant.dashboard.customer.CustomerPresenter;
import com.sunmi.assistant.dashboard.overview.OverviewFragment;
import com.sunmi.assistant.dashboard.overview.OverviewFragment_;
import com.sunmi.assistant.dashboard.overview.OverviewPresenter;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ShopListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final HandlerThread WORK_THREAD = new HandlerThread("RefreshTask");
    private static final Handler WORK_HANDLER;

    static {
        WORK_THREAD.start();
        WORK_HANDLER = new Handler(WORK_THREAD.getLooper());
    }

    private Context mContext;
    private List<DashboardContract.PagePresenter> mPages = new ArrayList<>(2);

    private ShopItem mShop;
    private int mCompanyId;
    private int mSource = 0;

    private int mPageIndex = 0;

    private RefreshTask mTask;

    @Override
    public void init(Context context) {
        if (!isViewAttached()) {
            return;
        }
        mContext = context;
        mCompanyId = SpUtils.getCompanyId();
        loadShopList();
        if (mTask == null) {
            mTask = new RefreshTask();
            WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
        }
    }

    @Override
    public void reload() {
        loadSource(mShop.isSaasExist());
    }

    @Override
    public void setShop(ShopItem shop) {
        mShop.setChecked(false);
        mShop = shop;
        shop.setChecked(true);
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
    public int getPeriod() {
        return mPages.get(mPageIndex).getPeriod();
    }

    private void loadShopList() {
        SunmiStoreApi.getInstance().getShopList(mCompanyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (data == null) {
                    onFail(code, msg, null);
                    return;
                }
                List<ShopListResp.ShopInfo> shops = data.getShop_list();
                if (shops == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<ShopItem> result = new ArrayList<>(shops.size());
                int shopId = SpUtils.getShopId();
                for (ShopListResp.ShopInfo shop : shops) {
                    if (shop.getShop_id() == shopId) {
                        ShopItem item = new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1);
                        item.setChecked(true);
                        mShop = item;
                        result.add(0, item);
                    } else {
                        result.add(new ShopItem(shop.getShop_id(), shop.getShop_name(),
                                shop.getSaas_exist() == 1));
                    }
                }
                if (isViewAttached()) {
                    mView.setShopList(result);
                }
                loadSource(mShop.isSaasExist());
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

    private void loadSource(boolean saasExist) {
        int old = mSource;
        if (saasExist) {
            mSource |= Constants.DATA_SOURCE_SAAS;
        } else {
            mSource &= ~Constants.DATA_SOURCE_SAAS;
        }
        IpcCloudApi.getInstance().getDetailList(mCompanyId, mShop.getShopId(), new RetrofitCallback<IpcListResp>() {
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
                if (isViewAttached()) {
                    mView.setSource(mSource);
                }
                for (DashboardContract.PagePresenter page : mPages) {
                    page.setSource(mSource);
                }
            }

            @Override
            public void onFail(int code, String msg, IpcListResp data) {
                LogCat.e(TAG, "Load data source Failed. " + code + ":" + msg);
                mView.loadDataFailed();
            }
        });
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
            mPages.get(0).refresh(false);
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
