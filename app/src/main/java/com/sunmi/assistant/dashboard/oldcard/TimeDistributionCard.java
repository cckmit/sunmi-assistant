package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.ui.BarXAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.RoundEdgeBarChartRenderer;
import com.sunmi.assistant.dashboard.ui.SpecificLabelsXAxisRenderer;
import com.sunmi.assistant.dashboard.ui.SpecificLabelsYAxisRenderer;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderTimeDistributionResp;
import com.sunmi.assistant.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TimeDistributionCard extends BaseRefreshCard<TimeDistributionCard.Model, OrderTimeDistributionResp> {

    private static final int PERIOD_TODAY_DATA_COUNT = 24;
    private static final int PERIOD_WEEK_DATA_COUNT = 7;
    private static final int INTERVAL_HOUR_SECOND = 3600;
    private static final int INTERVAL_DAY_SECOND = 86400;

    private SpecificLabelsXAxisRenderer xAxisRenderer;
    private SpecificLabelsYAxisRenderer yAxisRenderer;

    public TimeDistributionCard(Context context, DashboardContract.Presenter presenter,
                                int companyId, int shopId) {
        super(context, presenter, companyId, shopId);
        addOnViewClickListener(R.id.tv_dashboard_radio_by_sales, (adapter, holder, v, model, position) -> {
            model.dataSource = Constants.DATA_MODE_SALES;
            updateView();
        });
        addOnViewClickListener(R.id.tv_dashboard_radio_by_order, (adapter, holder, v, model, position) -> {
            model.dataSource = Constants.DATA_MODE_ORDER;
            updateView();
        });
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_time_distribution),
                Constants.DATA_MODE_SALES);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_old_chart_bar;
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
        xAxisRenderer = new SpecificLabelsXAxisRenderer(chart);
        yAxisRenderer = new SpecificLabelsYAxisRenderer(chart);

        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        RoundEdgeBarChartRenderer renderer = new RoundEdgeBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler());
        renderer.setRadius(12);
        chart.setFitBars(true);
        chart.setRenderer(renderer);
        chart.setXAxisRenderer(xAxisRenderer);
        chart.setRendererLeftYAxis(yAxisRenderer);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(ContextCompat.getColor(context, R.color.color_333338));
        xAxis.setValueFormatter(new BarXAxisLabelFormatter(holder.getContext()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(1f);
        yAxis.setTextSize(10f);
        yAxis.setTextColor(ContextCompat.getColor(context, R.color.color_333338));
        yAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        yAxis.setAxisMinimum(0f);
        yAxis.setMinWidth(36f);
        yAxis.enableGridDashedLine(dashLength, dashSpaceLength, 0f);

        return holder;
    }

    @Override
    protected Call<BaseResponse<OrderTimeDistributionResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        int interval;
        if (period == Constants.TIME_PERIOD_TODAY) {
            interval = INTERVAL_HOUR_SECOND;
        } else {
            interval = INTERVAL_DAY_SECOND;
        }
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(period);
        return PaymentApi.get().getOrderTimeDistribution(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, interval, callback);
    }

    @Override
    protected void setupModel(Model model, OrderTimeDistributionResp response) {
        List<OrderTimeDistributionResp.PeriodItem> list = response.getOrder_list();
        int size = list.size();
        model.period = calcPeriodByDataSize(size);
        List<BarEntry> amountList = new ArrayList<>(size);
        List<BarEntry> countList = new ArrayList<>(size);
        for (OrderTimeDistributionResp.PeriodItem item : list) {
            float x = Utils.encodeBarChartXAxisFloat(model.period, item.getTime());
            amountList.add(new BarEntry(x, item.getAmount()));
            countList.add(new BarEntry(x, item.getCount()));
        }
        model.dataSets.put(Constants.DATA_MODE_SALES, amountList);
        model.dataSets.put(Constants.DATA_MODE_ORDER, countList);
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

        TextView title = holder.getView(R.id.tv_dashboard_title);
        BarChart chart = holder.getView(R.id.chart_dashboard_bar);
        TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
        TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);

        title.setText(model.title);
        bySales.setSelected(model.dataSource == Constants.DATA_MODE_SALES);
        byOrder.setSelected(model.dataSource == Constants.DATA_MODE_ORDER);

        List<BarEntry> newDataSet = model.dataSets.get(model.dataSource);
        if (newDataSet == null) {
            newDataSet = new ArrayList<>();
            model.dataSets.put(model.dataSource, newDataSet);
        }

        if (!model.isValid || newDataSet.isEmpty()) {
            newDataSet.clear();
            Pair<Integer, Integer> result = calcRangeOfxAxis(model.period);
            for (int i = result.first; i < result.second; i++) {
                newDataSet.add(new BarEntry(i, 0f));
            }
        } else {
            model.period = calcPeriodByDataSize(newDataSet.size());
        }

        if (model.period == Constants.TIME_PERIOD_TODAY
                && newDataSet.size() <= PERIOD_TODAY_DATA_COUNT) {
            newDataSet.add(new BarEntry(24f, 0f));
        }

        // Calculate min & max of Y-Axis value.
        int max = 0;
        for (BarEntry entry : newDataSet) {
            if (entry.getY() > max) {
                max = (int) Math.ceil(entry.getY());
            }
        }

        xAxisRenderer.setPeriod(model.period, newDataSet.size());
        float maxValue = yAxisRenderer.setMaxValue(max);
        chart.getAxisLeft().setAxisMaximum(maxValue);

        // Calculate bar width.
        float barWidthRatio = calcBarWidth(model.period);

        BarDataSet dataSet;
        BarData barData = chart.getData();
        if (barData != null && barData.getDataSetCount() > 0) {
            dataSet = (BarDataSet) barData.getDataSetByIndex(0);
            dataSet.setValues(newDataSet);
            barData.setBarWidth(barWidthRatio);
            barData.notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(newDataSet, "data");
            dataSet.setColor(ContextCompat.getColor(holder.getContext(), R.color.color_2997FF));
            dataSet.setDrawValues(false);
            barData = new BarData(dataSet);
            barData.setBarWidth(barWidthRatio);
            chart.setData(barData);
        }
        chart.animateY(300, Easing.EaseOutCubic);
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.GONE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupView(holder, model, position);
    }

    private int calcPeriodByDataSize(int size) {
        if (size <= PERIOD_WEEK_DATA_COUNT) {
            return Constants.TIME_PERIOD_WEEK;
        } else if (size <= PERIOD_TODAY_DATA_COUNT + 1) {
            return Constants.TIME_PERIOD_TODAY;
        } else {
            return Constants.TIME_PERIOD_MONTH;
        }
    }

    private Pair<Integer, Integer> calcRangeOfxAxis(int period) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return new Pair<>(0, 25);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return new Pair<>(100, 107);
        } else {
            Calendar c = Calendar.getInstance();
            return new Pair<>(10001, c.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
        }
    }

    private float calcBarWidth(int period) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return 0.65f;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return 0.2f;
        } else {
            return 0.75f;
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private String title;
        private int dataSource;
        private SparseArray<List<BarEntry>> dataSets = new SparseArray<>(2);

        public Model(String title, int dataSource) {
            this.title = title;
            this.dataSource = dataSource;
        }

    }

}
