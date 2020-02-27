package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;
import com.sunmi.assistant.dashboard.ui.chart.CustomerLineMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.TimeMarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelRenderer;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.exception.TimeDateException;
import sunmi.common.model.CustomerHistoryTrendResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TotalRealTimeTrendCard extends BaseRefreshCard<TotalRealTimeTrendCard.Model, CustomerHistoryTrendResp> {

    private static TotalRealTimeTrendCard sInstance;

    private static final long MILLIS_PER_HOUR = 3600000;
    private static final long MILLIS_PER_DAY = 3600000 * 24;

    private static final int MAX_POINT_DAY = 25;
    private static final int MAX_POINT_WEEK = 8;

    private static final int COLOR_ALL = 0xFF00BC7D;
    private static final int COLOR_NEW = 0xFF5A97FC;
    private static final int COLOR_OLD = 0xFFFF8000;

    private XAxisLabelRenderer lineXAxisRenderer;
    private YAxisVolumeLabelsRenderer lineYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private CustomerLineMarkerView mLineComplexMarker;
    private TimeMarkerFormatter mMarkerFormatter;
    private float mDashLength;
    private float mDashSpaceLength;

    private TotalRealTimeTrendCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static TotalRealTimeTrendCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new TotalRealTimeTrendCard(presenter, source);
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
        return R.layout.dashboard_item_customer_trend;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);
        holder.addOnClickListener(R.id.tv_dashboard_all, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_ALL) {
                model.type = Constants.DATA_TYPE_ALL;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_new, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_NEW) {
                model.type = Constants.DATA_TYPE_NEW;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_old, (h, model, position) -> {
            if (model.type != Constants.DATA_TYPE_OLD) {
                model.type = Constants.DATA_TYPE_OLD;
                updateViews();
            }
        });

        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);

        // 设置图表坐标Label格式
        lineXAxisRenderer = new XAxisLabelRenderer(lineChart);
        lineYAxisRenderer = new YAxisVolumeLabelsRenderer(lineChart);
        lineChart.setXAxisRenderer(lineXAxisRenderer);
        lineChart.setRendererLeftYAxis(lineYAxisRenderer);

        // 设置通用图表
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        // 设置X轴
        XAxis lineXAxis = lineChart.getXAxis();
        lineXAxis.setDrawAxisLine(true);
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setTextSize(10f);
        lineXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineXAxis.setValueFormatter(new XAxisLabelFormatter(context));
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis lineYAxis = lineChart.getAxisLeft();
        lineYAxis.setDrawAxisLine(false);
        lineYAxis.setGranularityEnabled(true);
        lineYAxis.setGranularity(1f);
        lineYAxis.setTextSize(10f);
        lineYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineYAxis.setAxisMinimum(0f);
        lineYAxis.setDrawGridLines(true);
        lineYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        lineYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        lineYAxis.setYOffset(-5f);
        lineYAxis.setXOffset(-1f);
        lineYAxis.setValueFormatter(new YAxisVolumeLabelFormatter(context));

        // 设置Line图
        mMarkerFormatter = new TimeMarkerFormatter(context);
        mLineChartMarker = new LineChartMarkerView(context, mMarkerFormatter);
        mLineComplexMarker = new CustomerLineMarkerView(context);
        mLineChartMarker.setChartView(lineChart);
        mLineComplexMarker.setChartView(lineChart);
        lineChart.setMarker(mLineComplexMarker);

        return holder;
    }

    @Override
    protected Call<BaseResponse<CustomerHistoryTrendResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        String group = "day";
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            group = "hour";
        }
        SunmiStoreApi.getInstance().getHistoryCustomerTrend(companyId, shopId, period, group,
                new RetrofitCallback<CustomerHistoryTrendResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerHistoryTrendResp data) {
                        if (data == null || data.getCountList() == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        callback.onSuccess(code, msg, data);
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerHistoryTrendResp data) {
                        if (code == Constants.NO_CUSTOMER_DATA) {
                            callback.onSuccess(code, msg, data);
                        } else {
                            callback.onFail(code, msg, data);
                        }
                    }
                });
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model("");
    }

    private void initValue(int period, SparseArray<ChartEntry> allMap,
                           SparseArray<ChartEntry> newMap,
                           SparseArray<ChartEntry> oldMap) {
        long startTime = Utils.getStartTime(period);
        long yesterday = System.currentTimeMillis() - MILLIS_PER_DAY;
        long time;
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            for (int i = 1; i < MAX_POINT_DAY; i++) {
                time = startTime + (i - 1) * MILLIS_PER_HOUR;
                float x = Utils.encodeChartXAxisFloat(period, time);
                allMap.put((int) x, new CustomerEntry(x, 0f, time, 0, 0));
                newMap.put((int) x, new ChartEntry(x, 0, time));
                oldMap.put((int) x, new ChartEntry(x, 0, time));
            }

        } else if (period == Constants.TIME_PERIOD_WEEK) {
            for (int i = 1; i < MAX_POINT_WEEK; i++) {
                time = startTime + (i - 1) * MILLIS_PER_DAY;
                if (time > yesterday) {
                    break;
                }
                float x = Utils.encodeChartXAxisFloat(period, time);
                allMap.put((int) x, new CustomerEntry(x, 0f, time, 0, 0));
                newMap.put((int) x, new ChartEntry(x, 0, time));
                oldMap.put((int) x, new ChartEntry(x, 0, time));
            }
        } else {
            int max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) + 1;
            for (int i = 1; i < max; i++) {
                time = startTime + (i - 1) * MILLIS_PER_DAY;
                if (time > yesterday) {
                    break;
                }
                float x = Utils.encodeChartXAxisFloat(period, time);
                allMap.put((int) x, new CustomerEntry(x, 0f, time, 0, 0));
                newMap.put((int) x, new ChartEntry(x, 0, time));
                oldMap.put((int) x, new ChartEntry(x, 0, time));
            }
        }
    }

    private void fillValue(List<ChartEntry> list, SparseArray<ChartEntry> map) {
        for (int i = 0, size = map.size(); i < size; i++) {
            list.add(map.valueAt(i));
        }
    }

    @Override
    protected void setupModel(Model model, CustomerHistoryTrendResp response) {
        List<ChartEntry> allList = model.dataSets.get(Constants.DATA_TYPE_ALL);
        List<ChartEntry> newList = model.dataSets.get(Constants.DATA_TYPE_NEW);
        List<ChartEntry> oldList = model.dataSets.get(Constants.DATA_TYPE_OLD);
        allList.clear();
        newList.clear();
        oldList.clear();

        SparseArray<ChartEntry> allMap = new SparseArray<>(31);
        SparseArray<ChartEntry> newMap = new SparseArray<>(31);
        SparseArray<ChartEntry> oldMap = new SparseArray<>(31);

//        initValue(model.period, allMap, newMap, oldMap);
        if (response == null || response.getCountList() == null || response.getCountList().isEmpty()) {
            fillValue(allList, allMap);
            fillValue(newList, newMap);
            fillValue(oldList, oldMap);
            return;
        }

        try {
            response.init(model.period);
            List<CustomerHistoryTrendResp.Item> list = response.getCountList();
            for (CustomerHistoryTrendResp.Item item : list) {
                long time = item.getTime();
                float x = Utils.encodeChartXAxisFloat(model.period, time);
                allMap.put((int) x, new CustomerEntry(x, item.getTotalCount(), time,
                        item.getStrangerCount(), item.getRegularCount()));
                newMap.put((int) x, new ChartEntry(x, item.getStrangerCount(), time));
                oldMap.put((int) x, new ChartEntry(x, item.getRegularCount(), time));
            }
        } catch (TimeDateException e) {
            LogCat.e(TAG, e.getMessage());
            LogCat.d(TAG, "Code: " + e.getCode() + ", " + e.getDetail());
        }

        fillValue(allList, allMap);
        fillValue(newList, newMap);
        fillValue(oldList, oldMap);
    }

    private void setupLabelState(@NonNull BaseViewHolder<Model> holder, Model model) {
        // Get views
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView all = holder.getView(R.id.tv_dashboard_all);
        TextView newCustomer = holder.getView(R.id.tv_dashboard_new);
        TextView oldCustomer = holder.getView(R.id.tv_dashboard_old);

        // Set button selected
        all.setSelected(model.type == Constants.DATA_TYPE_ALL);
        all.setTypeface(null, model.type == Constants.DATA_TYPE_ALL ? Typeface.BOLD : Typeface.NORMAL);
        newCustomer.setSelected(model.type == Constants.DATA_TYPE_NEW);
        newCustomer.setTypeface(null, model.type == Constants.DATA_TYPE_NEW ? Typeface.BOLD : Typeface.NORMAL);
        oldCustomer.setSelected(model.type == Constants.DATA_TYPE_OLD);
        oldCustomer.setTypeface(null, model.type == Constants.DATA_TYPE_OLD ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void setupLineData(LineDataSet set, int color) {
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setColor(color);
        set.setHighLightColor(color);
        set.setLineWidth(2f);
        set.setHighlightLineWidth(1f);
        set.enableDashedHighlightLine(mDashLength, mDashSpaceLength, 0);
        set.setLineContinuous(false);
        set.setLinePhase(1f);
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        // Test data
//        model.random();

        setupLabelState(holder, model);
        LineChart line = holder.getView(R.id.view_dashboard_line_chart);

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
            if (entry.getX() > lastX) {
                lastX = entry.getX();
            }
            if (entry.getY() > max) {
                max = (int) Math.ceil(entry.getY());
            }
        }

        int maxDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        lineXAxisRenderer.setPeriod(model.period, maxDay);
        line.getXAxis().setAxisMinimum(xAxisRange.first);
        line.getXAxis().setAxisMaximum(xAxisRange.second);
        float maxAxis = lineYAxisRenderer.setMaxValue(max);
        line.getAxisLeft().setAxisMaximum(maxAxis);

        // Get color of line
        int color;
        int markerTitle;
        if (model.type == Constants.DATA_TYPE_NEW) {
            color = COLOR_NEW;
            markerTitle = R.string.dashboard_card_tab_new;
        } else if (model.type == Constants.DATA_TYPE_OLD) {
            color = COLOR_OLD;
            markerTitle = R.string.dashboard_card_tab_old;
        } else {
            color = COLOR_ALL;
            markerTitle = R.string.dashboard_card_tab_all;
        }

        // Use correct chart marker & update it.
        if (model.type == Constants.DATA_TYPE_ALL) {
            line.setMarker(mLineComplexMarker);
            mLineComplexMarker.setPointColor(color);
            mLineComplexMarker.setPeriod(model.period);
        } else {
            line.setMarker(mLineChartMarker);
            mLineChartMarker.setTitle(markerTitle);
            mLineChartMarker.setPointColor(color);
            if (model.period == Constants.TIME_PERIOD_YESTERDAY || model.period == Constants.TIME_PERIOD_TODAY) {
                mMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_HOUR_SPAN);
            } else {
                mMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_DATE);
            }
        }

        // Refresh data set
        LineDataSet set;
        LineData data = line.getData();
        ArrayList<Entry> values = new ArrayList<>(dataSet);
        if (data != null && data.getDataSetCount() > 0) {
            set = (LineDataSet) data.getDataSetByIndex(0);
            set.setColor(color);
            set.setHighLightColor(color);
            set.setValues(values);
            data.notifyDataChanged();
            line.notifyDataSetChanged();
        } else {
            set = new LineDataSet(values, "data");
            setupLineData(set, color);
            data = new LineData(set);
            line.setData(data);
        }
        line.highlightValue(lastX, 0);
        line.animateX(300);
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

    public static class CustomerEntry extends ChartEntry {
        private int newCustomer;
        private int oldCustomer;

        public CustomerEntry(float x, float y, long time, int newCustomer, int oldCustomer) {
            super(x, y, time);
            this.newCustomer = newCustomer;
            this.oldCustomer = oldCustomer;
        }

        public int getNewCustomer() {
            return newCustomer;
        }

        public int getOldCustomer() {
            return oldCustomer;
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private String title;
        private int type;
        private SparseArray<List<ChartEntry>> dataSets = new SparseArray<>(3);

        public Model(String title) {
            this.title = title;
            dataSets.put(Constants.DATA_TYPE_ALL, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_NEW, new ArrayList<>());
            dataSets.put(Constants.DATA_TYPE_OLD, new ArrayList<>());
        }

        @Override
        public void init(int source) {
            type = Constants.DATA_TYPE_ALL;
            for (int i = 0, size = dataSets.size(); i < size; i++) {
                int key = dataSets.keyAt(i);
                dataSets.get(key).clear();
            }
        }

        public void random() {
            List<ChartEntry> allList = dataSets.get(Constants.DATA_TYPE_ALL);
            List<ChartEntry> newList = dataSets.get(Constants.DATA_TYPE_NEW);
            List<ChartEntry> oldList = dataSets.get(Constants.DATA_TYPE_OLD);
            allList.clear();
            newList.clear();
            oldList.clear();
            Random r = new Random(System.currentTimeMillis());
            int count = period == Constants.TIME_PERIOD_WEEK ? 5 : 20;
            int min = count / 3;
            long time = Utils.getStartTime(period);
            boolean inDay = period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY;
            for (int i = 1; i < count + 1; i++) {
                time = time + (i - 1) * (inDay ? 3600000 : 86400000);
                float x = Utils.encodeChartXAxisFloat(period, time);
                if (i <= min + 1) {
                    allList.add(new CustomerEntry(x, 0f, time, 0, 0));
                    newList.add(new ChartEntry(x, 0f, time));
                    oldList.add(new ChartEntry(x, 0f, time));
                } else {
                    int n = r.nextInt(1000);
                    int o = r.nextInt(1000);
                    allList.add(new CustomerEntry(x, n + o, time, 0, 0));
                    newList.add(new ChartEntry(x, n, time));
                    oldList.add(new ChartEntry(x, o, time));
                }
            }
        }

    }
}
