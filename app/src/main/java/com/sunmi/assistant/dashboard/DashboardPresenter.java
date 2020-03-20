package com.sunmi.assistant.dashboard;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.data.DashboardModel;
import com.sunmi.assistant.dashboard.data.DashboardModelImpl;
import com.sunmi.assistant.dashboard.page.CustomerFragment;
import com.sunmi.assistant.dashboard.page.CustomerFragment_;
import com.sunmi.assistant.dashboard.page.ProfileFragment;
import com.sunmi.assistant.dashboard.page.ProfileFragment_;
import com.sunmi.assistant.dashboard.page.RealtimeFragment;
import com.sunmi.assistant.dashboard.page.RealtimeFragment_;
import com.sunmi.assistant.dashboard.page.TotalCustomerFragment;
import com.sunmi.assistant.dashboard.page.TotalCustomerFragment_;
import com.sunmi.assistant.dashboard.page.TotalRealtimeFragment;
import com.sunmi.assistant.dashboard.page.TotalRealtimeFragment_;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.assistant.data.AppModel;
import com.sunmi.assistant.data.AppModelImpl;
import com.sunmi.assistant.data.Callback;
import com.sunmi.assistant.utils.AppConstants;

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

    private AppModel appModel;
    private DashboardModel model;

    private boolean mInit = false;

    private int mCompanyId;
    private int mShopId;
    private int mAuthority = AppConstants.ACCOUNT_AUTH_NONE;
    private int mPerspective = CommonConstants.PERSPECTIVE_NONE;

    private DashboardCondition mCondition;
    private boolean mFloatingAdClosed = false;

    private SparseArray<PageContract.PagePresenter> mPages = new SparseArray<>(3);
    private int mPageType = Constants.PAGE_NONE;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RefreshTask mTask;

    @Override
    public void init() {
        appModel = AppModelImpl.get();
        model = DashboardModelImpl.get();
        mPerspective = SpUtils.getPerspective();
        load(Constants.FLAG_ALL_MASK, false, false, true);
    }

    private void initPerspective() {
        if (mInit) {
            return;
        }
        if (mPerspective == CommonConstants.PERSPECTIVE_NONE) {
            // 第一次登陆，根据权限自动选择视角
            if (mAuthority == AppConstants.ACCOUNT_AUTH_COMPANY) {
                switchPerspective(CommonConstants.PERSPECTIVE_TOTAL, -1, false);
            } else {
                switchPerspective(CommonConstants.PERSPECTIVE_SHOP, mShopId, false);
            }
        } else {
            // 非第一次登陆，根据SP缓存，加载上一次选择的视角
            if (mPerspective == CommonConstants.PERSPECTIVE_TOTAL && mAuthority == AppConstants.ACCOUNT_AUTH_SHOP) {
                // 权限降级，原总部视角因为外部原因，降级为门店视角
                mPerspective = CommonConstants.PERSPECTIVE_SHOP;
            }
            switchPerspective(mPerspective, mShopId, false);
        }
        mInit = true;
    }

    @Override
    public DashboardCondition onChildCreate(PageContract.PagePresenter presenter) {
        int type = presenter.getType();
        mPages.put(type, presenter);
        LogCat.i(Utils.TAG, "Create page:" + type);
        return mCondition == null ? null : mCondition.copy();
    }

    @Override
    public void refresh(boolean reloadCondition, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        if (reloadCondition) {
            load(getConditionMask(), clearCache, onlyCurrentPage, showLoading);
            return;
        }
        LogCat.i(Utils.TAG, "Just refresh. Current=" + onlyCurrentPage);
        for (int i = 0, size = mPages.size(); i < size; i++) {
            PageContract.PagePresenter page = mPages.valueAt(i);
            if (!onlyCurrentPage || page.getType() == mPageType) {
                page.refresh(true, showLoading);
            }
        }
    }

    @Override
    public void load(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        LogCat.i(Utils.TAG, "Loading. Flag=" + flag
                + "; Clear=" + clearCache + "; Current=" + onlyCurrentPage);
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        if (needLoadShop(flag)) {
            loadShopAndCondition(flag, clearCache, onlyCurrentPage, showLoading);
        } else if (needLoadCondition(flag)) {
            loadCondition(flag, clearCache, onlyCurrentPage, showLoading);
        }
    }

    private void loadShopAndCondition(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        loadShop(clearCache, new Callback<Object>() {
            @Override
            public void onLoaded(Object result) {
                if (needLoadCondition(flag)) {
                    loadCondition(flag, clearCache, onlyCurrentPage, showLoading);
                }
            }

            @Override
            public void onFail() {
            }
        });
    }

    private void loadShop(boolean clearCache, Callback<?> callback) {
        appModel.getShopListWithAuth(mCompanyId, clearCache, new Callback<Pair<Integer, SparseArray<ShopInfo>>>() {
            @Override
            public void onLoaded(Pair<Integer, SparseArray<ShopInfo>> result) {
                int authority = result.first;
                SparseArray<ShopInfo> shopMap = result.second;
                mAuthority = authority;
                // 构建门店列表筛选项
                List<FilterItem> list = new ArrayList<>(shopMap.size());
                for (int i = 0, size = shopMap.size(); i < size; i++) {
                    ShopInfo info = shopMap.valueAt(i);
                    boolean checked = authority == AppConstants.ACCOUNT_AUTH_SHOP && info.getShopId() == mShopId;
                    list.add(new FilterItem(info.getShopId(), info.getShopName(), checked));
                }
                if (isViewAttached()) {
                    mView.setShopList(authority, list);
                }
                initPerspective();
                if (callback != null) {
                    callback.onLoaded(null);
                }
            }

            @Override
            public void onFail() {
                if (isViewAttached()) {
                    mView.loadDataFailed();
                }
                if (callback != null) {
                    callback.onFail();
                }
            }
        });

    }

    private void loadCondition(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading) {
        if (clearCache) {
            model.clearCache(flag);
        } else if (mFloatingAdClosed) {
            flag &= ~Constants.FLAG_BUNDLED_LIST;
        }
        final int finalFlag = flag;
        Callback<DashboardCondition> callback = new Callback<DashboardCondition>() {
            @Override
            public void onLoaded(DashboardCondition result) {
                LogCat.i(Utils.TAG, "Load condition success. Flag=" + finalFlag
                        + "; Clear=" + clearCache + "; Current=" + onlyCurrentPage);
                mCondition = result;
                if (mFloatingAdClosed) {
                    mCondition.hasFloating = false;
                }
                for (int i = 0, size = mPages.size(); i < size; i++) {
                    PageContract.PagePresenter page = mPages.valueAt(i);
                    LogCat.i(Utils.TAG, "Page:" + page.getType());
                    page.setCondition(mCondition.copy());
                    if (!onlyCurrentPage || page.getType() == mPageType) {
                        page.refresh(true, showLoading);
                    }
                }
                if (isViewAttached()) {
                    mView.setCondition(mCondition);
                    mView.updateFloating(!mFloatingAdClosed && mCondition.hasFloating);
                }
            }

            @Override
            public void onFail() {
                LogCat.e(Utils.TAG, "Load condition failed.");
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
    public void switchPerspective(int perspective, int shopId, boolean refresh) {
        if (isViewAttached()) {
            mView.switchPerspective(perspective, shopId);
        }
        if (perspective == CommonConstants.PERSPECTIVE_TOTAL) {
            switchToTotalPerspective(refresh);
        } else if (perspective == CommonConstants.PERSPECTIVE_SHOP) {
            switchToShopPerspective(refresh);
        }
    }

    @Override
    public void switchShop(int shopId) {
        if (isViewAttached()) {
            mView.switchShop(shopId);
        }
        refresh(true, false, false, true);
    }

    private void switchToTotalPerspective(boolean refresh) {
        LogCat.i(Utils.TAG, "presenter: switch to total. Refresh=" + refresh);
        mPerspective = CommonConstants.PERSPECTIVE_TOTAL;
        SpUtils.setPerspective(mPerspective);
        mCondition = null;
        mPages.clear();

        List<PageHost> pages = new ArrayList<>();

        TotalRealtimeFragment totalRealtimeFragment = new TotalRealtimeFragment_();
        pages.add(new PageHost(R.string.dashboard_page_realtime_today, 0,
                totalRealtimeFragment, Constants.PAGE_TOTAL_REALTIME));

        TotalCustomerFragment totalCustomerFragment = new TotalCustomerFragment_();
        pages.add(new PageHost(R.string.dashboard_page_customer, 0,
                totalCustomerFragment, Constants.PAGE_TOTAL_CUSTOMER));

        mPageType = Constants.PAGE_TOTAL_REALTIME;
        if (isViewAttached()) {
            mView.setPages(pages, mPerspective);
        }
        if (refresh) {
            refresh(true, false, false, true);
        }
    }

    private void switchToShopPerspective(boolean refresh) {
        LogCat.i(Utils.TAG, "presenter: switch to shop. Refresh=" + refresh);
        mPerspective = CommonConstants.PERSPECTIVE_SHOP;
        SpUtils.setPerspective(mPerspective);
        mCondition = null;
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
            refresh(true, false, false, true);
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
        LogCat.i(Utils.TAG, "Switch page:" + type);
        scrollToTop(mPerspective != CommonConstants.PERSPECTIVE_TOTAL);
        mPageType = type;
        getCurrent().refresh(false, true);
    }

    @Override
    public void scrollToTop(boolean animated) {
        PageContract.PagePresenter current = getCurrent();
        if (current != null) {
            current.scrollToTop(animated);
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
        LogCat.i(Utils.TAG, "Dashboard detached.");
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
