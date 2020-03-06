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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;


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
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private boolean isConditionChanged = true;

    CustomerPresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_CUSTOMER;
    }

    private void initList() {
        mList.clear();
        mList.add(CustomerPeriodCard.get(this, mCondition));
        if (mCondition.hasCustomer) {
            mList.add(CustomerOverviewCard.get(this, mCondition));
            mList.add(CustomerTrendCard.get(this, mCondition));
            mList.add(CustomerEnterRateCard.get(this, mCondition));
            mList.add(CustomerFrequencyDistributionCard.get(this, mCondition));
        } else if (mCondition.hasFs) {
            mList.add(CustomerWaitDataCard.get(this, mCondition));
        } else {
            mList.add(CustomerNoDataCard.get(this, mCondition));
            mList.add(CustomerNoFsCard.get(this, mCondition));
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
        setPeriod(Constants.TIME_PERIOD_YESTERDAY);
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
        if (mPeriod != period && mCondition.hasCustomer
                && mPeriod == Constants.TIME_PERIOD_YESTERDAY) {
            // 从昨日变为本周或本月，增加卡片
            List<BaseRefreshCard> list = new ArrayList<>(2);
            list.add(CustomerFrequencyTrendCard.get(this, mCondition));
            list.add(CustomerFrequencyAvgCard.get(this, mCondition));
            for (BaseRefreshCard card : list) {
                card.init(mView.getContext());
            }
            mList.addAll(list);
            mView.addFrequencyCard(list);
        } else if (mPeriod != period && mCondition.hasCustomer
                && mPeriod != Constants.TIME_PERIOD_INIT
                && period == Constants.TIME_PERIOD_YESTERDAY) {
            // 从本周本月变为昨日，删除卡片
            mList.remove(mList.size() - 1);
            mList.remove(mList.size() - 1);
            mView.removeFrequencyCard();
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
