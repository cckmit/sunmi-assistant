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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.CommonHelper;

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
    private int mPeriod = Constants.TIME_PERIOD_INIT;
    private int mImportState = IMPORT_STATE_DISMISS;

    private boolean isConditionChanged = true;

    RealtimePresenter(PageContract.ParentPresenter parent) {
        this.mParent = parent;
        mCondition = this.mParent.onChildCreate(this);
    }

    @Override
    public void init() {
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
        mList.add(RealtimePeriodCard.get(this, mCondition));
        // No any data
        if (!mCondition.hasSaas && !mCondition.hasFs) {
            mList.add(RealtimeNoDataCard.get(this, mCondition));
            if (!CommonHelper.isGooglePlay()) {
                mList.add(RealtimeNoOrderCard.get(this, mCondition));
            }
            mList.add(RealtimeNoFsCard.get(this, mCondition));
            mList.add(RealtimeGapCard.get(this, mCondition));
            return;
        }

        // Overview data card
        mList.add(RealtimeOverviewCard.get(this, mCondition));

        // Shop enter rate card.
        if (mCondition.hasFs) {
            mList.add(RealtimeEnterRateCard.get(this, mCondition));
        }

        // Realtime trend card (line & bar)
        if (mCondition.hasSaas || mCondition.hasFs) {
            mList.add(RealtimeTrendCard.get(this, mCondition));
        }

        // Distribution card (pie)
        if (mCondition.hasFs) {
            mList.add(RealtimeDistributionCard.get(this, mCondition));
        }

        // No order card or import card
        if (!CommonHelper.isGooglePlay()) {
            if (!mCondition.hasSaas) {
                mList.add(RealtimeNoOrderCard.get(this, mCondition));
            } else if (!mCondition.hasImport || mImportState == IMPORT_STATE_SHOW) {
                RealtimeOrderImportCard card = RealtimeOrderImportCard.get(this, mCondition);
                card.setListener(this);
                mList.add(card);
            }
        }

        // No fs card
        if (!mCondition.hasFs) {
            mList.add(RealtimeNoFsCard.get(this, mCondition));
        }
        mList.add(RealtimeGapCard.get(this, mCondition));
    }

    @Override
    public void onImportStateChange(int state) {
        if (state == Constants.IMPORT_STATE_COMPLETE) {
            this.mImportState = IMPORT_STATE_DISMISS;
            scrollToTop();
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
        }
        mView.setCards(mList);
        setPeriod(Constants.TIME_PERIOD_TODAY);
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
    public void setPeriod(int period) {
        mPeriod = period;
        for (BaseRefreshCard card : mList) {
            card.setPeriod(period, false);
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
    public void scrollToTop() {
        if (isViewAttached()) {
            mView.scrollToTop();
        }
    }

    @Override
    public int getPeriod() {
        return mPeriod;
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
