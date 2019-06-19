package com.sunmi.assistant.dashboard;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.BarChartDataSet;
import com.sunmi.assistant.dashboard.model.DashboardConfig;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.ListCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.dashboard.model.PieChartDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

        List<BarEntry> data = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            data.add(new BarEntry(i, (float) (Math.random() * 300)));
        }
        BarChartDataSet dataSet = new BarChartDataSet(data, "xxxxx", "yyyyy");
        testList.add(new BarChartCard("交易时间分布", dataSet));

        List<PieEntry> dataPie = new ArrayList<>();
        float total = 1.0f;
        for (int i = 0; i < 8; i++) {
            if (i == 7) {
                dataPie.add(new PieEntry(total, "Test Last: " + String.format(Locale.getDefault(), "%.0f%%", total * 100)));
            } else {
                float random = (float) Math.random() * total / 2;
                total -= random;
                dataPie.add(new PieEntry(random, "Test " + i + ": " + String.format(Locale.getDefault(), "%.0f%%", random * 100)));
            }
        }
        PieChartDataSet dataSetPie = new PieChartDataSet(dataPie);
        testList.add(new PieChartCard("支付方式", dataSetPie));

        List<ListCard.Item> list = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            counts.add((int) (Math.random() * 100000));
        }
        Collections.sort(counts, (o1, o2) -> o2 - o1);
        for (int i = 0; i < 5; i++) {
            list.add(new ListCard.Item(i + 1, "TestName" + i, counts.get(i)));
        }
        testList.add(new ListCard("商品销量排行", list));


        mCurrentTimeSpan = timeSpan;
        mView.updateData(testList);
    }
}
