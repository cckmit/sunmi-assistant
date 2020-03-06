package com.sunmi.assistant.dashboard;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.Callback;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.data.DashboardModel;
import com.sunmi.assistant.dashboard.data.DashboardModelImpl;
import com.sunmi.assistant.dashboard.page.CustomerFragment;
import com.sunmi.assistant.dashboard.page.CustomerFragment_;
import com.sunmi.assistant.dashboard.page.ProfileFragment;
import com.sunmi.assistant.dashboard.page.ProfileFragment_;
import com.sunmi.assistant.dashboard.page.RealtimeFragment;
import com.sunmi.assistant.dashboard.page.RealtimeFragment_;
import com.sunmi.assistant.dashboard.page.TotalRealtimeFragment;
import com.sunmi.assistant.dashboard.page.TotalRealtimeFragment_;
import com.sunmi.assistant.dashboard.util.Constants;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.FilterItem;
import sunmi.common.model.Interval;
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = DashboardPresenter.class.getSimpleName();

    private static final int REFRESH_TIME_PERIOD = 120_000;

    private DashboardModel model;

    private int mCompanyId;
    private int mShopId;
    private int mPerspective = CommonConstants.PERSPECTIVE_TOTAL;

    private DashboardCondition mCondition;
    private boolean mFloatingAdClosed = false;

    private SparseArray<PageContract.PagePresenter> mPages = new SparseArray<>(3);
    private int mPageType = Constants.PAGE_NONE;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RefreshTask mTask;

    @Override
    public void init() {
        model = DashboardModelImpl.get();

        switchToTotalPerspective(false);
        load(Constants.FLAG_ALL_MASK, false, false, true);
    }

    @Override
    public DashboardCondition onChildCreate(PageContract.PagePresenter presenter) {
        mPages.put(presenter.getType(), presenter);
        return mCondition;
    }

    @Override
    public void refresh(boolean reloadCondition, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        if (reloadCondition) {
            load(getConditionMask(), clearCache, onlyCurrentPage, showLoading);
            return;
        }
        for (int i = 0, size = mPages.size(); i < size; i++) {
            PageContract.PagePresenter page = mPages.valueAt(i);
            if (!onlyCurrentPage || page.getType() == mPageType) {
                page.refresh(showLoading);
            }
        }
    }

    @Override
    public void load(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        if (needLoadShop(flag)) {
            loadShop(clearCache);
        }
        if (needLoadCondition(flag)) {
            loadCondition(flag, clearCache, onlyCurrentPage, showLoading);
        }
    }

    private void loadShop(boolean clearCache) {
        if (clearCache) {
            model.clearCache(Constants.FLAG_SHOP);
        }
        model.loadShopList(mCompanyId, new Callback<SparseArray<ShopInfo>>() {
            @Override
            public void onLoaded(SparseArray<ShopInfo> result) {
                List<FilterItem> list = new ArrayList<>(result.size());
                for (int i = 0, size = result.size(); i < size; i++) {
                    ShopInfo shop = result.valueAt(i);
                    list.add(new FilterItem(shop.getShopId(), shop.getShopName()));
                }
                if (isViewAttached()) {
                    mView.setShopList(list);
                }
            }

            @Override
            public void onFail() {
                if (isViewAttached()) {
                    mView.loadDataFailed();
                }
            }
        });
    }

    private void loadCondition(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        if (clearCache) {
            model.clearCache(flag);
        }
        Callback<DashboardCondition> callback = new Callback<DashboardCondition>() {
            @Override
            public void onLoaded(DashboardCondition result) {
                LogCat.i(TAG, "Load condition success.");
                mCondition = result;
                for (int i = 0, size = mPages.size(); i < size; i++) {
                    PageContract.PagePresenter page = mPages.valueAt(i);
                    page.setCondition(mCondition);
                    if (!onlyCurrentPage || page.getType() == mPageType) {
                        page.refresh(showLoading);
                    }
                }
                if (isViewAttached()) {
                    mView.setCondition(mCondition);
                    mView.updateFloating(!mFloatingAdClosed && mCondition.isFloatingShow);
                }
            }

            @Override
            public void onFail() {
                LogCat.e(TAG, "Load condition failed.");
                if (isViewAttached()) {
                    mView.loadDataFailed();
                }
            }
        };
        if (mPerspective == CommonConstants.PERSPECTIVE_TOTAL) {
            model.loadCondition(flag, mCondition, mCompanyId, callback);
        } else if (mPerspective == CommonConstants.PERSPECTIVE_SHOP) {
            model.loadCondition(flag, mCondition, mCompanyId, mShopId, callback);
        }
    }

    @Override
    public void switchPerspective(int perspective) {
        if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
            switchToTotalPerspective(true);
        } else if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
            switchToShopPerspective(true);
        }
    }

    private void switchToTotalPerspective(boolean refresh) {
        LogCat.d("yinhui", "presenter: switch to total.");
        mCondition = null;
        mPerspective = CommonConstants.PERSPECTIVE_TOTAL;
        mPages.clear();

        List<PageHost> pages = new ArrayList<>();

        TotalRealtimeFragment totalRealtimeFragment = new TotalRealtimeFragment_();
        pages.add(new PageHost(R.string.dashboard_page_realtime_today, 0,
                totalRealtimeFragment, Constants.PAGE_TOTAL_REALTIME));

        mPageType = Constants.PAGE_TOTAL_REALTIME;
        if (isViewAttached()) {
            mView.setPages(pages, mPerspective);
        }
        if (refresh) {
            refresh(true, false, true, true);
        }
    }

    private void switchToShopPerspective(boolean refresh) {
        LogCat.d("yinhui", "presenter: switch to shop.");
        mCondition = null;
        mPerspective = CommonConstants.PERSPECTIVE_SHOP;
        mPages.clear();

        List<PageHost> pages = new ArrayList<>();

        RealtimeFragment realtimeFragment = new RealtimeFragment_();
        pages.add(new PageHost(R.string.dashboard_page_realtime_data, 0, realtimeFragment, Constants.PAGE_OVERVIEW));

        CustomerFragment customerFragment = new CustomerFragment_();
        pages.add(new PageHost(R.string.dashboard_page_customer, 0, customerFragment, Constants.PAGE_CUSTOMER));

        ProfileFragment profileFragment = new ProfileFragment_();
        pages.add(new PageHost(R.string.dashboard_page_profile, 0, profileFragment, Constants.PAGE_PROFILE));

        mPageType = Constants.PAGE_OVERVIEW;
        if (isViewAttached()) {
            mView.setPages(pages, mPerspective);
        }
        if (refresh) {
            refresh(true, false, true, true);
        }
    }

    @Override
    public void switchPeriod(int period, Interval periodTime) {
        PageContract.PagePresenter current = getCurrent();
        if (current != null) {
            current.setPeriod(period, periodTime);
        }
    }

    @Override
    public void switchPage(int type) {
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
    public void closeFloatingAd() {
        mFloatingAdClosed = true;
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
            return Constants.TIME_PERIOD_DAY;
        }
    }

    @Override
    public void startAutoRefresh() {
        if (mTask == null) {
            mTask = new RefreshTask();
        }
        mHandler.removeCallbacks(mTask);
        mHandler.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    @Override
    public void stopAutoRefresh() {
        mHandler.removeCallbacks(mTask);
    }

    private PageContract.PagePresenter getCurrent() {
        PageContract.PagePresenter current = mPages.get(mPageType);
        if (current == null) {
            LogCat.e(TAG, "Page type of " + mPageType + " is ERROR.");
        }
        return current;
    }

    @Override
    public void detachView() {
        super.detachView();
        stopAutoRefresh();
        for (int i = 0, size = mPages.size(); i < size; i++) {
            PageContract.PagePresenter page = mPages.valueAt(i);
            page.release();
        }
        mPages.clear();
    }

    private boolean needLoadShop(int flag) {
        return (flag & Constants.FLAG_SHOP) != 0;
    }

    private boolean needLoadCondition(int flag) {
        return (mPerspective == CommonConstants.PERSPECTIVE_TOTAL && (flag & Constants.FLAG_CONDITION_COMPANY_MASK) != 0)
                || (mPerspective == CommonConstants.PERSPECTIVE_SHOP && (flag & Constants.FLAG_CONDITION_SHOP_MASK) != 0);
    }

    private int getConditionMask() {
        if (mPerspective == CommonConstants.PERSPECTIVE_TOTAL) {
            return Constants.FLAG_CONDITION_COMPANY_MASK;
        } else {
            return Constants.FLAG_CONDITION_SHOP_MASK;
        }
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh(false, false, false, false);
            mHandler.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
