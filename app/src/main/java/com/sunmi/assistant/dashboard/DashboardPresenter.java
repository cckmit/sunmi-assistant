package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshItem;
import com.sunmi.assistant.dashboard.card.DataCard;
import com.sunmi.assistant.dashboard.card.DistributionChartCard;
import com.sunmi.assistant.dashboard.card.PeriodTabCard;
import com.sunmi.assistant.dashboard.card.TrendChartCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
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

    private int mCompanyId;
    private int mShopId;

    private int mDataSource = 0x3;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private List<BaseRefreshItem> mList;

    private RefreshTask mTask;

    @Override
    public void init() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        initList(mCompanyId, mShopId);
    }

    @Override
    public void switchPeriodTo(int period) {
        LogCat.d(TAG, "All card switch period to: " + period + "; Current period is " + mPeriod);
        if (mPeriod == period || period == Constants.TIME_PERIOD_INIT) {
            LogCat.d(TAG, "Switch period skip.");
            return;
        }
        this.mPeriod = period;
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
                card.setPeriod(period);
            }
        }
        if (isViewAttached()) {
            mView.updateTab(period);
        }
    }

    @Override
    public void switchCompanyTo(int companyId, int shopId) {
        mCompanyId = companyId;
        mShopId = shopId;
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
                card.setCompanyId(companyId, shopId);
            }
        }
    }

    @Override
    public void switchShopTo(int shopId) {
        mShopId = shopId;
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
                card.setShopId(shopId);
            }
        }
    }

    @Override
    public void refresh() {
        if (mList != null) {
            for (BaseRefreshItem card : mList) {
                card.refresh();
            }
        }
    }

    @Override
    public void refresh(int position) {
        if (mList != null && mList.size() > position) {
            mList.get(position).refresh();
        }
    }

    @Override
    public void showFailedTip() {
        if (isViewAttached()) {
            mView.shortTip(R.string.toast_network_Exception);
        }
    }

    private void initList(int companyId, int shopId) {
        if (!isViewAttached()) {
            return;
        }
        Context context = mView.getContext();
        mList = new ArrayList<>(5);
        mList.add(new PeriodTabCard(context, this, mDataSource));
        mList.add(new DataCard(context, this, mDataSource));
        mList.add(new TrendChartCard(context, this, mDataSource));
        mList.add(new DistributionChartCard(context, this, mDataSource));
        mView.initData(mList);
        mTask = new RefreshTask();
        WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
        switchPeriodTo(Constants.TIME_PERIOD_TODAY);
    }

    @Override
    public void detachView() {
        super.detachView();
        WORK_HANDLER.removeCallbacks(mTask);
        for (BaseRefreshItem card : mList) {
            card.cancelLoad();
        }
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh();
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
