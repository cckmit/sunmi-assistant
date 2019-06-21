package com.sunmi.assistant.dashboard.model;

import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.dashboard.DataRefreshHelper;

import java.util.List;

/**
 * 图表卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class BarChartCard extends BaseRefreshCard<BarChartCard> {
    public String title;
    public int dataSource;
    public BarChartDataSet dataSet;

    public BarChartCard(String title, int dataSource, DataRefreshHelper<BarChartCard> helper) {
        super(helper);
        this.title = title;
        this.dataSource = dataSource;
    }

    public static class BarChartDataSet {
        public String xAxisLabel;
        public String yAxisLabel;
        public List<BarEntry> data;

        public BarChartDataSet(List<BarEntry> data, String xAxisLabel, String yAxisLabel) {
            this.xAxisLabel = xAxisLabel;
            this.yAxisLabel = yAxisLabel;
            this.data = data;
        }
    }
}
