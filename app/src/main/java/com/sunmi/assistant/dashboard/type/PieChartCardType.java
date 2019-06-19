package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.PieChartCard;

import java.util.ArrayList;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class PieChartCardType extends ItemType<PieChartCard, BaseViewHolder<PieChartCard>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_chart_pie;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }


    @NonNull
    @Override
    public BaseViewHolder<PieChartCard> onCreateViewHolder(
            @NonNull View view, @NonNull ItemType<PieChartCard, BaseViewHolder<PieChartCard>> type) {
        BaseViewHolder<PieChartCard> holder = new BaseViewHolder<>(view, type);
        PieChart chart = holder.getView(R.id.chart_dashboard_pie);

        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setUsePercentValues(true);
        chart.setTransparentCircleRadius(0f);
        chart.setExtraRightOffset(50);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(4f);
        l.setTextSize(12f);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<PieChartCard> holder, PieChartCard model, int position) {
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
        TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);
        PieChart chart = holder.getView(R.id.chart_dashboard_pie);
        title.setText(model.title);
        bySales.setSelected(true);
        byOrder.setSelected(false);

        PieDataSet dataSet;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (PieDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(model.dataSet.data);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new PieDataSet(model.dataSet.data, "");

            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.MATERIAL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            dataSet.setDrawValues(false);
            dataSet.setDrawIcons(false);
            PieData data = new PieData(dataSet);
            chart.setData(data);
        }
//        chart.animateY(300, Easing.EaseOutCubic);
    }

}
