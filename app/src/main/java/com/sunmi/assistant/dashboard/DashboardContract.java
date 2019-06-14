package com.sunmi.assistant.dashboard;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    int TIME_SPAN_TODAY = 0;
    int TIME_SPAN_WEEK = 1;
    int TIME_SPAN_MONTH = 2;

    interface View extends BaseView {

    }

    interface Presenter {

        void loadConfig();

        void loadDashboardData(int timeSpan);
    }
}
