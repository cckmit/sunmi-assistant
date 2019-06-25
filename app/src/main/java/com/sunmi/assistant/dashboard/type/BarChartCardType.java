package com.sunmi.assistant.dashboard.type;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DataRefreshCallback;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.ui.ChartDataChangeAnimation;

import java.util.List;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class BarChartCardType extends ItemType<BarChartCard, BaseViewHolder<BarChartCard>> {

    private static final String TAG = "BarChartCardType";

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_chart_bar;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<BarChartCard> onCreateViewHolder(
            @NonNull View view, @NonNull ItemType<BarChartCard, BaseViewHolder<BarChartCard>> type) {
        BaseViewHolder<BarChartCard> holder = new BaseViewHolder<>(view, type);
        BarChart chart = holder.getView(R.id.chart_dashboard_bar);
        Context context = view.getContext();
        float dashLength = CommonHelper.dp2px(context, 4f);
        float dashSpaceLength = CommonHelper.dp2px(context, 2f);

        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        RoundEdgeBarChartRenderer renderer = new RoundEdgeBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler());
        renderer.setRadius(20);
        chart.setFitBars(true);
        chart.setRenderer(renderer);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#333338"));
        xAxis.setValueFormatter(new BarXAxisLabelFormatter(holder.getContext()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1f);
        yAxis.setTextSize(10f);
        yAxis.setTextColor(Color.parseColor("#333338"));
        yAxis.setGridColor(Color.parseColor("#1A000000"));
        yAxis.setAxisMinimum(0f);
        yAxis.enableGridDashedLine(dashLength, dashSpaceLength, 0f);
        yAxis.setGridLineWidth(1f);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<BarChartCard> holder, BarChartCard model, int position) {
        model.setCallback(new DataRefreshCallback() {
            @Override
            public void onSuccess() {
                setupView(holder, model, position);
            }

            @Override
            public void onFail() {
            }
        });
        setupView(holder, model, position);
    }

    private void setupView(BaseViewHolder<BarChartCard> holder, BarChartCard model, int position) {
        TextView title = holder.getView(R.id.tv_dashboard_title);
        BarChart chart = holder.getView(R.id.chart_dashboard_bar);
        TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
        TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);
        title.setText(model.title);
        bySales.setSelected(model.dataSource == 0);
        byOrder.setSelected(model.dataSource == 1);

        if (model.flag == BaseRefreshCard.FLAG_INIT) {
            Log.d(TAG, "Card data setup view skip.");
            return;
        }

        BarChartCard.BarChartDataSet modelDataSet = model.dataSets[model.dataSource];
        if (modelDataSet == null || modelDataSet.data == null || modelDataSet.data.size() == 0) {
            chart.setData(null);
            chart.invalidate();
            return;
        }

        // Calculate min & max of Y-Axis value.
        List<BarEntry> dataList = modelDataSet.data;
        float max = 0;
        for (BarEntry entry : dataList) {
            if (entry.getY() > max) {
                max = entry.getY();
            }
        }
        chart.getAxisLeft().setAxisMaximum(max > 0 ? max * 1.2f : 5f);

        // Set the radius of bar chart based on the time span.
        RoundEdgeBarChartRenderer renderer = (RoundEdgeBarChartRenderer) chart.getRenderer();
        if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            renderer.setRadius(16);
        } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            renderer.setRadius(68);
        } else {
            renderer.setRadius(20);
        }

        BarDataSet dataSet;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
//            dataSet.setValues(dataList);
//            chart.getData().notifyDataChanged();
//            chart.notifyDataSetChanged();
//            chart.invalidate();
            BarChartDataUpdateAnim anim = new BarChartDataUpdateAnim(300, chart,
                    dataSet.getValues(), modelDataSet.data);
            anim.run();
        } else {
            dataSet = new BarDataSet(dataList, "data");
            dataSet.setColor(Color.parseColor("#2997FF"));
            dataSet.setDrawValues(false);
            BarData data = new BarData(dataSet);
            chart.animateY(300, Easing.EaseOutCubic);
            chart.setData(data);
            chart.invalidate();
        }
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
    }

    public static class BarXAxisLabelFormatter extends ValueFormatter {

        private static String[] sWeekName;

        BarXAxisLabelFormatter(Context context) {
            sWeekName = context.getResources().getStringArray(R.array.dashboard_week_name);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return Utils.decodeBarChartXAxisFloat(value, sWeekName);
        }
    }

    public static class RoundEdgeBarChartRenderer extends BarChartRenderer {
        private RectF mBarShadowRectBuffer = new RectF();

        private int mRadius;

        RoundEdgeBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);
        }

        void setRadius(int radius) {
            this.mRadius = radius;
        }

        protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            mBarBorderPaint.setColor(dataSet.getBarBorderColor());
            mBarBorderPaint.setStrokeWidth(com.github.mikephil.charting.utils.Utils
                    .convertDpToPixel(dataSet.getBarBorderWidth()));
            mShadowPaint.setColor(dataSet.getBarShadowColor());
            boolean drawBorder = dataSet.getBarBorderWidth() > 0f;

            float phaseX = mAnimator.getPhaseX();
            float phaseY = mAnimator.getPhaseY();

            if (mChart.isDrawBarShadowEnabled()) {
                mShadowPaint.setColor(dataSet.getBarShadowColor());

                BarData barData = mChart.getBarData();

                float barWidth = barData.getBarWidth();
                float barWidthHalf = barWidth / 2.0f;
                float x;

                int i = 0;
                double count = Math.min(Math.ceil((int) (double) ((float) dataSet.getEntryCount() * phaseX)), dataSet.getEntryCount());
                while (i < count) {

                    BarEntry e = dataSet.getEntryForIndex(i);

                    x = e.getX();

                    mBarShadowRectBuffer.left = x - barWidthHalf;
                    mBarShadowRectBuffer.right = x + barWidthHalf;

                    trans.rectValueToPixel(mBarShadowRectBuffer);

                    if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                        i++;
                        continue;
                    }

                    if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                        break;

                    mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                    mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                    c.drawRoundRect(mBarRect, mRadius, mRadius, mShadowPaint);
                    i++;
                }
            }

            // initialize the buffer
            BarBuffer buffer = mBarBuffers[index];
            buffer.setPhases(phaseX, phaseY);
            buffer.setDataSet(index);
            buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
            buffer.setBarWidth(mChart.getBarData().getBarWidth());

            buffer.feed(dataSet);

            trans.pointValuesToPixel(buffer.buffer);

            boolean isSingleColor = dataSet.getColors().size() == 1;

            if (isSingleColor) {
                mRenderPaint.setColor(dataSet.getColor());
            }

            int j = 0;
            while (j < buffer.size()) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4;
                    continue;
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (!isSingleColor) {
                    // Set the color for the currently drawn value. If the index
                    // is out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getColor(j / 4));
                }

                if (dataSet.getGradientColor() != null) {
                    GradientColor gradientColor = dataSet.getGradientColor();
                    mRenderPaint.setShader(new LinearGradient(
                            buffer.buffer[j],
                            buffer.buffer[j + 3],
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            gradientColor.getStartColor(),
                            gradientColor.getEndColor(),
                            android.graphics.Shader.TileMode.MIRROR));
                }

                if (dataSet.getGradientColors() != null) {
                    mRenderPaint.setShader(new LinearGradient(
                            buffer.buffer[j],
                            buffer.buffer[j + 3],
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            dataSet.getGradientColor(j / 4).getStartColor(),
                            dataSet.getGradientColor(j / 4).getEndColor(),
                            Shader.TileMode.MIRROR));
                }
                Path path2 = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]), mRadius, mRadius, true, true, false, false);
                c.drawPath(path2, mRenderPaint);
                if (drawBorder) {
                    Path path = roundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, true, true, false, false);
                    c.drawPath(path, mBarBorderPaint);
                }
                j += 4;
            }
        }

        private Path roundRect(RectF rect, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
            float top = rect.top;
            float left = rect.left;
            float right = rect.right;
            float bottom = rect.bottom;
            Path path = new Path();
            if (rx < 0) rx = 0;
            if (ry < 0) ry = 0;
            float width = right - left;
            float height = bottom - top;
            if (rx > width / 2) rx = width / 2;
            if (ry > height / 2) ry = height / 2;
            float widthMinusCorners = (width - (2 * rx));
            float heightMinusCorners = (height - (2 * ry));

            path.moveTo(right, top + ry);
            if (tr)
                path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
            else {
                path.rLineTo(0, -ry);
                path.rLineTo(-rx, 0);
            }
            path.rLineTo(-widthMinusCorners, 0);
            if (tl)
                path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
            else {
                path.rLineTo(-rx, 0);
                path.rLineTo(0, ry);
            }
            path.rLineTo(0, heightMinusCorners);

            if (bl)
                path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            else {
                path.rLineTo(0, ry);
                path.rLineTo(rx, 0);
            }

            path.rLineTo(widthMinusCorners, 0);
            if (br)
                path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
            else {
                path.rLineTo(rx, 0);
                path.rLineTo(0, -ry);
            }

            path.rLineTo(0, -heightMinusCorners);

            path.close();//Given close, last lineto can be removed.

            return path;
        }
    }

    private static class BarChartDataUpdateAnim extends ChartDataChangeAnimation<BarEntry, BarData> {

        private BarChartDataUpdateAnim(int duration, Chart<BarData> chart,
                                       List<BarEntry> oldData, List<BarEntry> newData) {
            super(duration, chart, oldData, newData);
        }

        @Override
        public BarEntry newEntry(BarEntry entry, float newValue) {
            return new BarEntry(entry.getX(), newValue);
        }
    }
}
