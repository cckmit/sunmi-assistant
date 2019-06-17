package com.sunmi.assistant.dashboard.model;

import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

/**
 * 图表数据集
 *
 * @author yinhui
 * @since 2019-06-14
 */
public class PieChartDataSet {
    public List<PieEntry> data;

    public PieChartDataSet(List<PieEntry> data) {
        this.data = data;
    }
}
