package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.card.BaseRefreshCard;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    int TIME_PERIOD_INIT = 0;
    int TIME_PERIOD_TODAY = 1;
    int TIME_PERIOD_WEEK = 2;
    int TIME_PERIOD_MONTH = 3;

    int DATA_MODE_SALES = 0;
    int DATA_MODE_ORDER = 1;

    interface View extends BaseView {

        Context getContext();

        void initData(List<BaseRefreshCard> data);
    }

    interface Presenter {

        void loadConfig();

        void switchPeriodTo(int period);

        void switchShopTo(int shopId);

        void refresh();

    }
}
