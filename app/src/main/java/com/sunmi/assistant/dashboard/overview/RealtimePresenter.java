package com.sunmi.assistant.dashboard.overview;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.card.RealtimeDistributionCard;
import com.sunmi.assistant.dashboard.card.RealtimeEnterRateCard;
import com.sunmi.assistant.dashboard.card.RealtimeGapCard;
import com.sunmi.assistant.dashboard.card.RealtimeNoDataCard;
import com.sunmi.assistant.dashboard.card.RealtimeNoFsCard;
import com.sunmi.assistant.dashboard.card.RealtimeNoOrderCard;
import com.sunmi.assistant.dashboard.card.RealtimeOrderImportCard;
import com.sunmi.assistant.dashboard.card.RealtimeOverviewCard;
import com.sunmi.assistant.dashboard.card.RealtimePeriodCard;
import com.sunmi.assistant.dashboard.card.RealtimeTrendCard;

import java.util.ArrayList;
import java.util.List;

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

    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;
    private int mImportState = IMPORT_STATE_DISMISS;

    private PageContract.ParentPresenter mParent;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    RealtimePresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_OVERVIEW;
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
        mList.add(RealtimePeriodCard.get(this, source));
        // No any data
        if (!Utils.hasAuth(source) && !Utils.hasFs(source)) {
            mList.add(RealtimeNoDataCard.get(this, source));
            if (!CommonHelper.isGooglePlay()) {
                mList.add(RealtimeNoOrderCard.get(this, source));
            }
            mList.add(RealtimeNoFsCard.get(this, source));
            mList.add(RealtimeGapCard.get(this, source));
            return;
        }

        // Overview data card
        if (Utils.hasAuth(source) || Utils.hasFs(source)) {
            mList.add(RealtimeOverviewCard.get(this, source));
        }

        // Shop enter rate card.
        if (Utils.hasFs(source)) {
            mList.add(RealtimeEnterRateCard.get(this, source));
        }

        // Realtime trend card (line & bar)
        if (Utils.hasAuth(source) || Utils.hasFs(source)) {
            mList.add(RealtimeTrendCard.get(this, source));
        }

        // Distribution card (pie)
        if (Utils.hasFs(source)) {
            mList.add(RealtimeDistributionCard.get(this, source));
        }

        // No order card or import card
        if (!CommonHelper.isGooglePlay()) {
            if (!Utils.hasAuth(source)) {
                mList.add(RealtimeNoOrderCard.get(this, source));
            } else if (!Utils.hasImport(source) || mImportState == IMPORT_STATE_SHOW) {
                RealtimeOrderImportCard card = RealtimeOrderImportCard.get(this, source);
                card.setListener(this);
                mList.add(card);
            }
        }

        // No fs card
        if (!Utils.hasFs(source)) {
            mList.add(RealtimeNoFsCard.get(this, source));
        }
        mList.add(RealtimeGapCard.get(this, source));
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
