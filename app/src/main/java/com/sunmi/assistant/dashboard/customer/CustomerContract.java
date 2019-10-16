package com.sunmi.assistant.dashboard.customer;

import android.content.Context;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.List;

import sunmi.common.base.BaseView;

public interface CustomerContract {

    interface View extends BaseView {

        Context getContext();

        void updateTab(int period);

        void setCards(List<BaseRefreshCard> data);

        int getScrollY();

        void scrollTo(int y);
    }

    interface Presenter extends DashboardContract.PagePresenter {

        void load();

    }
}
