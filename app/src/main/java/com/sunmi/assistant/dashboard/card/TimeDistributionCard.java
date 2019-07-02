package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.ui.BarXAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.RoundEdgeBarChartRenderer;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderTimeDistributionResp;
import com.sunmi.assistant.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TimeDistributionCard extends BaseRefreshCard<TimeDistributionCard.Model> {

    private static final String TAG = "TimeDistributionCard";

    public TimeDistributionCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model(getContext().getString(R.string.dashboard_time_distribution),
                DashboardContract.DATA_MODE_SALES);
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new TimeDistributionType();
    }

    @Override
    protected void load(int companyId, int shopId, int period, Pair<Long, Long> periodTimestamp, Model model) {
        int interval;
        if (period == DashboardContract.TIME_PERIOD_TODAY) {
            interval = 3600;
        } else {
            interval = 86400;
        }
        SunmiStoreRemote.get().getOrderTimeDistribution(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, interval,
                new CardCallback<OrderTimeDistributionResp>() {
                    @Override
                    public void success(OrderTimeDistributionResp data) {
                        Log.d(TAG, "HTTP request time distribution detail success.");
                        List<OrderTimeDistributionResp.PeriodItem> list = data.getOrder_list();
                        List<BarEntry> amountList = new ArrayList<>(list.size());
                        List<BarEntry> countList = new ArrayList<>(list.size());
                        for (OrderTimeDistributionResp.PeriodItem item : list) {
                            float x = Utils.encodeBarChartXAxisFloat(period, item.getTime());
                            amountList.add(new BarEntry(x, item.getAmount()));
                            countList.add(new BarEntry(x, item.getCount()));
                        }
                        model.dataSets.put(DashboardContract.DATA_MODE_SALES, amountList);
                        model.dataSets.put(DashboardContract.DATA_MODE_ORDER, countList);
                    }
                });
    }

    public class TimeDistributionType extends ItemType<Model, BaseViewHolder<Model>> {

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
        public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
            BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
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
            xAxis.setTextColor(context.getResources().getColor(R.color.color_333338));
            xAxis.setValueFormatter(new BarXAxisLabelFormatter(holder.getContext()));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            YAxis yAxis = chart.getAxisLeft();
            yAxis.setDrawAxisLine(false);
            yAxis.setGranularityEnabled(true);
            yAxis.setGranularity(1f);
            yAxis.setTextSize(10f);
            yAxis.setTextColor(context.getResources().getColor(R.color.color_333338));
            yAxis.setGridColor(context.getResources().getColor(R.color.black_10));
            yAxis.setAxisMinimum(0f);
            yAxis.enableGridDashedLine(dashLength, dashSpaceLength, 0f);
            yAxis.setGridLineWidth(1f);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            setHolder(holder);
            Log.d(TAG, "onBindViewHolder.");
            TextView title = holder.getView(R.id.tv_dashboard_title);
            BarChart chart = holder.getView(R.id.chart_dashboard_bar);
            TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
            TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);
            title.setText(model.title);
            bySales.setSelected(model.dataSource == DashboardContract.DATA_MODE_SALES);
            byOrder.setSelected(model.dataSource == DashboardContract.DATA_MODE_ORDER);

            if (getState() == STATE_INIT || getState() == STATE_LOADING) {
                Log.d(TAG, "Card data setup view skip.");
                return;
            }

            List<BarEntry> dataList = model.dataSets.get(model.dataSource);
            if (dataList == null || dataList.size() == 0) {
                chart.setData(null);
                chart.invalidate();
                holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
                return;
            }

            // Calculate min & max of Y-Axis value.
            float max = 0;
            for (BarEntry entry : dataList) {
                if (entry.getY() > max) {
                    max = entry.getY();
                }
            }
            chart.getAxisLeft().setAxisMaximum(max > 0 ? max * 1.2f : 5f);

            BarDataSet dataSet;
            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
                dataSet.setValues(dataList);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
//                BarChartDataUpdateAnim anim = new BarChartDataUpdateAnim(300, chart,
//                        dataSet.getValues(), dataList);
//                anim.run();
            } else {
                dataSet = new BarDataSet(dataList, "data");
                dataSet.setColor(holder.getContext().getResources().getColor(R.color.color_2997FF));
                dataSet.setDrawValues(false);
                BarData data = new BarData(dataSet);
                chart.setData(data);
                chart.invalidate();
            }
            chart.animateY(300, Easing.EaseOutCubic);
            holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
        }

    }

    static class Model {
        public String title;
        public int dataSource;
        public SparseArray<List<BarEntry>> dataSets = new SparseArray<>(2);

        public Model(String title, int dataSource) {
            this.title = title;
            this.dataSource = dataSource;
        }
    }

}
