package com.sunmi.assistant.dashboard.customer;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.card.CustomerAnalysisCard;
import com.sunmi.assistant.dashboard.card.CustomerDataCard;
import com.sunmi.assistant.dashboard.card.CustomerNoDataCard;
import com.sunmi.assistant.dashboard.card.CustomerNoFsCard;
import com.sunmi.assistant.dashboard.card.CustomerPeriodCard;
import com.sunmi.assistant.dashboard.card.CustomerTrendCard;
import com.sunmi.assistant.dashboard.card.CustomerWaitDataCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


public class CustomerPresenter extends BasePresenter<CustomerContract.View>
        implements CustomerContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = CustomerPresenter.class.getSimpleName();

    private int mCompanyId;
    private int mShopId;
    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    public CustomerPresenter() {
        LogCat.d(TAG, "Create OverviewPresenter");
    }

    @Override
    public int getType() {
        return Constants.PAGE_CUSTOMER;
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
        setPeriod(Constants.TIME_PERIOD_YESTERDAY);
    }

    @Override
    public void setSource(int source) {
        boolean needReload = mSource != source
                || mCompanyId != SpUtils.getCompanyId()
                || mShopId != SpUtils.getShopId();
        if (mSource != source) {
            mSource = source;
            initList(mSource);
        }
        if (needReload) {
            load();
        }
    }

    @Override
    public void setPeriod(int period) {
        LogCat.d(TAG, "Set period: " + period + "; List=" + mList.size());
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
        if (Utils.hasCustomer(source)) {
            mList.add(CustomerPeriodCard.get(this, source));
            mList.add(CustomerDataCard.get(this, source));
            mList.add(CustomerTrendCard.get(this, source));
            mList.add(CustomerAnalysisCard.get(this, source));
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
