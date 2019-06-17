package com.sunmi.assistant.dashboard;

import com.sunmi.assistant.dashboard.model.DashboardConfig;
import com.sunmi.assistant.dashboard.model.DataCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private int mCurrentTimeSpan = DashboardContract.TIME_SPAN_TODAY;

    @Override
    public void loadConfig() {
        mView.updateInfo(new DashboardConfig("商米科技", "地球分公司"));
    }

    @Override
    public void timeSpanSwitchTo(int timeSpan) {
        List<Object> testList = new ArrayList<>();
        testList.add(new DataCard("总销售额", "6,342.00", "日环比", -0.12f));
        testList.add(new DataCard("客单价", "42.00", "日环比", 0.09f));
        testList.add(new DataCard("总笔数", "123", "日环比", 0.1f));
        testList.add(new DataCard("退款笔数", "3", "日环比", -0.5f));

        mCurrentTimeSpan = timeSpan;
        mView.updateData(testList);
    }
}
