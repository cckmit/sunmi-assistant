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
import com.sunmi.assistant.dashboard.ui.chart.BarChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.BarChartRoundEdgeRenderer;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;
import com.sunmi.assistant.dashboard.ui.chart.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.TimeMarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelRenderer;
import com.sunmi.assistant.dashboard.ui.chart.YAxisRateLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisRateLabelRenderer;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.exception.TimeDateException;
import sunmi.common.model.CustomerRateResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeTrendCard extends BaseRefreshCard<RealtimeTrendCard.Model, CustomerRateResp> {

    private static RealtimeTrendCard sInstance;

    private XAxisLabelRenderer lineXAxisRenderer;
    private YAxisRateLabelRenderer lineYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private TimeMarkerFormatter lineMarkerFormatter;

    private XAxisLabelRenderer barXAxisRenderer;
    private YAxisVolumeLabelsRenderer barYAxisRenderer;
    private BarChartMarkerView mBarChartMarker;
    private TimeMarkerFormatter barMarkerFormatter;

    private float mDashLength;
    private float mDashSpaceLength;

    private RealtimeTrendCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static RealtimeTrendCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new RealtimeTrendCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_realtime_trend;
    }

    private void setupClick(BaseViewHolder<Model> holder) {
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
    }

    private void setupLineChart(Context context, LineChart chart) {
        // 设置图表坐标Label格式
        lineXAxisRenderer = new XAxisLabelRenderer(chart);
        lineYAxisRenderer = new YAxisRateLabelRenderer(chart);
        chart.setXAxisRenderer(lineXAxisRenderer);
        chart.setRendererLeftYAxis(lineYAxisRenderer);

        // 设置通用图表
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        // 设置X轴
        XAxis lineXAxis = chart.getXAxis();
        lineXAxis.setDrawAxisLine(true);
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setTextSize(10f);
        lineXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis lineYAxis = chart.getAxisLeft();
        lineYAxis.setDrawAxisLine(false);
        lineYAxis.setGranularityEnabled(true);
        lineYAxis.setGranularity(0.2f);
        lineYAxis.setTextSize(10f);
        lineYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineYAxis.setAxisMinimum(0f);
        lineYAxis.setAxisMaximum(1.01f);
        lineYAxis.setDrawGridLines(true);
        lineYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        lineYAxis.setValueFormatter(new YAxisRateLabelFormatter());
        lineYAxis.setMinWidth(36f);

        // 设置Marker
        lineMarkerFormatter = new TimeMarkerFormatter(context);
        lineMarkerFormatter.setValueType(TimeMarkerFormatter.VALUE_TYPE_RATE);
        mLineChartMarker = new LineChartMarkerView(context, lineMarkerFormatter);
        mLineChartMarker.setChartView(chart);
        mLineChartMarker.setTitle(R.string.dashboard_card_tab_rate);
        chart.setMarker(mLineChartMarker);
    }

    private void setupBarChart(Context context, BarChart chart) {

        // 设置图表坐标Label格式
        barXAxisRenderer = new XAxisLabelRenderer(chart);
        barYAxisRenderer = new YAxisVolumeLabelsRenderer(chart);
        chart.setXAxisRenderer(barXAxisRenderer);
        chart.setRendererLeftYAxis(barYAxisRenderer);

        // 设置通用图表
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        // 设置X轴
        XAxis barXAxis = chart.getXAxis();
        barXAxis.setDrawAxisLine(true);
        barXAxis.setDrawGridLines(false);
        barXAxis.setTextSize(10f);
        barXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        barXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis barYAxis = chart.getAxisLeft();
        barYAxis.setDrawAxisLine(false);
        barYAxis.setGranularityEnabled(true);
        barYAxis.setGranularity(1f);
        barYAxis.setTextSize(10f);
        barYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        barYAxis.setAxisMinimum(0f);
        barYAxis.setDrawGridLines(true);
        barYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        barYAxis.setMinWidth(36f);

        // 设置Marker和Bar样式
        float barRadius = CommonHelper.dp2px(context, 1f);
        BarChartRoundEdgeRenderer renderer = new BarChartRoundEdgeRenderer(chart, barRadius);
        chart.setRenderer(renderer);
        chart.setFitBars(true);
        chart.setDrawBarShadow(false);
        barMarkerFormatter = new TimeMarkerFormatter(context);
        mBarChartMarker = new BarChartMarkerView(context, barMarkerFormatter);
        mBarChartMarker.setChartView(chart);
        chart.setMarker(mBarChartMarker);
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        setupClick(holder);
        Context context = view.getContext();

        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);

        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);
        BarChart barChart = holder.getView(R.id.view_dashboard_bar_chart);

        setupLineChart(context, lineChart);
        setupBarChart(context, barChart);
        return holder;
    }

    @Override
    protected Call<BaseResponse<CustomerRateResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerRate(companyId, shopId, period, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerRateResp response) {
        List<ChartEntry> rateList = model.dataSets.get(Constants.DATA_TYPE_RATE);
        List<ChartEntry> volumeList = model.dataSets.get(Constants.DATA_TYPE_VOLUME);
        List<ChartEntry> customerList = model.dataSets.get(Constants.DATA_TYPE_CUSTOMER);
        rateList.clear();
        volumeList.clear();
        customerList.clear();
        if (response == null || response.getCountList() == null || response.getCountList().isEmpty()) {
            LogCat.e(TAG, "Response is empty.");
            return;
        }
        try {
            response.init(model.period);
            List<CustomerRateResp.Item> list = response.getCountList();
            for (CustomerRateResp.Item bean : list) {
                long timestamp = Math.abs(bean.getTime());
                int count = Math.abs(bean.getOrderCount());
                int customer = Math.abs(bean.getPassengerFlowCount());
                float x = Utils.encodeChartXAxisFloat(model.period, timestamp);
                float rate = customer == 0 ? 0f : Math.min((float) count / customer, 1f);
                rateList.add(new ChartEntry(x, rate, timestamp));
                volumeList.add(new ChartEntry(x, count, timestamp));
                customerList.add(new ChartEntry(x, customer, timestamp));
            }
        } catch (TimeDateException e) {
            LogCat.e(TAG, e.getMessage());
            LogCat.d(TAG, "Code: " + e.getCode() + ", " + e.getDetail());
            rateList.clear();
            volumeList.clear();
            customerList.clear();
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // setup card background
        View root = holder.getView(R.id.layout_dashboard_root);
        if (hasFs()) {
            root.setBackgroundResource(R.color.common_fill);
        } else {
            root.setBackgroundResource(R.drawable.bg_top_gray_radius);
        }

        // setup label selected
        setupLabelState(holder, model);

        LineChart line = holder.getView(R.id.view_dashboard_line_chart);
        BarChart bar = holder.getView(R.id.view_dashboard_bar_chart);

        // Get data set from model
        List<ChartEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }

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

        // Refresh data set
        if (model.type == Constants.DATA_TYPE_RATE) {
            setupLineData(line, model, xAxisRange, lastX);
        } else {
            setupBarData(bar, model, xAxisRange, max, lastX);
        }
    }

    private void setupLabelState(@NonNull BaseViewHolder<Model> holder, Model model) {
        // Get views
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
    }

    private void setupLineData(LineChart line, Model model, Pair<Integer, Integer> xAxisRange, float lastX) {
        int maxDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        lineXAxisRenderer.setPeriod(model.period, maxDay);
        line.getXAxis().setAxisMinimum(xAxisRange.first);
        line.getXAxis().setAxisMaximum(xAxisRange.second);

        int color = ContextCompat.getColor(line.getContext(), R.color.common_orange);
        if (model.period == Constants.TIME_PERIOD_YESTERDAY || model.period == Constants.TIME_PERIOD_TODAY) {
            lineMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_HOUR);
        } else {
            lineMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_DATE);
        }

        LineDataSet set;
        LineData data = line.getData();
        List<ChartEntry> dataSet = model.dataSets.get(model.type);
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
            mLineChartMarker.setPointColor(color);
            data = new LineData(set);
            line.setData(data);
        }
        line.highlightValue(lastX, 0);
        line.animateX(300);
    }

    private void setupBarData(BarChart bar, Model model,
                              Pair<Integer, Integer> xAxisRange, int maxX, float lastX) {
        int maxDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        barXAxisRenderer.setPeriod(model.period, maxDay);
        float maxAxis = barYAxisRenderer.setMaxValue(maxX);
        bar.getXAxis().setAxisMinimum(xAxisRange.first);
        bar.getXAxis().setAxisMaximum(xAxisRange.second);
        bar.getAxisLeft().setAxisMaximum(maxAxis);

        if (model.type == Constants.DATA_TYPE_VOLUME) {
            mBarChartMarker.setTitle(R.string.dashboard_card_tab_volume);
        } else if (model.type == Constants.DATA_TYPE_CUSTOMER) {
            mBarChartMarker.setTitle(R.string.dashboard_card_tab_customer);
        }

        if (model.period == Constants.TIME_PERIOD_YESTERDAY || model.period == Constants.TIME_PERIOD_TODAY) {
            barMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_HOUR);
        } else {
            barMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_DATE);
        }

        float barWidthRatio = calcBarWidth(model.period);
        int color = model.type == Constants.DATA_TYPE_VOLUME ?
                ContextCompat.getColor(bar.getContext(), R.color.color_FFD0B3) :
                ContextCompat.getColor(bar.getContext(), R.color.color_AFC3FA);
        int colorHighlight = model.type == Constants.DATA_TYPE_VOLUME ?
                ContextCompat.getColor(bar.getContext(), R.color.common_orange) :
                ContextCompat.getColor(bar.getContext(), R.color.assist_primary);
        BarDataSet set;
        BarData data = bar.getData();
        List<ChartEntry> dataSet = model.dataSets.get(model.type);
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
        private int type;
        private SparseArray<List<ChartEntry>> dataSets = new SparseArray<>(3);

        public Model() {
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

        private void updateType(int source) {
            if ((source & Constants.DATA_SOURCE_FS) != 0 && (source & Constants.DATA_SOURCE_AUTH) != 0) {
                type = Constants.DATA_TYPE_RATE;
            } else if ((source & Constants.DATA_SOURCE_AUTH) != 0) {
                type = Constants.DATA_TYPE_VOLUME;
            } else {
                type = Constants.DATA_TYPE_CUSTOMER;
            }
        }

        private void random() {
            List<ChartEntry> rateList = dataSets.get(Constants.DATA_TYPE_RATE);
            List<ChartEntry> volumeList = dataSets.get(Constants.DATA_TYPE_VOLUME);
            List<ChartEntry> customerList = dataSets.get(Constants.DATA_TYPE_CUSTOMER);
            rateList.clear();
            volumeList.clear();
            customerList.clear();
            Random r = new Random(System.currentTimeMillis());
            int count = period == Constants.TIME_PERIOD_WEEK ? 5 : 20;
            int min = count / 3;
            long time = Utils.getStartTime(period);
            boolean inDay = period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY;
            for (int i = 1; i < count + 1; i++) {
                time = time + (i - 1) * (inDay ? 3600000 : 86400000);
                float x = Utils.encodeChartXAxisFloat(period, time);
                if (i <= min + 1) {
                    rateList.add(new ChartEntry(x, 0f, time));
                    volumeList.add(new ChartEntry(x, 0f, time));
                    customerList.add(new ChartEntry(x, 0f, time));
                } else {
                    rateList.add(new ChartEntry(x, r.nextFloat(), time));
                    volumeList.add(new ChartEntry(x, r.nextInt(1000), time));
                    customerList.add(new ChartEntry(x, r.nextInt(1000), time));
                }
            }
        }

    }
}
