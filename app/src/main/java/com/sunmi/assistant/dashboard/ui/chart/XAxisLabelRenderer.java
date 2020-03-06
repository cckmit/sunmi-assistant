package com.sunmi.assistant.dashboard.ui.chart;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.sunmi.assistant.dashboard.util.Constants;

/**
 * @author yinhui
 * @since 2019-07-03
 */
public class XAxisLabelRenderer extends XAxisRenderer {

    private float[] labels;
    private boolean adjustLabelCountToChartWidth;

    public XAxisLabelRenderer(BarLineChartBase chart) {
        super(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT));
    }

    public void setPeriod(int period, int maxDay) {
        if (period == Constants.TIME_PERIOD_DAY) {
            labels = new float[]{1, 5, 9, 13, 17, 21, 25};
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            labels = new float[]{101, 102, 103, 104, 105, 106, 107};
        } else {
            labels = new float[]{10001, 10006, 10012, 10018, 10024, 10000 + maxDay};
        }
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        if (labels == null) {
            super.computeAxisValues(min, max);
            return;
        }
        mAxis.mEntryCount = labels.length;
        mAxis.mEntries = labels;
        mAxis.setCenterAxisLabels(false);

        computeSize();
//        if (adjustLabelCountToChartWidth) {
//            int width = mXAxis.mLabelRotatedWidth;
//
//            while (width * mAxis.mEntryCount > mViewPortHandler.getChartWidth() / 2f) {
//                float[] entries = mAxis.mEntries;
//                int length = entries.length;
//                float[] newLabels = new float[length % 2 == 0 ? length / 2 : length / 2 + 1];
//                for (int i = 0; i < length; i++) {
//                    if (i % 2 == 0) {
//                        newLabels[i / 2] = entries[i];
//                    }
//                }
//                mAxis.mEntries = newLabels;
//                mAxis.mEntryCount = mAxis.mEntries.length;
//            }
//        }
    }
}
