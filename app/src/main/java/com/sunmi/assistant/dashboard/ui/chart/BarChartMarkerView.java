package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.sunmi.assistant.R;

/**
 * @author yinhui
 * @date 2019-09-11
 */
public class BarChartMarkerView extends MarkerView {

    private TextView mTvTitle;
    private TextView mTvValue;
    private TextView mTvLabel;

    private MPPointF mOffset;
    private MPPointF mRealOffset;
    private float mGap;

    private IMarkerFormatter formatter;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context context
     */
    public BarChartMarkerView(Context context, IMarkerFormatter formatter) {
        super(context, R.layout.dashboard_chart_bar_marker);
        this.formatter = formatter;

        mTvTitle = findViewById(R.id.tv_dashboard_marker_title);
        mTvValue = findViewById(R.id.tv_dashboard_marker_value);
        mTvLabel = findViewById(R.id.tv_dashboard_marker_label);
        mGap = getResources().getDimension(R.dimen.dp_4);
    }

    public void setTitle(@StringRes int titleId) {
        this.mTvTitle.setText(titleId);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mTvValue.setText(formatter.valueFormat(e.getY()));
        if (e instanceof ChartEntry) {
            ChartEntry entry = (ChartEntry) e;
            mTvLabel.setText(formatter.timeFormat(entry.getTime()));
        } else {
            mTvLabel.setText(formatter.xAxisFormat(e.getX()));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        if (mRealOffset == null) {
            mRealOffset = new MPPointF();
        }
        MPPointF offset = getOffset();
        mRealOffset.x = offset.x;
        mRealOffset.y = offset.y;

        Chart chart = getChartView();

        float width = getWidth();
        float height = getHeight();

        if (posX + mRealOffset.x < 0) {
            mRealOffset.x = -posX;
        } else if (chart != null && posX + width + mRealOffset.x > chart.getWidth()) {
            mRealOffset.x = chart.getWidth() - posX - width;
        }

        if (posY + mRealOffset.y < 0) {
            mRealOffset.y = mGap;
        } else if (chart != null && posY + height + mRealOffset.y > chart.getHeight()) {
            mRealOffset.y = chart.getHeight() - posY - height;
        }

        return mRealOffset;
    }

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-((float) getWidth() / 2), -getHeight() - mGap);
        } else {
            mOffset.x = -((float) getWidth() / 2);
        }
        return mOffset;
    }
}
