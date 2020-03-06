package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealtimeOverviewCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealtimePerformanceCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealtimeTrendCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public class TotalRealtimePresenter extends BasePresenter<TotalRealtimeContract.View>
        implements TotalRealtimeContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = TotalRealtimePresenter.class.getSimpleName();


    private PageContract.ParentPresenter mParent;
    private List<BaseRefreshCard> mList = new ArrayList<>();

    private DashboardCondition mCondition;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private boolean isConditionChanged = true;

    TotalRealtimePresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_TOTAL_REALTIME;
    }

    private void initList() {
        mList.clear();
        mList.add(TotalRealtimeOverviewCard.get(this, mCondition));
        mList.add(TotalRealtimeTrendCard.get(this, mCondition));
        mList.add(TotalRealtimePerformanceCard.get(this, mCondition));
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
