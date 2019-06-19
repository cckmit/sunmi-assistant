package com.sunmi.assistant.dashboard.type;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.ui.RoundEdgeBarChartRenderer;

import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class BarChartCardType extends ItemType<BarChartCard, BaseViewHolder<BarChartCard>> {

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
        xAxis.setValueFormatter(new BarXAxisLabelFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setTextSize(10f);
        yAxis.setTextColor(Color.parseColor("#333338"));
        yAxis.setGridColor(Color.parseColor("#1A000000"));
        yAxis.setAxisMinimum(0.0f);
        yAxis.enableGridDashedLine(dashLength, dashSpaceLength, 0f);
        yAxis.setGridLineWidth(1f);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<BarChartCard> holder, BarChartCard model, int position) {
        TextView title = holder.getView(R.id.tv_dashboard_title);
        BarChart chart = holder.getView(R.id.chart_dashboard_bar);
        title.setText(model.title);
        BarDataSet dataSet = new BarDataSet(model.dataSet.data, "data");
        dataSet.setColor(Color.parseColor("#2997FF"));
        dataSet.setDrawValues(false);
        BarData data = new BarData(dataSet);
        chart.animateY(300, Easing.EaseOutCubic);
        chart.setData(data);
    }

    public static class BarXAxisLabelFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.format(Locale.getDefault(), "%02.0f:00", value);
        }
    }
}
