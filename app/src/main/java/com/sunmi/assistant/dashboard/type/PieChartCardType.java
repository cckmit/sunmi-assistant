package com.sunmi.assistant.dashboard.type;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.dashboard.ui.ChartDataChangeAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class PieChartCardType extends ItemType<PieChartCard, BaseViewHolder<PieChartCard>> {

    private static final String HOLDER_TAG_LEGENDS = "legends";
    private static final String HOLDER_TAG_LEGENDS_DATA = "legends_data";

    private static final Integer[] PIE_COLORS = {
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"), Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.rgb(51, 181, 229)
    };

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
        chart.getLegend().setEnabled(false);

        List<TextView> legends = new ArrayList<>(6);
        legends.add(holder.getView(R.id.chart_dashboard_legend1));
        legends.add(holder.getView(R.id.chart_dashboard_legend2));
        legends.add(holder.getView(R.id.chart_dashboard_legend3));
        legends.add(holder.getView(R.id.chart_dashboard_legend4));
        legends.add(holder.getView(R.id.chart_dashboard_legend5));
        legends.add(holder.getView(R.id.chart_dashboard_legend6));
        List<TextView> legendsData = new ArrayList<>(6);
        legendsData.add(holder.getView(R.id.chart_dashboard_legend1_data));
        legendsData.add(holder.getView(R.id.chart_dashboard_legend2_data));
        legendsData.add(holder.getView(R.id.chart_dashboard_legend3_data));
        legendsData.add(holder.getView(R.id.chart_dashboard_legend4_data));
        legendsData.add(holder.getView(R.id.chart_dashboard_legend5_data));
        legendsData.add(holder.getView(R.id.chart_dashboard_legend6_data));
        holder.putTag(HOLDER_TAG_LEGENDS, legends);
        holder.putTag(HOLDER_TAG_LEGENDS_DATA, legendsData);
//        chart.animateY(300, Easing.EaseOutCubic);

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

        if (model.dataSet.data == null || model.dataSet.data.size() == 0) {
            chart.setData(null);
            chart.invalidate();
            return;
        }

        PieDataSet dataSet;
        List<PieEntry> dataList = model.dataSet.data;
        legendSetUp(holder, dataList);
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (PieDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(dataList);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
//            PieChartDataUpdateAnim anim = new PieChartDataUpdateAnim(300, chart,
//                    dataSet.getValues(), model.dataSet.data);
//            anim.run();
        } else {
            dataSet = new PieDataSet(dataList, "data");
            dataSet.setColors(Arrays.asList(PIE_COLORS));
            dataSet.setDrawValues(false);
            dataSet.setDrawIcons(false);
            PieData data = new PieData(dataSet);
            chart.setData(data);
            chart.invalidate();
        }
    }

    private void legendSetUp(@NonNull BaseViewHolder<PieChartCard> holder, List<PieEntry> dataList) {
        int size = dataList.size();
        List<TextView> legends = holder.getTag(HOLDER_TAG_LEGENDS);
        List<TextView> legendsData = holder.getTag(HOLDER_TAG_LEGENDS_DATA);
        for (int i = 0; i < 6; i++) {
            TextView legend = legends.get(i);
            TextView legendData = legendsData.get(i);
            if (i < size) {
                Drawable drawable = holder.getContext().getResources()
                        .getDrawable(R.drawable.dashboard_pie_chart_legend_form);
                drawable = DrawableCompat.wrap(drawable);
                PieEntry entry = dataList.get(i);
                DrawableCompat.setTint(drawable, PIE_COLORS[i]);
                if (i < 3) {
                    legend.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                } else {
                    legend.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                }
                legend.setText(entry.getLabel());
                legendData.setText(String.format(Locale.getDefault(), "%.0f%%", entry.getValue() * 100));
                legend.setVisibility(View.VISIBLE);
                legendData.setVisibility(View.VISIBLE);
            } else {
                legend.setVisibility(View.GONE);
                legendData.setVisibility(View.GONE);
            }
        }
    }

    private static class PieChartDataUpdateAnim extends ChartDataChangeAnimation<PieEntry, PieData> {

        private PieChartDataUpdateAnim(int duration, Chart<PieData> chart,
                                       List<PieEntry> oldData, List<PieEntry> newData) {
            super(duration, chart, oldData, newData);
        }

        @Override
        public PieEntry newEntry(PieEntry entry, float newValue) {
            return new PieEntry(newValue, entry.getLabel());
        }
    }

}
