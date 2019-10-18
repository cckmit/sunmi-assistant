package com.sunmi.assistant.dashboard.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
public class LineChartMarkerView extends MarkerView {

    private ImageView mIvPoint;
    private TextView mTvTitle;
    private TextView mTvValue;
    private TextView mTvLabel;

    private int mOffsetPoint;
    private MPPointF mOffset;
    private MPPointF mRealOffset;

    private String label;
    private int period;
    private int type;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     */
    public LineChartMarkerView(Context context) {
        super(context, R.layout.dashboard_chart_line_marker);
        mTvTitle = findViewById(R.id.tv_dashboard_marker_title);
        mTvValue = findViewById(R.id.tv_dashboard_marker_value);
        mTvLabel = findViewById(R.id.tv_dashboard_marker_label);
        mIvPoint = findViewById(R.id.iv_dashboard_marker_point);
        mOffsetPoint = mIvPoint.getWidth() / 2;
        label = context.getString(R.string.dashboard_card_marker_time);
    }

    public void setType(int period, int type) {
        this.period = period;
        this.type = type;
        switch (type) {
            case Constants.DATA_TYPE_RATE:
                mTvTitle.setText(R.string.dashboard_card_tab_rate);
                break;
            case Constants.DATA_TYPE_VOLUME:
                mTvTitle.setText(R.string.dashboard_card_tab_volume);
                break;
            case Constants.DATA_TYPE_CUSTOMER:
                mTvTitle.setText(R.string.dashboard_card_tab_customer);
                break;
            case Constants.DATA_TYPE_NEW_OLD:
                mTvTitle.setText(R.string.dashboard_card_tab_new_old);
                break;
            case Constants.DATA_TYPE_GENDER:
                mTvTitle.setText(R.string.dashboard_card_tab_gender);
                break;
            case Constants.DATA_TYPE_AGE:
                mTvTitle.setText(R.string.dashboard_card_tab_age);
                break;
            case Constants.DATA_TYPE_ALL:
                mTvTitle.setText(R.string.dashboard_card_tab_all);
                break;
            case Constants.DATA_TYPE_NEW:
                mTvTitle.setText(R.string.dashboard_card_tab_new);
                break;
            case Constants.DATA_TYPE_OLD:
                mTvTitle.setText(R.string.dashboard_card_tab_old);
                break;
            default:
        }
    }

    public void setPointColor(int color) {
        Drawable drawable = mIvPoint.getDrawable();
        GradientDrawable target = null;
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layer = (LayerDrawable) drawable;
            int count = layer.getNumberOfLayers();
            Drawable last = layer.getDrawable(Math.max(0, count - 1));
            if (last instanceof GradientDrawable) {
                target = (GradientDrawable) last;
            }
        } else if (drawable instanceof GradientDrawable) {
            target = (GradientDrawable) drawable;
        }
        if (target != null) {
            target.setColor(color);
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

        int pointSize = mOffsetPoint * 2;
        Rect r = new Rect(getWidth() / 2 - mOffsetPoint, getHeight() - pointSize,
                getWidth() / 2 + mOffsetPoint, getHeight());

        if (posX + mRealOffset.x < 0) {
            mRealOffset.x = -posX;
            r.left = Math.max((int) (posX - mOffsetPoint), 0);
            r.right = r.left + pointSize;
        } else if (chart != null && posX + width + mRealOffset.x > chart.getWidth()) {
            mRealOffset.x = chart.getWidth() - posX - width;
            r.right = Math.min((int) (-mRealOffset.x + mOffsetPoint), getWidth());
            r.left = r.right - pointSize;
        }

        if (posY + mRealOffset.y < 0) {
            mRealOffset.y = -mOffsetPoint;
            r.top = 0;
            r.bottom = pointSize;
        } else if (chart != null && posY + height + mRealOffset.y > chart.getHeight()) {
            mRealOffset.y = chart.getHeight() - posY - height;
        }

        mIvPoint.layout(r.left, r.top, r.right, r.bottom);

        return mRealOffset;
    }

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-((float) getWidth() / 2), -getHeight() + mOffsetPoint);
        } else {
            mOffset.x = -((float) getWidth() / 2);
        }
        return mOffset;
    }
}
