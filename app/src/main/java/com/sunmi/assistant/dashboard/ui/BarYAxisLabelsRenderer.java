package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;

/**
 * @author yinhui
 * @since 2019-07-03
 */
public class BarYAxisLabelsRenderer extends YAxisRenderer {

    private float[] labels;

    public BarYAxisLabelsRenderer(BarLineChartBase chart) {
        super(chart.getViewPortHandler(), chart.getAxisLeft(), chart.getTransformer(YAxis.AxisDependency.LEFT));
    }

    public float setMaxValue(float max) {
        if (labels == null) {
            labels = new float[6];
        }
        if (max <= 5) {
            for (int i = 0; i < 6; i++) {
                labels[i] = i;
            }
            return 5;
        } else if (max <= 10) {
            for (int i = 0; i < 6; i++) {
                labels[i] = i * 2;
            }
            return 10;
        } else {
            double offset = Math.ceil(max / 25);
            for (int i = 0; i < 6; i++) {
                labels[i] = (float) (i * offset * 5);
            }
            return (float) (offset * 25);
        }
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
