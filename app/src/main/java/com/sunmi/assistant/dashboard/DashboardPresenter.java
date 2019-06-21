package com.sunmi.assistant.dashboard;

import android.util.Pair;

import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.model.DataCard;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

public class DashboardPresenter extends BasePresenter<DashboardContract.View>
        implements DashboardContract.Presenter {

    private int mTimeSpan;
    private List<BaseRefreshCard> mList;

    @Override
    public void loadConfig() {
        mView.updateInfo("商米科技", "地球分公司");
        mList = new ArrayList<>(7);
        mList.add(new DataCard("总销售额", "%.2f",
                new DataRefreshHelper.TotalSalesAmountRefresh(6885, 8314)));
        mView.updateData(mList);
    }

    @Override
    public void timeSpanSwitchTo(int timeSpan) {
        if (mTimeSpan == timeSpan) {
            return;
        }
        this.mTimeSpan = timeSpan;
        Pair<Long, Long> timeSpanPair = Utils.calcTimeSpan(timeSpan);
        for (BaseRefreshCard card : mList) {
            card.setTimeSpan(timeSpan, timeSpanPair);
        }
    }


}
