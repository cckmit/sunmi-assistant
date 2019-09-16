package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;

/**
 * @author yinhui
 * @since 2019-07-03
 */
public class LineYAxisLabelsRenderer extends YAxisRenderer {

    private float[] labels = {0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f};

    public LineYAxisLabelsRenderer(BarLineChartBase chart) {
        super(chart.getViewPortHandler(), chart.getAxisLeft(), chart.getTransformer(YAxis.AxisDependency.LEFT));
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        if (labels == null) {
            super.computeAxisValues(min, max);
        } else {
            mAxis.mEntryCount = labels.length;
            mAxis.mEntries = labels;
            mAxis.setCenterAxisLabels(false);
        }
    }
}
