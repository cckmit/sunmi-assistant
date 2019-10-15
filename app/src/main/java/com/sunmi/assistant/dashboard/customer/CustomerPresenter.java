package com.sunmi.assistant.dashboard.customer;

import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.card.CustomerDataCard;
import com.sunmi.assistant.dashboard.card.CustomerPeriodCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.SpUtils;


public class CustomerPresenter extends BasePresenter<CustomerContract.View>
        implements CustomerContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = CustomerPresenter.class.getSimpleName();

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
        setPeriod(Constants.TIME_PERIOD_YESTERDAY);
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
        if (Utils.hasCustomer(source)) {
            mList.add(CustomerPeriodCard.init(this, source));
            mList.add(CustomerDataCard.init(this, source));
        } else if (Utils.hasFs(source)) {

        } else {

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
