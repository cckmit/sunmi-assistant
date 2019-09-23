package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.ui.BarChartMarkerView;
import com.sunmi.assistant.dashboard.ui.BarYAxisLabelsRenderer;
import com.sunmi.assistant.dashboard.ui.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.LineYAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.LineYAxisLabelsRenderer;
import com.sunmi.assistant.dashboard.ui.RoundEdgeBarChartRenderer;
import com.sunmi.assistant.dashboard.ui.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.XAxisLabelsRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.ConsumerRateResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TrendChartCard extends BaseRefreshItem<TrendChartCard.Model, ConsumerRateResp> {

    private XAxisLabelsRenderer lineXAxisRenderer;
    private LineYAxisLabelsRenderer lineYAxisRenderer;
    private XAxisLabelsRenderer barXAxisRenderer;
    private BarYAxisLabelsRenderer barYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private BarChartMarkerView mBarChartMarker;
    private final float mDashLength;
    private final float mDashSpaceLength;

    public TrendChartCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter);
        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);
        addOnViewClickListener(R.id.tv_dashboard_rate, (adapter, holder, v, model, position) -> {
            if (model.type != Constants.DATA_TYPE_RATE) {
                model.type = Constants.DATA_TYPE_RATE;
                updateView();
            }
        });
        addOnViewClickListener(R.id.tv_dashboard_volume, (adapter, holder, v, model, position) -> {
            if (model.type != Constants.DATA_TYPE_VOLUME) {
                model.type = Constants.DATA_TYPE_VOLUME;
                updateView();
            }
        });
        addOnViewClickListener(R.id.tv_dashboard_consumer, (adapter, holder, v, model, position) -> {
            if (model.type != Constants.DATA_TYPE_CONSUMER) {
                model.type = Constants.DATA_TYPE_CONSUMER;
                updateView();
            }
        });
    }

    @Override
    protected Model createModel(Context context) {
        int type;
        if (showTransactionData() && showConsumerData()) {
            type = Constants.DATA_TYPE_RATE;
        } else if (showTransactionData()) {
            type = Constants.DATA_TYPE_VOLUME;
        } else {
            type = Constants.DATA_TYPE_CONSUMER;
        }
        return new Model("", type);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_trend;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);
        BarChart barChart = holder.getView(R.id.view_dashboard_bar_chart);
        Context context = view.getContext();
        // 设置图表坐标Label格式
        lineXAxisRenderer = new XAxisLabelsRenderer(lineChart);
        lineYAxisRenderer = new LineYAxisLabelsRenderer(lineChart);
        barXAxisRenderer = new XAxisLabelsRenderer(barChart);
        barYAxisRenderer = new BarYAxisLabelsRenderer(barChart);
        lineChart.setXAxisRenderer(lineXAxisRenderer);
        lineChart.setRendererLeftYAxis(lineYAxisRenderer);
        barChart.setXAxisRenderer(barXAxisRenderer);
        barChart.setRendererLeftYAxis(barYAxisRenderer);

        // 设置通用图表
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // 设置X轴
        XAxis lineXAxis = lineChart.getXAxis();
        XAxis barXAxis = barChart.getXAxis();
        lineXAxis.setDrawAxisLine(true);
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setTextSize(10f);
        lineXAxis.setTextColor(ContextCompat.getColor(context, R.color.color_A1A7B3));
        lineXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barXAxis.setDrawAxisLine(true);
        barXAxis.setDrawGridLines(false);
        barXAxis.setTextSize(10f);
        barXAxis.setTextColor(ContextCompat.getColor(context, R.color.color_A1A7B3));
        barXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis lineYAxis = lineChart.getAxisLeft();
        YAxis barYAxis = barChart.getAxisLeft();
        lineYAxis.setDrawAxisLine(false);
        lineYAxis.setGranularityEnabled(true);
        lineYAxis.setGranularity(0.2f);
        lineYAxis.setTextSize(10f);
        lineYAxis.setTextColor(ContextCompat.getColor(context, R.color.color_A1A7B3));
        lineYAxis.setAxisMinimum(0f);
        lineYAxis.setAxisMaximum(1f);
        lineYAxis.setDrawGridLines(true);
        lineYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        lineYAxis.setValueFormatter(new LineYAxisLabelFormatter());
        lineYAxis.setMinWidth(36f);
        barYAxis.setDrawAxisLine(false);
        barYAxis.setGranularityEnabled(true);
        barYAxis.setGranularity(1f);
        barYAxis.setTextSize(10f);
        barYAxis.setTextColor(ContextCompat.getColor(context, R.color.color_A1A7B3));
        barYAxis.setAxisMinimum(0f);
        barYAxis.setDrawGridLines(true);
        barYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        barYAxis.setMinWidth(36f);

        // 设置Line图
        mLineChartMarker = new LineChartMarkerView(context);
        mLineChartMarker.setChartView(lineChart);
        lineChart.setMarker(mLineChartMarker);

        // 设置Bar图
        float barRadius = CommonHelper.dp2px(context, 1f);
        RoundEdgeBarChartRenderer renderer = new RoundEdgeBarChartRenderer(barChart, barRadius);
        barChart.setRenderer(renderer);
        barChart.setFitBars(true);
        barChart.setDrawBarShadow(false);

        mBarChartMarker = new BarChartMarkerView(context);
        mBarChartMarker.setChartView(barChart);
        barChart.setMarker(mBarChartMarker);

        return holder;
    }

    @Override
    protected Call<BaseResponse<ConsumerRateResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getConsumerRate(companyId, shopId, period, callback);
        return null;
    }

    @Override
    protected void setupModel(Model model, ConsumerRateResp response) {
        List<BarEntry> rateList = model.dataSets.get(Constants.DATA_TYPE_RATE);
        List<BarEntry> volumeList = model.dataSets.get(Constants.DATA_TYPE_VOLUME);
        List<BarEntry> consumerList = model.dataSets.get(Constants.DATA_TYPE_CONSUMER);
        rateList.clear();
        volumeList.clear();
        consumerList.clear();
        List<ConsumerRateResp.CountListBean> list = response.getCountList();
        for (ConsumerRateResp.CountListBean bean : list) {
            int time = Math.abs(bean.getTime());
            int count = Math.abs(bean.getOrderCount());
            int consumer = Math.abs(bean.getPassengerFlowCount());
            float x = Utils.encodeChartXAxisFloat(model.period, time);
            rateList.add(new BarEntry(x, consumer == 0 ? 0f : Math.min((float) count / consumer, 1f)));
            volumeList.add(new BarEntry(x, count));
            consumerList.add(new BarEntry(x, consumer));

//            rateList.add(new BarEntry(x, (float) Math.random()));
//            volumeList.add(new BarEntry(x, (int)(Math.random() * 1000)));
//            consumerList.add(new BarEntry(x, (int)(Math.random() * 1000)));
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Get views
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView rate = holder.getView(R.id.tv_dashboard_rate);
        TextView volume = holder.getView(R.id.tv_dashboard_volume);
        TextView consumer = holder.getView(R.id.tv_dashboard_consumer);

        LineChart line = holder.getView(R.id.view_dashboard_line_chart);
        BarChart bar = holder.getView(R.id.view_dashboard_bar_chart);

        // Set visible & button selected
        rate.setVisibility(showConsumerData() && showTransactionData() ? View.VISIBLE : View.GONE);
        volume.setVisibility(showTransactionData() ? View.VISIBLE : View.GONE);
        consumer.setVisibility(showConsumerData() ? View.VISIBLE : View.GONE);
        line.setVisibility(model.type == Constants.DATA_TYPE_RATE ? View.VISIBLE : View.INVISIBLE);
        bar.setVisibility(model.type != Constants.DATA_TYPE_RATE ? View.VISIBLE : View.INVISIBLE);
        rate.setSelected(model.type == Constants.DATA_TYPE_RATE);
        volume.setSelected(model.type == Constants.DATA_TYPE_VOLUME);
        consumer.setSelected(model.type == Constants.DATA_TYPE_CONSUMER);

        // Get data set from model
        List<BarEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }
        LogCat.d(TAG, "Period=" + model.period + "; type=" + model.type + "\nData set:" + dataSet);

        // Calculate min & max of axis value.
        Pair<Integer, Integer> xAxisRange = Utils.calcChartXAxisRange(model.period);
        int max = 0;
        for (BarEntry entry : dataSet) {
            if (model.type == Constants.DATA_TYPE_RATE && entry.getY() > 1) {
                entry.setY(1f);
            }
            if (entry.getY() > max) {
                max = (int) Math.ceil(entry.getY());
            }
        }
        int maxDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        if (model.type == Constants.DATA_TYPE_RATE) {
            lineXAxisRenderer.setPeriod(model.period, maxDay);
            line.getXAxis().setAxisMinimum(xAxisRange.first);
            line.getXAxis().setAxisMaximum(xAxisRange.second);
        } else {
            barXAxisRenderer.setPeriod(model.period, maxDay);
            float maxAxis = barYAxisRenderer.setMaxValue(max);
            bar.getXAxis().setAxisMinimum(xAxisRange.first);
            bar.getXAxis().setAxisMaximum(xAxisRange.second);
            bar.getAxisLeft().setAxisMaximum(maxAxis);
        }

        // Refresh data set
        if (model.type == Constants.DATA_TYPE_RATE) {
            line.highlightValue(null);
            mLineChartMarker.setType(model.type);
            LineDataSet set;
            LineData data = line.getData();
            ArrayList<Entry> values = new ArrayList<>(dataSet);
            if (data != null && data.getDataSetCount() > 0) {
                set = (LineDataSet) data.getDataSetByIndex(0);
                set.setValues(values);
                data.notifyDataChanged();
                line.notifyDataSetChanged();
            } else {
                set = new LineDataSet(values, "data");
                int color = ContextCompat.getColor(holder.getContext(), R.color.colorOrange);
                set.setColor(color);
                set.setLineWidth(2f);
                set.setDrawFilled(true);
                set.setFillDrawable(ContextCompat.getDrawable(holder.getContext(),
                        R.drawable.dashboard_line_chart_filled_color));
                set.setDrawValues(false);
                set.setDrawCircleHole(false);
                set.setCircleColor(color);
                set.setCircleRadius(1f);
                set.setDrawHorizontalHighlightIndicator(false);
                set.setHighlightLineWidth(1f);
                set.enableDashedHighlightLine(mDashLength, mDashSpaceLength, 0);
                data = new LineData(set);
                line.setData(data);
            }
            line.animateX(300);
        } else {
            bar.highlightValue(null);
            mBarChartMarker.setType(model.type);
            float barWidthRatio = calcBarWidth(model.period);
            int color = model.type == Constants.DATA_TYPE_VOLUME ?
                    ContextCompat.getColor(holder.getContext(), R.color.color_FFD0B3) :
                    ContextCompat.getColor(holder.getContext(), R.color.color_AFC3FA);
            int colorHighlight = model.type == Constants.DATA_TYPE_VOLUME ?
                    ContextCompat.getColor(holder.getContext(), R.color.colorOrange) :
                    ContextCompat.getColor(holder.getContext(), R.color.color_4B7AFA);
            BarDataSet set;
            BarData data = bar.getData();
            ArrayList<BarEntry> values = new ArrayList<>(dataSet);
            if (data != null && data.getDataSetCount() > 0) {
                set = (BarDataSet) data.getDataSetByIndex(0);
                set.setColor(color);
                set.setHighLightColor(colorHighlight);
                set.setValues(values);
                data.setBarWidth(barWidthRatio);
                data.notifyDataChanged();
                bar.notifyDataSetChanged();
            } else {
                set = new BarDataSet(values, "data");
                set.setColor(ContextCompat.getColor(holder.getContext(), R.color.color_2997FF));
                set.setDrawValues(false);
                set.setColor(color);
                set.setHighLightColor(colorHighlight);
                data = new BarData(set);
                data.setBarWidth(barWidthRatio);
                bar.setData(data);
            }
            bar.animateY(300, Easing.EaseOutCubic);
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showLoading(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showError(holder, model, position);
    }

    private float calcBarWidth(int period) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return 0.5f;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return 0.3f;
        } else {
            return 0.6f;
        }
    }

    public static class Model extends BaseRefreshItem.BaseModel {
        private String title;
        private int type;
        private SparseArray<List<BarEntry>> dataSets = new SparseArray<>(3);

        public Model(String title, int type) {
            this.title = title;
            this.type = type;
            dataSets.put(Constants.DATA_TYPE_RATE, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_VOLUME, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_CONSUMER, new ArrayList<>());
        }

        public void clear() {
            dataSets.clear();
        }
    }
}
