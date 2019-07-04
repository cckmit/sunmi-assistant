package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;

/**
 * @author yinhui
 * @since 2019-07-03
 */
public class SpecificLabelsXAxisRenderer extends XAxisRenderer {

    private float[] labels;
    private boolean adjustLabelCountToChartWidth;

    public SpecificLabelsXAxisRenderer(BarChart chart, float[] labels, boolean adjustLabelCountToChartWidth) {
        super(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT));
        this.labels = labels;
        this.adjustLabelCountToChartWidth = adjustLabelCountToChartWidth;
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        mAxis.mEntryCount = labels.length;
        mAxis.mEntries = labels;
        mAxis.setCenterAxisLabels(false);

        computeSize();

        if (adjustLabelCountToChartWidth) {
            int width = mXAxis.mLabelRotatedWidth;

            while (width * mAxis.mEntryCount > mViewPortHandler.getChartWidth() / 2f) {
                float[] entries = mAxis.mEntries;
                int length = entries.length;
                float[] newLabels = new float[length % 2 == 0 ? length / 2 : length / 2 + 1];
                for (int i = 0; i < length; i++) {
                    if (i % 2 == 0) {
                        newLabels[i / 2] = entries[i];
                    }
                }
                mAxis.mEntries = newLabels;
                mAxis.mEntryCount = mAxis.mEntries.length;
            }
        }
    }
}
