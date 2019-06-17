package com.sunmi.assistant.dashboard.model;

/**
 * 图表卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class PieChartCard {
    public String title;
    public PieChartDataSet dataSet;

    public PieChartCard(String title, PieChartDataSet data) {
        this.title = title;
        this.dataSet = data;
    }
}
