package com.sunmi.assistant.dashboard.ui.chart;

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
import com.sunmi.assistant.dashboard.card.CustomerTrendCard;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author yinhui
 * @date 2019-09-11
 */
public class CustomerLineMarkerView extends MarkerView {

    private String mNewName;
    private String mOldName;

    private ImageView mIvPoint;
    private TextView mTvTitle;
    private TextView mTvValue;
    private TextView mTvLabel1;
    private TextView mTvLabel2;

    private int mOffsetPoint;
    private MPPointF mOffset;
    private MPPointF mRealOffset;

    private int mPeriod;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     */
    public CustomerLineMarkerView(Context context) {
        super(context, R.layout.dashboard_chart_line_complex_marker);
        mNewName = context.getString(R.string.dashboard_card_tab_new) + ":";
        mOldName = context.getString(R.string.dashboard_card_tab_old) + ":";
        mTvTitle = findViewById(R.id.tv_dashboard_marker_title);
        mTvValue = findViewById(R.id.tv_dashboard_marker_value);
        mTvLabel1 = findViewById(R.id.tv_dashboard_marker_label1);
        mTvLabel2 = findViewById(R.id.tv_dashboard_marker_label2);
        mIvPoint = findViewById(R.id.iv_dashboard_marker_point);
        mOffsetPoint = mIvPoint.getWidth() / 2;
    }

    public void setPeriod(int period) {
        this.mPeriod = period;
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
        if (e instanceof CustomerTrendCard.CustomerEntry) {
            String title = "";
            CustomerTrendCard.CustomerEntry entry = (CustomerTrendCard.CustomerEntry) e;
            long time = entry.getTime();
            if (mPeriod == Constants.TIME_PERIOD_YESTERDAY) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(time);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                title = String.format(Locale.getDefault(), "%02d:00-%02d:00", hour, hour + 1);
            } else if (mPeriod == Constants.TIME_PERIOD_WEEK) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(time);
                int timeIndex = c.get(Calendar.DAY_OF_WEEK) - 1;
                title = Utils.formatDateTime("MM.dd", time) + " "
                        + Utils.getWeekName(getContext(), timeIndex);
            } else if (mPeriod == Constants.TIME_PERIOD_MONTH) {
                title = Utils.formatDateTime("MM.dd", time);
            }
            mTvTitle.setText(title);
            mTvValue.setText(String.valueOf((int) e.getY()));
            mTvLabel1.setText(String.format(Locale.getDefault(), "%s%d",
                    mNewName, entry.getNewCustomer()));
            mTvLabel2.setText(String.format(Locale.getDefault(), "%s%d",
                    mOldName, entry.getOldCustomer()));
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
