package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.PieMarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.MPPointF;
import com.sunmi.assistant.R;

/**
 * @author yinhui
 * @date 2019-09-11
 */
public class PieChartMarkerView extends PieMarkerView {

    private LinearLayout mContent;
    private TextView mTvLabel;
    private TextView mTvValue;

    private boolean mIsLeft;
    private MPPointF mLeftOffset;
    private MPPointF mRightOffset;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context context
     */
    public PieChartMarkerView(Context context) {
        super(context, R.layout.dashboard_chart_pie_marker);
        mContent = findViewById(R.id.layout_dashboard_content);
        mTvLabel = findViewById(R.id.tv_dashboard_marker_label);
        mTvValue = findViewById(R.id.tv_dashboard_marker_value);
    }

    @Override
    public void refreshContent(Entry e, boolean isLeft) {
        PieEntry entry = (PieEntry) e;
        mIsLeft = isLeft;
        if (isLeft) {
            mContent.setGravity(Gravity.LEFT);
        } else {
            mContent.setGravity(Gravity.RIGHT);
        }
        mTvValue.setText(getContext().getString(R.string.str_num_people, (int) entry.getValue()));
        mTvLabel.setText(entry.getLabel());
        super.refreshContent(e, isLeft);
    }

    @Override
    public MPPointF getOffset() {
        if (mLeftOffset == null) {
            // center the marker horizontally and vertically
            mLeftOffset = new MPPointF(0, -getHeight() / 2);
        }
        if (mRightOffset == null) {
            // center the marker horizontally and vertically
            mRightOffset = new MPPointF(-getWidth(), -getHeight() / 2);
        } else {
            mRightOffset.x = -getWidth();
        }
        return mIsLeft ? mLeftOffset : mRightOffset;
    }
}
