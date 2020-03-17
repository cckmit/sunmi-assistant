package com.sunmi.assistant.dashboard.ui.chart;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;

/**
 * @author yinhui
 * @since 2019-07-03
 */
public class YAxisVolumeLabelsRenderer extends YAxisRenderer {

    private float[] labels;

    public YAxisVolumeLabelsRenderer(BarLineChartBase chart) {
        super(chart.getViewPortHandler(), chart.getAxisLeft(), chart.getTransformer(YAxis.AxisDependency.LEFT));
    }

    public float setMaxValue(float max) {
        if (labels == null) {
            labels = new float[6];
        }
        float fragment = max / 5;
        if (fragment <= 1) {
            for (int i = 0; i < 6; i++) {
                labels[i] = i;
            }
            return 5.1f;
        } else {
            int index = (int) Math.pow(10, (int) Math.log10(fragment));
            float result = (float) (Math.ceil(fragment / index) * index);
            for (int i = 0; i < 6; i++) {
                labels[i] = result * i;
            }
            return result * 5 + 0.1f;
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
