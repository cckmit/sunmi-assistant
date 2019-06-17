package com.sunmi.assistant.dashboard.model;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * 图表数据集
 *
 * @author yinhui
 * @since 2019-06-14
 */
public class BarChartDataSet {
    public String xAxisLabel;
    public String yAxisLabel;
    public List<BarEntry> data;

    public BarChartDataSet(List<BarEntry> data, String xAxisLabel, String yAxisLabel) {
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.data = data;
    }
}
