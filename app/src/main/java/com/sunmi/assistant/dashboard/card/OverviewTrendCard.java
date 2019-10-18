package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.Typeface;
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
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.ui.BarChartMarkerView;
import com.sunmi.assistant.dashboard.ui.ChartEntry;
import com.sunmi.assistant.dashboard.ui.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.LineYAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.RateYAxisLabelsRenderer;
import com.sunmi.assistant.dashboard.ui.RoundEdgeBarChartRenderer;
import com.sunmi.assistant.dashboard.ui.VolumeYAxisLabelsRenderer;
import com.sunmi.assistant.dashboard.ui.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.XAxisLabelsRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerRateResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class OverviewTrendCard extends BaseRefreshCard<OverviewTrendCard.Model, CustomerRateResp> {

    private static OverviewTrendCard sInstance;

    private XAxisLabelsRenderer lineXAxisRenderer;
    private RateYAxisLabelsRenderer lineYAxisRenderer;
    private XAxisLabelsRenderer barXAxisRenderer;
    private VolumeYAxisLabelsRenderer barYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private BarChartMarkerView mBarChartMarker;
    private float mDashLength;
    private float mDashSpaceLength;

    private OverviewTrendCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static OverviewTrendCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new OverviewTrendCard(presenter, source);
        } else {
            sInstance.reset(source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_trend;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);
        holder.addOnClickListener(R.id.tv_dashboard_rate, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_RATE) {
                model.type = Constants.DATA_TYPE_RATE;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_volume, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_VOLUME) {
                model.type = Constants.DATA_TYPE_VOLUME;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_customer, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_CUSTOMER) {
                model.type = Constants.DATA_TYPE_CUSTOMER;
                updateViews();
            }
        });

        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);
        BarChart barChart = holder.getView(R.id.view_dashboard_bar_chart);

        // 设置图表坐标Label格式
        lineXAxisRenderer = new XAxisLabelsRenderer(lineChart);
        lineYAxisRenderer = new RateYAxisLabelsRenderer(lineChart);
        barXAxisRenderer = new XAxisLabelsRenderer(barChart);
        barYAxisRenderer = new VolumeYAxisLabelsRenderer(barChart);
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
        lineXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barXAxis.setDrawAxisLine(true);
        barXAxis.setDrawGridLines(false);
        barXAxis.setTextSize(10f);
        barXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        barXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis lineYAxis = lineChart.getAxisLeft();
        YAxis barYAxis = barChart.getAxisLeft();
        lineYAxis.setDrawAxisLine(false);
        lineYAxis.setGranularityEnabled(true);
        lineYAxis.setGranularity(0.2f);
        lineYAxis.setTextSize(10f);
        lineYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
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
        barYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
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
    protected Call<BaseResponse<CustomerRateResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerRate(companyId, shopId, period, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model("");
    }

    @Override
    protected void setupModel(Model model, CustomerRateResp response) {
        List<ChartEntry> rateList = model.dataSets.get(Constants.DATA_TYPE_RATE);
        List<ChartEntry> volumeList = model.dataSets.get(Constants.DATA_TYPE_VOLUME);
        List<ChartEntry> customerList = model.dataSets.get(Constants.DATA_TYPE_CUSTOMER);
        rateList.clear();
        volumeList.clear();
        customerList.clear();
        if (response == null || response.getCountList() == null) {
            LogCat.e(TAG, "Trend data load Failed. Response is null.");
            return;
        }
        List<CustomerRateResp.CountListBean> list = response.getCountList();
        int size = list.size();
        for (CustomerRateResp.CountListBean bean : list) {
            int timeIndex = Math.abs(bean.getTime());
            int count = Math.abs(bean.getOrderCount());
            int customer = Math.abs(bean.getPassengerFlowCount());
            float x = Utils.encodeChartXAxisFloat(model.period, timeIndex);
            long time = Utils.getTime(model.period, timeIndex, size);
            float rate = customer == 0 ? 0f : Math.min((float) count / customer, 1f);
            rateList.add(new ChartEntry(x, rate, time));
            volumeList.add(new ChartEntry(x, count, time));
            customerList.add(new ChartEntry(x, customer, time));
        }

        // Test data
//        model.random();
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Get views
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView rate = holder.getView(R.id.tv_dashboard_rate);
        TextView volume = holder.getView(R.id.tv_dashboard_volume);
        TextView customer = holder.getView(R.id.tv_dashboard_customer);

        LineChart line = holder.getView(R.id.view_dashboard_line_chart);
        BarChart bar = holder.getView(R.id.view_dashboard_bar_chart);

        // Set visible & button selected
        rate.setVisibility(hasFs() && hasAuth() ? View.VISIBLE : View.GONE);
        volume.setVisibility(hasAuth() ? View.VISIBLE : View.GONE);
        customer.setVisibility(hasFs() ? View.VISIBLE : View.GONE);
        line.setVisibility(model.type == Constants.DATA_TYPE_RATE ? View.VISIBLE : View.INVISIBLE);
        bar.setVisibility(model.type != Constants.DATA_TYPE_RATE ? View.VISIBLE : View.INVISIBLE);
        rate.setSelected(model.type == Constants.DATA_TYPE_RATE);
        rate.setTypeface(null, model.type == Constants.DATA_TYPE_RATE ? Typeface.BOLD : Typeface.NORMAL);
        volume.setSelected(model.type == Constants.DATA_TYPE_VOLUME);
        volume.setTypeface(null, model.type == Constants.DATA_TYPE_VOLUME ? Typeface.BOLD : Typeface.NORMAL);
        customer.setSelected(model.type == Constants.DATA_TYPE_CUSTOMER);
        customer.setTypeface(null, model.type == Constants.DATA_TYPE_CUSTOMER ? Typeface.BOLD : Typeface.NORMAL);

        // Get data set from model
        List<ChartEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }
//        LogCat.d(TAG, "Period=" + model.period + "; type=" + model.type + "\nData set:" + dataSet);

        // Calculate min & max of axis value.
        Pair<Integer, Integer> xAxisRange = Utils.calcChartXAxisRange(model.period);
        int max = 0;
        float lastX = 0;
        for (ChartEntry entry : dataSet) {
            if (model.type == Constants.DATA_TYPE_RATE && entry.getY() > 1) {
                entry.setY(1f);
            }
            if (entry.getX() > lastX) {
                lastX = entry.getX();
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
            int color = ContextCompat.getColor(holder.getContext(), R.color.common_orange);
            mLineChartMarker.setType(model.period, model.type);
            mLineChartMarker.setPointColor(color);
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
                set.setDrawValues(false);
                set.setDrawCircleHole(false);
                set.setDrawHorizontalHighlightIndicator(false);
                set.setColor(color);
                set.setCircleColor(color);
                set.setHighLightColor(color);
                set.setLineWidth(2f);
                set.setCircleRadius(1f);
                set.setHighlightLineWidth(1f);
                set.enableDashedHighlightLine(mDashLength, mDashSpaceLength, 0);
                data = new LineData(set);
                line.setData(data);
            }
            line.highlightValue(lastX, 0);
            line.animateX(300);
        } else {
            mBarChartMarker.setType(model.period, model.type);
            float barWidthRatio = calcBarWidth(model.period);
            int color = model.type == Constants.DATA_TYPE_VOLUME ?
                    ContextCompat.getColor(holder.getContext(), R.color.color_FFD0B3) :
                    ContextCompat.getColor(holder.getContext(), R.color.color_AFC3FA);
            int colorHighlight = model.type == Constants.DATA_TYPE_VOLUME ?
                    ContextCompat.getColor(holder.getContext(), R.color.common_orange) :
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
            bar.highlightValue(lastX, 0);
            bar.animateY(300, Easing.EaseOutCubic);
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.period = mPeriod;
        model.dataSets.get(model.type).clear();
        setupView(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.period = mPeriod;
        model.dataSets.get(model.type).clear();
        setupView(holder, model, position);
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

    public static class Model extends BaseRefreshCard.BaseModel {
        private String title;
        private int type;
        private SparseArray<List<ChartEntry>> dataSets = new SparseArray<>(3);

        public Model(String title) {
            this.title = title;
            dataSets.put(Constants.DATA_TYPE_RATE, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_VOLUME, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_CUSTOMER, new ArrayList<>());
        }

        @Override
        public void init(int source) {
            updateType(source);
            for (int i = 0, size = dataSets.size(); i < size; i++) {
                int key = dataSets.keyAt(i);
                dataSets.get(key).clear();
            }
        }

        public void updateType(int source) {
            if ((source & Constants.DATA_SOURCE_FS) != 0 && (source & Constants.DATA_SOURCE_AUTH) != 0) {
                type = Constants.DATA_TYPE_RATE;
            } else if ((source & Constants.DATA_SOURCE_AUTH) != 0) {
                type = Constants.DATA_TYPE_VOLUME;
            } else {
                type = Constants.DATA_TYPE_CUSTOMER;
            }
        }

        public void random() {
            List<ChartEntry> rateList = dataSets.get(Constants.DATA_TYPE_RATE);
            List<ChartEntry> volumeList = dataSets.get(Constants.DATA_TYPE_VOLUME);
            List<ChartEntry> customerList = dataSets.get(Constants.DATA_TYPE_CUSTOMER);
            rateList.clear();
            volumeList.clear();
            customerList.clear();
            int count = period == Constants.TIME_PERIOD_WEEK ? 5 : 20;
            int min = count / 3;
            for (int i = 1; i < count + 1; i++) {
                float x = Utils.encodeChartXAxisFloat(period, i);
                long time = Utils.getTime(period, i, count);
                if (i <= min + 1) {
                    rateList.add(new ChartEntry(x, 0f, time));
                    volumeList.add(new ChartEntry(x, 0f, time));
                    customerList.add(new ChartEntry(x, 0f, time));
                } else {
                    rateList.add(new ChartEntry(x, (float) Math.random(), time));
                    volumeList.add(new ChartEntry(x, (int) (Math.random() * 1000), time));
                    customerList.add(new ChartEntry(x, (int) (Math.random() * 1000), time));
                }
            }
        }

    }
}
