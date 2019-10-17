package com.sunmi.assistant.dashboard.overview;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.card.EmptyDataCard;
import com.sunmi.assistant.dashboard.card.EmptyGapCard;
import com.sunmi.assistant.dashboard.card.NoFsCard;
import com.sunmi.assistant.dashboard.card.NoOrderCard;
import com.sunmi.assistant.dashboard.card.OverviewDataCard;
import com.sunmi.assistant.dashboard.card.OverviewDistributionCard;
import com.sunmi.assistant.dashboard.card.OverviewPeriodCard;
import com.sunmi.assistant.dashboard.card.OverviewTrendCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;


public class OverviewPresenter extends BasePresenter<OverviewContract.View>
        implements OverviewContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = OverviewPresenter.class.getSimpleName();

    private int mCompanyId;
    private int mShopId;
    private int mSource = -1;
    private int mPeriod = -1;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    @Override
    public int getType() {
        return Constants.PAGE_OVERVIEW;
    }

    @Override
    public int getScrollY() {
        if (isViewAttached()) {
            return mView.getScrollY();
        } else {
            return 0;
        }
    }

    @Override
    public void scrollTo(int y) {
        if (isViewAttached()) {
            mView.scrollTo(y);
        }
    }

    @Override
    public void load() {
        if (!isViewAttached()) {
            return;
        }

        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();

        for (BaseRefreshCard card : mList) {
            card.reset(mSource);
            card.init(mView.getContext());
        }
        mView.setCards(mList);
        setPeriod(Constants.TIME_PERIOD_TODAY);
    }

    @Override
    public void setSource(int source) {
        if (mSource == source) {
            return;
        }
        mSource = source;
        initList(mSource);
        load();
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
    public int getPeriod() {
        return mPeriod;
    }

    @Override
    public void refresh(boolean showLoading) {
        for (BaseRefreshCard card : mList) {
            card.refresh(showLoading);
        }
    }

    @Override
    public void refresh(int position, boolean showLoading) {
        if (mList.size() > position) {
            mList.get(position).refresh(showLoading);
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
        if (Utils.hasAuth(source) && Utils.hasFs(source)) {
            mList.add(OverviewPeriodCard.get(this, source));
            mList.add(OverviewDataCard.get(this, source));
            mList.add(OverviewTrendCard.get(this, source));
            mList.add(OverviewDistributionCard.get(this, source));
            mList.add(EmptyGapCard.get(this, source));
        } else if (Utils.hasAuth(source) && !Utils.hasFs(source)) {
            mList.add(OverviewPeriodCard.get(this, source));
            mList.add(OverviewDataCard.get(this, source));
            mList.add(OverviewTrendCard.get(this, source));
            mList.add(NoFsCard.get(this, source));
            mList.add(EmptyGapCard.get(this, source));
        } else if (!Utils.hasAuth(source) && Utils.hasFs(source)) {
            mList.add(OverviewPeriodCard.get(this, source));
            mList.add(OverviewDataCard.get(this, source));
            mList.add(OverviewTrendCard.get(this, source));
            mList.add(OverviewDistributionCard.get(this, source));
            mList.add(NoOrderCard.get(this, source));
            mList.add(EmptyGapCard.get(this, source));
        } else {
            mList.add(EmptyDataCard.get(this, source));
            mList.add(NoOrderCard.get(this, source));
            mList.add(NoFsCard.get(this, source));
            mList.add(EmptyGapCard.get(this, source));
        }
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
