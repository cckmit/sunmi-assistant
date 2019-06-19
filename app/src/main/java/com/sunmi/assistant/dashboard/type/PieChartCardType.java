package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.PieChartCard;

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
        chart.getLegend().setEnabled(false);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<PieChartCard> holder, PieChartCard model, int position) {
        TextView title = holder.getView(R.id.tv_dashboard_title);
        PieChart chart = holder.getView(R.id.chart_dashboard_pie);
        title.setText(model.title);
        PieDataSet dataSet = new PieDataSet(model.dataSet.data, "aaaaaa");
        chart.setData(new PieData(dataSet));
    }
}
