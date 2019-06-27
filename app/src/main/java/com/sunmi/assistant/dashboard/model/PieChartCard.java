package com.sunmi.assistant.dashboard.model;

import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.dashboard.DataRefreshHelper;

import java.util.List;

/**
 * 图表卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class PieChartCard extends BaseRefreshCard<PieChartCard> {
    public String title;
    public int dataSource;
    public PieChartDataSet[] dataSets = new PieChartDataSet[2];

    public PieChartCard(String title, int dataSource, DataRefreshHelper<PieChartCard> helper) {
        super(helper);
        this.title = title;
        this.dataSource = dataSource;
    }

    public static class PieChartDataSet {
        public List<PieEntry> data;

        public PieChartDataSet(List<PieEntry> data) {
            this.data = data;
        }
    }
}
