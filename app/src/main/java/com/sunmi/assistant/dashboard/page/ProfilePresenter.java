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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BasePresenter;


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
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private boolean isConditionChanged = true;

    ProfilePresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_PROFILE;
    }

    private void initList() {
        mList.clear();
        mList.add(ProfilePeriodCard.get(this, mCondition));
        if (mCondition.hasCustomer) {
            mList.add(ProfileOverviewCard.get(this, mCondition));
            mList.add(ProfileAnalysisCard.get(this, mCondition));
        } else if (mCondition.hasFs) {
            mList.add(ProfileWaitDataCard.get(this, mCondition));
        } else {
            mList.add(ProfileNoDataCard.get(this, mCondition));
            mList.add(ProfileNoFsCard.get(this, mCondition));
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
