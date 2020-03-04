package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealTimeOverviewCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealtimePerformanceCard;
import com.sunmi.assistant.dashboard.card.total.TotalRealtimeTrendCard;
import com.sunmi.assistant.dashboard.util.Constants;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public class TotalRealtimePresenter extends BasePresenter<TotalRealtimeContract.View>
        implements TotalRealtimeContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = TotalRealtimePresenter.class.getSimpleName();

    private static final int IMPORT_STATE_SHOW = 0;
    private static final int IMPORT_STATE_DISMISS = 1;

    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;
    private int mImportState = IMPORT_STATE_DISMISS;

    private PageContract.ParentPresenter mParent;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    TotalRealtimePresenter(PageContract.ParentPresenter parent) {
        this.mParent = parent;
        this.mParent.onChildCreate(getType(), this);
    }

    @Override
    public void load() {
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
    public void setSource(int source, boolean showLoading) {
        if (mSource != source) {
            mSource = source;
            initList(mSource);
            load();
        } else {
            for (BaseRefreshCard card : mList) {
                card.refresh(showLoading);
            }
        }
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
    public void scrollToTop() {
        if (isViewAttached()) {
            mView.scrollToTop();
        }
    }

    @Override
    public void refresh(boolean showLoading) {
        mParent.refresh(true, showLoading);
    }

    @Override
    public int getType() {
        return Constants.PAGE_TOTAL_REALTIME;
    }

    @Override
    public int getPeriod() {
        return mPeriod;
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
    public void release() {
        detachView();
    }

    private void initList(int source) {
        mList.clear();
        mList.add(TotalRealTimeOverviewCard.get(this,source));
        mList.add(TotalRealtimeTrendCard.get(this, source));
        mList.add(TotalRealtimePerformanceCard.get(this, source));
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
