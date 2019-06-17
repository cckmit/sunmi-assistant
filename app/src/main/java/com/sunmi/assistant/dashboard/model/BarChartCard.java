package com.sunmi.assistant.dashboard.model;

/**
 * 图表卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class BarChartCard {
    public String title;
    public BarChartDataSet dataSet;

    public BarChartCard(String title, BarChartDataSet data) {
        this.title = title;
        this.dataSet = data;
    }
}
