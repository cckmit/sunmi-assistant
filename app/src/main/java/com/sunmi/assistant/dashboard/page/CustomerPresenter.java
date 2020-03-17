package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerEnterRateCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerFrequencyAvgCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerFrequencyDistributionCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerFrequencyTrendCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerNoDataCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerNoFsCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerOverviewCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerPeriodCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerTrendCard;
import com.sunmi.assistant.dashboard.card.shop.CustomerWaitDataCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.Interval;
import sunmi.common.utils.log.LogCat;


/**
 * @author yinhui
 * @date 2019-10-14
 */
public class CustomerPresenter extends BasePresenter<CustomerContract.View>
        implements CustomerContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = CustomerPresenter.class.getSimpleName();

    private PageContract.ParentPresenter mParent;
    private List<BaseRefreshCard> mList = new ArrayList<>();

    private DashboardCondition mCondition;
    private int mPeriod;
    private Interval mPeriodTime;

    private boolean isConditionChanged = true;

    CustomerPresenter(PageContract.ParentPresenter parent) {
        this.mParent = parent;
        mCondition = this.mParent.onChildCreate(this);
    }

    @Override
    public void init() {
        LogCat.d(Utils.TAG, TAG + ": INIT, Condition=" + mCondition);
        mPeriod = Constants.TIME_PERIOD_DAY;
        mPeriodTime = Utils.getPeriodTimestamp(mPeriod, System.currentTimeMillis() - Utils.MILLIS_OF_DAY);
        if (isViewAttached()) {
            mView.updateTab(mPeriod);
        }
        if (isConditionChanged && mCondition != null) {
            refresh(false, true);
        }
    }

    @Override
    public int getType() {
        return Constants.PAGE_CUSTOMER;
    }

    private void initList() {
        mList.clear();
        mList.add(CustomerPeriodCard.get(this, mCondition, mPeriod, mPeriodTime));
        if (mCondition.hasCustomer) {
            mList.add(CustomerOverviewCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(CustomerTrendCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(CustomerEnterRateCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(CustomerFrequencyDistributionCard.get(this, mCondition, mPeriod, mPeriodTime));
            if (mPeriod != Constants.TIME_PERIOD_DAY) {
                mList.add(CustomerFrequencyTrendCard.get(this, mCondition, mPeriod, mPeriodTime));
                mList.add(CustomerFrequencyAvgCard.get(this, mCondition, mPeriod, mPeriodTime));
            }
        } else if (mCondition.hasFs) {
            mList.add(CustomerWaitDataCard.get(this, mCondition, mPeriod, mPeriodTime));
        } else {
            mList.add(CustomerNoDataCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(CustomerNoFsCard.get(this, mCondition, mPeriod, mPeriodTime));
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
    public void refresh(boolean force, boolean showLoading) {
        if (isConditionChanged && mCondition != null) {
            initList();
            load();
            isConditionChanged = false;
        } else if (force) {
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
        if (mCondition.hasCustomer) {
            // 更新到店频次卡片
            if (period == Constants.TIME_PERIOD_DAY) {
                ListIterator<BaseRefreshCard> it = mList.listIterator();
                while (it.hasNext()) {
                    BaseRefreshCard card = it.next();
                    if (card instanceof CustomerFrequencyTrendCard || card instanceof CustomerFrequencyAvgCard) {
                        it.remove();
                    }
                }
                if (isViewAttached()) {
                    mView.removeFrequencyCard();
                }
            } else {
                boolean hasFrequencyTrendCard = false;
                boolean hasFrequencyAvgCard = false;
                for (BaseRefreshCard card : mList) {
                    if (card instanceof CustomerFrequencyTrendCard) {
                        hasFrequencyTrendCard = true;
                    }
                    if (card instanceof CustomerFrequencyAvgCard) {
                        hasFrequencyAvgCard = true;
                    }
                }
                List<BaseRefreshCard> list = new ArrayList<>(2);
                if (!hasFrequencyTrendCard) {
                    list.add(CustomerFrequencyTrendCard.get(this, mCondition, mPeriod, mPeriodTime));
                }
                if (!hasFrequencyAvgCard) {
                    list.add(CustomerFrequencyAvgCard.get(this, mCondition, mPeriod, mPeriodTime));
                }
                mList.addAll(list);
                if (isViewAttached()) {
                    mView.addFrequencyCard(list);
                }
            }
        }
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
