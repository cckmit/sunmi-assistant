package com.sunmi.assistant.dashboard.model;

import java.util.ArrayList;

/**
 * 图表卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class BarChartCard {
    public String title;
    public ArrayList<String> dataSource;
    public int currentSource;
    public ChartDataset dataset;
}
