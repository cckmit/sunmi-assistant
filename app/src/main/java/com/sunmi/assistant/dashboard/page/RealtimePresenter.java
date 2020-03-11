package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeDistributionCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeEnterRateCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeGapCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeNoDataCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeNoFsCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeNoOrderCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeOrderImportCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeOverviewCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimePeriodCard;
import com.sunmi.assistant.dashboard.card.shop.RealtimeTrendCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.Interval;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public class RealtimePresenter extends BasePresenter<RealtimeContract.View>
        implements RealtimeContract.Presenter, BaseRefreshCard.Presenter,
        RealtimeOrderImportCard.OnImportStateChangeListener {

    private static final String TAG = RealtimePresenter.class.getSimpleName();

    private static final int IMPORT_STATE_SHOW = 0;
    private static final int IMPORT_STATE_DISMISS = 1;

    private PageContract.ParentPresenter mParent;
    private List<BaseRefreshCard> mList = new ArrayList<>();

    private DashboardCondition mCondition;
    private int mPeriod;
    private Interval mPeriodTime;
    private int mImportState = IMPORT_STATE_DISMISS;

    private boolean isConditionChanged = true;

    RealtimePresenter(PageContract.ParentPresenter parent) {
        this.mParent = parent;
        mCondition = this.mParent.onChildCreate(this);
    }

    @Override
    public void init() {
        LogCat.d(Utils.TAG, TAG + ": INIT, Condition=" + mCondition);
        mPeriod = Constants.TIME_PERIOD_DAY;
        mPeriodTime = Utils.getPeriodTimestamp(mPeriod, 0);
        if (isViewAttached()) {
            mView.updateTab(mPeriod);
        }
        if (isConditionChanged && mCondition != null) {
            refresh(true);
        }
    }

    @Override
    public int getType() {
        return Constants.PAGE_OVERVIEW;
    }

    private void initList() {
        mList.clear();
        mList.add(RealtimePeriodCard.get(this, mCondition, mPeriod, mPeriodTime));
        // No any data
        if (!mCondition.hasSaas && !mCondition.hasFs) {
            mList.add(RealtimeNoDataCard.get(this, mCondition, mPeriod, mPeriodTime));
            if (!CommonHelper.isGooglePlay()) {
                mList.add(RealtimeNoOrderCard.get(this, mCondition, mPeriod, mPeriodTime));
            }
            mList.add(RealtimeNoFsCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(RealtimeGapCard.get(this, mCondition, mPeriod, mPeriodTime));
            return;
        }

        // Overview data card
        mList.add(RealtimeOverviewCard.get(this, mCondition, mPeriod, mPeriodTime));

        // Shop enter rate card.
        if (mCondition.hasFs) {
            mList.add(RealtimeEnterRateCard.get(this, mCondition, mPeriod, mPeriodTime));
        }

        // Realtime trend card (line & bar)
        if (mCondition.hasSaas || mCondition.hasFs) {
            mList.add(RealtimeTrendCard.get(this, mCondition, mPeriod, mPeriodTime));
        }

        // Distribution card (pie)
        if (mCondition.hasFs) {
            mList.add(RealtimeDistributionCard.get(this, mCondition, mPeriod, mPeriodTime));
        }

        // No order card or import card
        if (!CommonHelper.isGooglePlay()) {
            if (!mCondition.hasSaas) {
                mList.add(RealtimeNoOrderCard.get(this, mCondition, mPeriod, mPeriodTime));
            } else if (!mCondition.hasImport || mImportState == IMPORT_STATE_SHOW) {
                RealtimeOrderImportCard card = RealtimeOrderImportCard.get(this, mCondition, mPeriod, mPeriodTime);
                card.setListener(this);
                mList.add(card);
            }
        }

        // No fs card
        if (!mCondition.hasFs) {
            mList.add(RealtimeNoFsCard.get(this, mCondition, mPeriod, mPeriodTime));
        }
        mList.add(RealtimeGapCard.get(this, mCondition, mPeriod, mPeriodTime));
    }

    @Override
    public void onImportStateChange(int state) {
        if (state == Constants.IMPORT_STATE_COMPLETE) {
            this.mImportState = IMPORT_STATE_DISMISS;
            scrollToTop(true);
        } else {
            this.mImportState = IMPORT_STATE_SHOW;
        }
    }

    private void load() {
        if (!isViewAttached()) {
            return;
        }
        for (BaseRefreshCard card : mList) {
            card.init(mView.getContext());
            card.refresh(true);
        }
        mView.setCards(mList);
    }

    @Override
    public void refresh(boolean showLoading) {
        if (isConditionChanged) {
            initList();
            load();
            isConditionChanged = false;
        } else {
            for (BaseRefreshCard card : mList) {
                card.refresh(showLoading);
            }
        }
    }

    @Override
    public void setCondition(DashboardCondition condition) {
        if (!Objects.equals(mCondition, condition)) {
            mCondition = condition;
            isConditionChanged = true;
        }
    }

    @Override
    public void pullToRefresh(boolean showLoading) {
        mParent.refresh(true, true, true, showLoading);
    }

    @Override
    public void setPeriod(int period, Interval periodTime) {
        if (mPeriod == period && Objects.equals(mPeriodTime, periodTime)) {
            return;
        }
        mPeriod = period;
        mPeriodTime = periodTime;
        for (BaseRefreshCard card : mList) {
            card.setPeriod(period, periodTime, false);
        }
        if (isViewAttached()) {
            mView.updateTab(period);
        }
    }

    @Override
    public void showLoading() {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
    }

    @Override
    public void hideLoading() {
        if (isViewAttached()) {
            mView.hideLoadingDialog();
        }
    }

    @Override
    public void showFailedTip() {
        if (isViewAttached()) {
            mView.shortTip(R.string.toast_network_Exception);
        }
    }

    @Override
    public void scrollToTop(boolean animated) {
        if (isViewAttached()) {
            mView.scrollToTop(animated);
        }
    }

    @Override
    public int getPeriod() {
        return mPeriod;
    }

    @Override
    public Interval getPeriodTime() {
        return mPeriodTime;
    }

    @Override
    public void release() {
        detachView();
    }

    @Override
    public void detachView() {
        super.detachView();
        for (BaseRefreshCard card : mList) {
            card.cancelLoad();
        }
        mList.clear();
    }

}
