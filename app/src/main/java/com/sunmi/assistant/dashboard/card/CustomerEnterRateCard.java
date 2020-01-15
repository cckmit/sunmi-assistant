package com.sunmi.assistant.dashboard.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;
import com.sunmi.assistant.dashboard.ui.chart.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.MarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelRenderer;
import com.sunmi.assistant.dashboard.ui.chart.YAxisRateLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisRateLabelRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerEnterRateTrendResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @date 2020-01-13
 */
public class CustomerEnterRateCard extends BaseRefreshCard<CustomerEnterRateCard.Model, CustomerEnterRateTrendResp> {

    private static CustomerEnterRateCard sInstance;

    private XAxisLabelRenderer lineXAxisRenderer;
    private YAxisRateLabelRenderer lineYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private MarkerFormatter mMarkerFormatter;

    private float mDashLength;
    private float mDashSpaceLength;


    private CustomerEnterRateCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerEnterRateCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerEnterRateCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_customer_enter_rate;
    }

    @Override
    public void init(Context context) {
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<CustomerEnterRateCard.Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);

        LineChart chart = holder.getView(R.id.view_dashboard_line_chart);

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
        lineYAxis.setAxisMaximum(1f);
        lineYAxis.setDrawGridLines(true);
        lineYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        lineYAxis.setValueFormatter(new YAxisRateLabelFormatter());
        lineYAxis.setMinWidth(36f);

        // 设置Line图
        mMarkerFormatter = new MarkerFormatter(context);
        mMarkerFormatter.setValueType(MarkerFormatter.VALUE_TYPE_RATE);
        mLineChartMarker = new LineChartMarkerView(context, mMarkerFormatter);
        mLineChartMarker.setChartView(chart);
        mLineChartMarker.setTitle(R.string.dashboard_card_title_customer_enter_rate);
        chart.setMarker(mLineChartMarker);

        return holder;
    }

    @Override
    protected Call<BaseResponse<CustomerEnterRateTrendResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        String group = "day";
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            group = "hour";
        }
        SunmiStoreApi.getInstance().getCustomerEnterRateTrend(companyId, shopId, period, group, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerEnterRateTrendResp response) {
        model.dataSet.clear();
        List<CustomerEnterRateTrendResp.Item> list = response.getCountList();
        if (list == null) {
            return;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            for (CustomerEnterRateTrendResp.Item item : list) {
                long time = format.parse(item.getTime()).getTime();
                float x = Utils.encodeChartXAxisFloat(model.period, time);
                float y = (float) item.getPassengerCount() / (item.getPassPassengerCount() + item.getPassengerCount());
                model.dataSet.add(new ChartEntry(x, y, time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Test data
//        model.random();

        LineChart line = holder.getView(R.id.view_dashboard_line_chart);

        // Get data set from model
        List<ChartEntry> dataSet = model.dataSet;
        if (dataSet == null) {
            dataSet = new ArrayList<>();
        }

        // Calculate min & max of axis value.
        Pair<Integer, Integer> xAxisRange = Utils.calcChartXAxisRange(model.period);
        int max = 0;
        float lastX = 0;
        for (ChartEntry entry : dataSet) {
            if (entry.getY() > 1) {
                entry.setY(1f);
            }
            if (entry.getX() > lastX) {
                lastX = entry.getX();
            }
            if (entry.getY() > max) {
                max = (int) Math.ceil(entry.getY());
            }
        }

        int color = ContextCompat.getColor(holder.getContext(), R.color.common_orange);
        int maxDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        lineXAxisRenderer.setPeriod(model.period, maxDay);
        line.getXAxis().setAxisMinimum(xAxisRange.first);
        line.getXAxis().setAxisMaximum(xAxisRange.second);

        // Use correct chart marker & update it.
        if (model.period == Constants.TIME_PERIOD_YESTERDAY || model.period == Constants.TIME_PERIOD_TODAY) {
            mMarkerFormatter.setTimeType(MarkerFormatter.TIME_TYPE_HOUR);
        } else {
            mMarkerFormatter.setTimeType(MarkerFormatter.TIME_TYPE_DATE);
        }

        // Refresh data set
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
            mLineChartMarker.setPointColor(color);
            data = new LineData(set);
            line.setData(data);
        }
        line.highlightValue(lastX, 0);
        line.animateX(300);
    }

    private void setupLineData(LineDataSet set, int color) {
        set.setColor(color);
        set.setCircleColor(color);
        set.setHighLightColor(color);
        set.setLineWidth(2f);
        set.setDrawValues(false);
        set.setDrawCircleHole(false);
        set.setCircleRadius(1f);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightLineWidth(1f);
        set.enableDashedHighlightLine(mDashLength, mDashSpaceLength, 0);
        set.setLineContinuous(false);
        set.setLinePhase(1f);
    }

    public static class Model extends BaseRefreshCard.BaseModel {

        private List<ChartEntry> dataSet = new ArrayList<>();

        @Override
        public void init(int source) {
            dataSet.clear();
        }

        public void random() {
            dataSet.clear();
            Random r = new Random(System.currentTimeMillis());
            int count = period == Constants.TIME_PERIOD_WEEK ? 5 : 20;
            int min = count / 3;
            long time = Utils.getStartTime(period);
            boolean inDay = period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY;
            for (int i = 1; i < count + 1; i++) {
                time = time + (i - 1) * (inDay ? 3600000 : 86400000);
                float x = Utils.encodeChartXAxisFloat(period, time);
                if (i <= min + 1) {
                    dataSet.add(new ChartEntry(x, 0f, time));
                } else {
                    dataSet.add(new ChartEntry(x, r.nextFloat(), time));
                }
            }
        }
    }
}
