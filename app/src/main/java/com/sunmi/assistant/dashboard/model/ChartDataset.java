package com.sunmi.assistant.dashboard.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据集
 *
 * @author yinhui
 * @since 2019-06-14
 */
public class ChartDataset {
    public int typeIndex;
    public String xAxisLabel;
    public String yAxisLabel;
    public List<Entry> data = new ArrayList<>();

    public ChartDataset(int typeIndex, String xAxisLabel, String yAxisLabel) {
        this.typeIndex = typeIndex;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
    }
}
