package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.CustomerEnterRateCard;
import com.sunmi.assistant.dashboard.card.CustomerFrequencyAvgCard;
import com.sunmi.assistant.dashboard.card.CustomerFrequencyDistributionCard;
import com.sunmi.assistant.dashboard.card.CustomerFrequencyTrendCard;
import com.sunmi.assistant.dashboard.card.CustomerNoDataCard;
import com.sunmi.assistant.dashboard.card.CustomerNoFsCard;
import com.sunmi.assistant.dashboard.card.CustomerOverviewCard;
import com.sunmi.assistant.dashboard.card.CustomerPeriodCard;
import com.sunmi.assistant.dashboard.card.CustomerTrendCard;
import com.sunmi.assistant.dashboard.card.CustomerWaitDataCard;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;


/**
 * @author yinhui
 * @date 2019-10-14
 */
public class CustomerPresenter extends BasePresenter<CustomerContract.View>
        implements CustomerContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = CustomerPresenter.class.getSimpleName();

    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private PageContract.ParentPresenter mParent;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    CustomerPresenter(PageContract.ParentPresenter parent) {
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
        setPeriod(Constants.TIME_PERIOD_YESTERDAY);
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
        if (mPeriod != period && Utils.hasCustomer(mSource)
                && mPeriod == Constants.TIME_PERIOD_YESTERDAY) {
            // 从昨日变为本周或本月，增加卡片
            List<BaseRefreshCard> list = new ArrayList<>(2);
            list.add(CustomerFrequencyTrendCard.get(this, mSource));
            list.add(CustomerFrequencyAvgCard.get(this, mSource));
            for (BaseRefreshCard card : list) {
                card.init(mView.getContext());
            }
            mList.addAll(list);
            mView.addFrequencyCard(list);
        } else if (mPeriod != period && Utils.hasCustomer(mSource)
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
        return Constants.PAGE_CUSTOMER;
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
        mList.add(CustomerPeriodCard.get(this, source));
        if (Utils.hasCustomer(source)) {
            mList.add(CustomerOverviewCard.get(this, source));
            mList.add(CustomerTrendCard.get(this, source));
            mList.add(CustomerEnterRateCard.get(this, source));
            mList.add(CustomerFrequencyDistributionCard.get(this, source));
        } else if (Utils.hasFs(source)) {
            mList.add(CustomerWaitDataCard.get(this, source));
        } else {
            mList.add(CustomerNoDataCard.get(this, source));
            mList.add(CustomerNoFsCard.get(this, source));
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
