package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.card.ProfileAnalysisCard;
import com.sunmi.assistant.dashboard.card.ProfileNoDataCard;
import com.sunmi.assistant.dashboard.card.ProfileNoFsCard;
import com.sunmi.assistant.dashboard.card.ProfileOverviewCard;
import com.sunmi.assistant.dashboard.card.ProfilePeriodCard;
import com.sunmi.assistant.dashboard.card.ProfileWaitDataCard;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;


/**
 * @author yinhui
 * @date 2019-10-14
 */
public class ProfilePresenter extends BasePresenter<ProfileContract.View>
        implements ProfileContract.Presenter, BaseRefreshCard.Presenter {

    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private int mSource = -1;
    private int mPeriod = Constants.TIME_PERIOD_INIT;

    private PageContract.ParentPresenter mParent;

    private List<BaseRefreshCard> mList = new ArrayList<>();

    ProfilePresenter(PageContract.ParentPresenter parent) {
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
        return Constants.PAGE_PROFILE;
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
        mList.add(ProfilePeriodCard.get(this, source));
        if (Utils.hasCustomer(source)) {
            mList.add(ProfileOverviewCard.get(this, source));
            mList.add(ProfileAnalysisCard.get(this, source));
        } else if (Utils.hasFs(source)) {
            mList.add(ProfileWaitDataCard.get(this, source));
        } else {
            mList.add(ProfileNoDataCard.get(this, source));
            mList.add(ProfileNoFsCard.get(this, source));
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
