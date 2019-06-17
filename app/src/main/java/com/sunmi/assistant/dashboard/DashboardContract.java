package com.sunmi.assistant.dashboard;

import com.sunmi.assistant.dashboard.model.DashboardConfig;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    int TIME_SPAN_TODAY = 0;
    int TIME_SPAN_WEEK = 1;
    int TIME_SPAN_MONTH = 2;

    int DATA_MODE_SALES = 0;
    int DATA_MODE_ORDER = 1;

    interface View extends BaseView {

        void updateInfo(DashboardConfig config);

        void updateData(List<Object> data);
    }

    interface Presenter {

        void loadConfig();

        void timeSpanSwitchTo(int timeSpan);
    }
}
