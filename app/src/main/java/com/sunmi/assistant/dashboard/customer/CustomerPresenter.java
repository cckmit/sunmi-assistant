package com.sunmi.assistant.dashboard.customer;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.PageContract;
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

    private PageContract.ParentPresenter mParent;
    private int mPageIndex;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    public CustomerPresenter(PageContract.ParentPresenter parent, int index) {
        this.mParent = parent;
        this.mPageIndex = index;
        LogCat.d("yinhui", "Create OverviewPresenter");
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
    public void setSource(int source, boolean showLoading) {
        boolean needReload = mSource != source
                || mCompanyId != SpUtils.getCompanyId()
                || mShopId != SpUtils.getShopId();
        if (mSource != source) {
            mSource = source;
            initList(mSource);
        }
        if (needReload) {
            load();
        } else {
            for (BaseRefreshCard card : mList) {
                card.refresh(showLoading);
            }
        }
    }

    @Override
    public void setPeriod(int period) {
        LogCat.d("yinhui", "Set period: " + period + "; List=" + mList.size() + "; Presenter=" + this);
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
        mParent.refresh(showLoading);
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
