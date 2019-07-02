package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.sunmi.assistant.dashboard.card.CustomerPriceCard;
import com.sunmi.assistant.dashboard.card.PayMethodCard;
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


class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private static final String TAG = "DashboardPresenter";

    private static final int REFRESH_TIME_PERIOD = 120_000;
    private static final HandlerThread sThread = new HandlerThread("RefreshTask");
    private static final Handler sHandler;

    static {
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
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
        initList(mCompanyId, mShopId, DashboardContract.TIME_PERIOD_INIT);
    }

    @Override
    public void timeSpanSwitchTo(int period) {
        Log.d(TAG, "Switch time span to: " + period);
        if (mPeriod == period || period == DashboardContract.TIME_PERIOD_INIT) {
            Log.d(TAG, "Switch time span skip.");
            return;
        }
        this.mPeriod = period;
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.setPeriod(period);
            }
        }
    }

    @Override
    public void refresh(DataRefreshCallback callback) {
        if (mList != null) {
            for (BaseRefreshCard card : mList) {
                card.refresh();
            }
        }
    }

    private void initList(int companyId, int shopId, int period) {
        mList = new ArrayList<>(9);
        Context context = mView.getContext();
        mList.add(new TitleCard(context));
        mList.add(new TopTabCard(context, period));
        mList.add(new TotalSalesCard(context, companyId, shopId, period));
        mList.add(new CustomerPriceCard(context, companyId, shopId, period));
        mList.add(new TotalCountCard(context, companyId, shopId, period));
        mList.add(new TotalRefundsCard(context, companyId, shopId, period));
        mList.add(new TimeDistributionCard(context, companyId, shopId, period));
        mList.add(new PayMethodCard(context, companyId, shopId, period));
        mList.add(new QuantityRankCard(context, companyId, shopId, period));
        mView.initData(mList);
        mTask = new RefreshTask(mList);
        sHandler.postDelayed(mTask, REFRESH_TIME_PERIOD);
    }

    @Override
    public void detachView() {
        super.detachView();
        sHandler.removeCallbacks(mTask);
    }

    private static class RefreshTask implements Runnable {

        private final List<BaseRefreshCard> mList;

        private RefreshTask(List<BaseRefreshCard> list) {
            this.mList = list;
        }

        @Override
        public void run() {
            for (BaseRefreshCard card : mList) {
                card.refresh();
            }
            sHandler.postDelayed(this, REFRESH_TIME_PERIOD);
        }
    }

}
