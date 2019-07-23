package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.CustomerPriceCard;
import com.sunmi.assistant.dashboard.card.PurchaseTypeCard;
import com.sunmi.assistant.dashboard.card.QuantityRankCard;
import com.sunmi.assistant.dashboard.card.TimeDistributionCard;
import com.sunmi.assistant.dashboard.card.TitleCard;
import com.sunmi.assistant.dashboard.card.TopTabCard;
import com.sunmi.assistant.dashboard.card.TotalCountCard;
import com.sunmi.assistant.dashboard.card.TotalRefundsCard;
import com.sunmi.assistant.dashboard.card.TotalSalesCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = "DashboardPresenter";

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final HandlerThread WORK_THREAD = new HandlerThread("RefreshTask");
    private static final Handler WORK_HANDLER;

    static {
        WORK_THREAD.start();
        WORK_HANDLER = new Handler(WORK_THREAD.getLooper());
    }

    private int mCompanyId;
    private int mShopId;

    private int mPeriod = DashboardContract.TIME_PERIOD_INIT;

    private List<BaseRefreshCard> mList;

    private RefreshTask mTask;

    @Override
    public void loadConfig() {
        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();
        initList(mCompanyId, mShopId);
    }

    @Override
    public void switchPeriodTo(int period) {
        LogCat.d(TAG, "All card switch period to: " + period + "; Current period is " + mPeriod);
        if (mPeriod == period || period == DashboardContract.TIME_PERIOD_INIT) {
            LogCat.d(TAG, "Switch period skip.");
            return;
        }
        this.mPeriod = period;
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setPeriod(period);
            }
        }
        if (isViewAttached()) {
            mView.updateStickyTab(period);
        }
    }

    @Override
    public void switchShopTo(int shopId) {
        mShopId = shopId;
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setShopId(shopId);
            }
        }
    }

    @Override
    public void refresh() {
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
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
        mList = new ArrayList<>(9);
        mList.add(new TitleCard(context, this, companyId, shopId));
        mList.add(new TopTabCard(context, this));

        mList.add(new TotalSalesCard(context, this, companyId, shopId));
        mList.add(new CustomerPriceCard(context, this, companyId, shopId));
        mList.add(new TotalCountCard(context, this, companyId, shopId));
        mList.add(new TotalRefundsCard(context, this, companyId, shopId));

        mList.add(new TimeDistributionCard(context, this, companyId, shopId));
        mList.add(new PurchaseTypeCard(context, this, companyId, shopId));
        mList.add(new QuantityRankCard(context, this, companyId, shopId));
        mView.initData(mList);
        mTask = new RefreshTask();
        WORK_HANDLER.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    @Override
    public void detachView() {
        super.detachView();
        WORK_HANDLER.removeCallbacks(mTask);
    }

    private class RefreshTask implements Runnable {

        @Override
        public void run() {
            refresh();
            WORK_HANDLER.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
