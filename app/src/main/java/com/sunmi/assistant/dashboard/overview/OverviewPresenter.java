package com.sunmi.assistant.dashboard.overview;

import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.card.EmptyDataCard;
import com.sunmi.assistant.dashboard.card.EmptyGapCard;
import com.sunmi.assistant.dashboard.card.NoFsCard;
import com.sunmi.assistant.dashboard.card.NoOrderCard;
import com.sunmi.assistant.dashboard.overview.card.DataCard;
import com.sunmi.assistant.dashboard.overview.card.DistributionChartCard;
import com.sunmi.assistant.dashboard.overview.card.PeriodTabCard;
import com.sunmi.assistant.dashboard.overview.card.TrendChartCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;


public class OverviewPresenter extends BasePresenter<OverviewContract.View>
        implements OverviewContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = OverviewPresenter.class.getSimpleName();

    private Context mContext;

    private int mCompanyId;
    private int mShopId;
    private int mSource = -1;
    private int mPeriod;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public int getIndex() {
        return Constants.PAGE_OVERVIEW;
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void load() {
        if (!isViewAttached() || mSource < 0) {
            return;
        }

        mCompanyId = SpUtils.getCompanyId();
        mShopId = SpUtils.getShopId();

        for (BaseRefreshCard card : mList) {
            card.init(mSource);
        }
        mView.setCards(mList);
        refresh(true);
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
        if (mPeriod == period) {
            return;
        }
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
        switch (mSource) {
            case 0x3:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(DistributionChartCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            case 0x2:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(NoFsCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            case 0x1:
                mList.add(PeriodTabCard.init(this, source));
                mList.add(DataCard.init(this, source));
                mList.add(TrendChartCard.init(this, source));
                mList.add(DistributionChartCard.init(this, source));
                mList.add(NoOrderCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
                break;
            default:
                mList.add(EmptyDataCard.init(this, source));
                mList.add(NoFsCard.init(this, source));
                mList.add(NoOrderCard.init(this, source));
                mList.add(EmptyGapCard.init(this, source));
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        mContext = null;
        for (BaseRefreshCard card : mList) {
            card.cancelLoad();
        }
        mList.clear();
    }

}
