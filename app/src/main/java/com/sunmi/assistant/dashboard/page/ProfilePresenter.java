package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.shop.ProfileAnalysisCard;
import com.sunmi.assistant.dashboard.card.shop.ProfileNoDataCard;
import com.sunmi.assistant.dashboard.card.shop.ProfileNoFsCard;
import com.sunmi.assistant.dashboard.card.shop.ProfileOverviewCard;
import com.sunmi.assistant.dashboard.card.shop.ProfilePeriodCard;
import com.sunmi.assistant.dashboard.card.shop.ProfileWaitDataCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.Interval;
import sunmi.common.utils.log.LogCat;


/**
 * @author yinhui
 * @date 2019-10-14
 */
public class ProfilePresenter extends BasePresenter<ProfileContract.View>
        implements ProfileContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private PageContract.ParentPresenter mParent;
    private List<BaseRefreshCard> mList = new ArrayList<>();

    private DashboardCondition mCondition;
    private int mPeriod;
    private Interval mPeriodTime;

    private boolean isConditionChanged = true;

    ProfilePresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_PROFILE;
    }

    private void initList() {
        mList.clear();
        mList.add(ProfilePeriodCard.get(this, mCondition, mPeriod, mPeriodTime));
        if (mCondition.hasCustomer) {
            mList.add(ProfileOverviewCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(ProfileAnalysisCard.get(this, mCondition, mPeriod, mPeriodTime));
        } else if (mCondition.hasFs) {
            mList.add(ProfileWaitDataCard.get(this, mCondition, mPeriod, mPeriodTime));
        } else {
            mList.add(ProfileNoDataCard.get(this, mCondition, mPeriod, mPeriodTime));
            mList.add(ProfileNoFsCard.get(this, mCondition, mPeriod, mPeriodTime));
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
        for (BaseRefreshCard card : mList) {
            card.setPeriod(period, periodTime, true);
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
