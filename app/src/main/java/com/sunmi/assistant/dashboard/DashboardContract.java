package com.sunmi.assistant.dashboard;

import android.content.Context;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    int TIME_SPAN_INIT = 0;
    int TIME_SPAN_TODAY = 1;
    int TIME_SPAN_WEEK = 2;
    int TIME_SPAN_MONTH = 3;

    int DATA_MODE_SALES = 0;
    int DATA_MODE_ORDER = 1;

    interface View extends BaseView {

        Context getContext();

        void updateCompanyName(String companyName);

        void updateShopName(String shopName);

        void updateData(List<?> data);
    }

    interface Presenter {

        void loadConfig();

        void timeSpanSwitchTo(int timeSpan);
    }
}
