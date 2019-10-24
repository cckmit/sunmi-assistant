package com.sunmi.assistant.dashboard.overview;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.card.EmptyDataCard;
import com.sunmi.assistant.dashboard.card.EmptyGapCard;
import com.sunmi.assistant.dashboard.card.NoFsCard;
import com.sunmi.assistant.dashboard.card.NoOrderCard;
import com.sunmi.assistant.dashboard.card.OverviewDataCard;
import com.sunmi.assistant.dashboard.card.OverviewDistributionCard;
import com.sunmi.assistant.dashboard.card.OverviewOrderImportCard;
import com.sunmi.assistant.dashboard.card.OverviewPeriodCard;
import com.sunmi.assistant.dashboard.card.OverviewTrendCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public class OverviewPresenter extends BasePresenter<OverviewContract.View>
        implements OverviewContract.Presenter, BaseRefreshCard.Presenter,
        OverviewOrderImportCard.OnImportStateChangeListener {

    private static final String TAG = OverviewPresenter.class.getSimpleName();

    private static final int IMPORT_STATE_SHOW = 0;
    private static final int IMPORT_STATE_DISMISS = 1;

    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;
    private int mImportState = IMPORT_STATE_DISMISS;

    private PageContract.ParentPresenter mParent;
    private int mPageIndex;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    public OverviewPresenter(PageContract.ParentPresenter parent, int index) {
        this.mParent = parent;
        this.mPageIndex = index;
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
    public int getIndex() {
        return mPageIndex;
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
    public void onImportStateChange(int state) {
        if (state == Constants.IMPORT_COMPLETE) {
            this.mImportState = IMPORT_STATE_DISMISS;
            scrollToTop();
            refresh(true);
        } else {
            this.mImportState = IMPORT_STATE_SHOW;
        }
    }

    @Override
    public void release() {
        detachView();
    }

    private void initList(int source) {
        mList.clear();
        mList.add(OverviewPeriodCard.get(this, source));
        // No any data
        if (!Utils.hasAuth(source) && !Utils.hasFs(source)) {
            mList.add(EmptyDataCard.get(this, source));
            mList.add(NoOrderCard.get(this, source));
            mList.add(NoFsCard.get(this, source));
            mList.add(EmptyGapCard.get(this, source));
            return;
        }

        // Time tab & data & trend card
        if (Utils.hasAuth(source) || Utils.hasFs(source)) {
            mList.add(OverviewDataCard.get(this, source));
            mList.add(OverviewTrendCard.get(this, source));
        }

        // Distribution card
        if (Utils.hasFs(source)) {
            mList.add(OverviewDistributionCard.get(this, source));
        }

        // No order card or import card
        if (!Utils.hasAuth(source)) {
            mList.add(NoOrderCard.get(this, source));
        } else if (!Utils.hasImport(source) || mImportState == IMPORT_STATE_SHOW) {
            OverviewOrderImportCard card = OverviewOrderImportCard.get(this, source);
            card.setListener(this);
            mList.add(card);
        }

        // No fs card
        if (!Utils.hasFs(source)) {
            mList.add(NoFsCard.get(this, source));
        }
        mList.add(EmptyGapCard.get(this, source));
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
