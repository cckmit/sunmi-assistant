package com.sunmi.assistant.dashboard.ui;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;

import java.util.Locale;

/**
 * @author yinhui
 * @date 2019-09-11
 */
public class BarChartMarkerView extends MarkerView {

    private ImageView mIvPoint;
    private TextView mTvTitle;
    private TextView mTvValue;
    private TextView mTvLabel;

    private MPPointF mOffset;
    private MPPointF mRealOffset;
    private float mGap;

    private String label;
    private int period;
    private int type;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context context
     */
    public BarChartMarkerView(Context context) {
        super(context, R.layout.dashboard_chart_bar_marker);
        mTvTitle = findViewById(R.id.tv_dashboard_marker_title);
        mTvValue = findViewById(R.id.tv_dashboard_marker_value);
        mTvLabel = findViewById(R.id.tv_dashboard_marker_label);
        mGap = getResources().getDimension(R.dimen.dp_4);
        label = context.getString(R.string.dashboard_card_marker_time);
    }

    public void setType(int period, int type) {
        this.period = period;
        this.type = type;
        if (type == Constants.DATA_TYPE_RATE) {
            mTvTitle.setText(R.string.dashboard_card_tab_rate);
        } else if (type == Constants.DATA_TYPE_VOLUME) {
            mTvTitle.setText(R.string.dashboard_card_tab_volume);
        } else {
            mTvTitle.setText(R.string.dashboard_card_tab_customer);
        }
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof ChartEntry) {
            if (type == Constants.DATA_TYPE_RATE) {
                mTvValue.setText(String.format(Locale.getDefault(), "%.2f%%", e.getY() * 100));
            } else {
                mTvValue.setText(String.valueOf((int) e.getY()));
            }
            mTvLabel.setText(String.format("%s %s", label,
                    Utils.convertXToMarkerName(getContext(), period, ((ChartEntry) e).getTime())));
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
