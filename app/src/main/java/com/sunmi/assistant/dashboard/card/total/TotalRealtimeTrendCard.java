package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;
import com.sunmi.assistant.dashboard.ui.chart.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.TimeMarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelRenderer;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;

import org.threeten.bp.DateTimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.Interval;
import sunmi.common.model.TotalRealtimeCustomerTrendResp;
import sunmi.common.model.TotalRealtimeSalesTrendResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TotalRealtimeTrendCard extends BaseRefreshCard<TotalRealtimeTrendCard.Model, Object> {

    private static TotalRealtimeTrendCard sInstance;

    private static final int COLOR_CUSTOMER = 0xFF4B7AFA;
    private static final int COLOR_SALES = 0xFFFF6000;

    private static final int TYPE_CUSTOMER = 0;
    private static final int TYPE_SALES = 1;

    private XAxisLabelRenderer lineXAxisRenderer;
    private YAxisVolumeLabelsRenderer lineYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private TimeMarkerFormatter mMarkerFormatter;

    private TotalRealtimeTrendCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static TotalRealtimeTrendCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new TotalRealtimeTrendCard(presenter, condition, period, periodTime);
        } else {
            sInstance.reset(presenter, condition, period, periodTime);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_total_realtime_trend;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        holder.addOnClickListener(R.id.tv_dashboard_customer, (h, model, position) -> {
            if (model.type != TYPE_CUSTOMER) {
                model.type = TYPE_CUSTOMER;
                updateViews();
            }
        });
        holder.addOnClickListener(R.id.tv_dashboard_sales, (h, model, position) -> {
            if (model.type != TYPE_SALES) {
                model.type = TYPE_SALES;
                updateViews();
            }
        });

        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);
        // 设置图表基本属性
        Utils.setupLineChart(lineChart);

        // 设置图表渲染器，格式化器，自定义浮窗
        lineXAxisRenderer = new XAxisLabelRenderer(lineChart);
        lineXAxisRenderer.setPeriod(Constants.TIME_PERIOD_DAY, 0);
        lineYAxisRenderer = new YAxisVolumeLabelsRenderer(lineChart);
        lineChart.setXAxisRenderer(lineXAxisRenderer);
        lineChart.setRendererLeftYAxis(lineYAxisRenderer);

        lineChart.getXAxis().setValueFormatter(new XAxisLabelFormatter(context));
        lineChart.getAxisLeft().setValueFormatter(new YAxisVolumeLabelFormatter(context));

        mMarkerFormatter = new TimeMarkerFormatter(context);
        mMarkerFormatter.setTimeType(TimeMarkerFormatter.TIME_TYPE_HOUR_TOTAL_SPAN);
        mLineChartMarker = new LineChartMarkerView(context, mMarkerFormatter);
        mLineChartMarker.setChartView(lineChart);
        lineChart.setMarker(mLineChartMarker);

        return holder;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
        if (mCondition.hasFs) {
            loadCustomer(companyId, callback);
        } else if (mCondition.hasSaas) {
            loadSales(companyId, callback);
        }
        return null;
    }

    private void loadCustomer(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeCustomerTrend(companyId,
                new RetrofitCallback<TotalRealtimeCustomerTrendResp>() {
                    @Override
                    public void onSuccess(int code, String msg, TotalRealtimeCustomerTrendResp data) {
                        if (data == null || data.getList() == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        List<ChartEntry> dataSet = getModel().dataSets.get(TYPE_CUSTOMER);
                        dataSet.clear();
                        List<TotalRealtimeCustomerTrendResp.Item> list = data.getList();
                        try {
                            for (TotalRealtimeCustomerTrendResp.Item item : list) {
                                long time = Utils.parseTime(Utils.FORMAT_API_TIME, item.getTime()) + Utils.MILLIS_OF_HOUR;
                                float x = Utils.encodeChartXAxisFloat(callback.getPeriod(), time);
                                dataSet.add(new ChartEntry(x, item.getTotalCount(), time));
                            }
                        } catch (DateTimeException e) {
                            e.printStackTrace();
                        }
                        dataSet.add(new ChartEntry(1f, 0f, DateTimeUtils.getToday().getTime()));
                        Collections.sort(dataSet, (o1, o2) -> Long.compare(o1.getTime(), o2.getTime()));
                        float total = 0;
                        for (ChartEntry entry : dataSet) {
                            total += entry.getY();
                            entry.setY(total);
                        }
                        if (mCondition.hasSaas) {
                            loadSales(companyId, callback);
                        } else {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, TotalRealtimeCustomerTrendResp data) {
                        callback.onFail(code, msg, null);
                    }
                });
    }

    private void loadSales(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeSalesTrend(companyId,
                new RetrofitCallback<TotalRealtimeSalesTrendResp>() {
                    @Override
                    public void onSuccess(int code, String msg, TotalRealtimeSalesTrendResp data) {
                        if (data == null || data.getList() == null) {
                            onFail(code, msg, data);
                            return;
                        }
                        List<ChartEntry> dataSet = getModel().dataSets.get(TYPE_SALES);
                        dataSet.clear();
                        List<TotalRealtimeSalesTrendResp.Item> list = data.getList();
                        try {
                            for (TotalRealtimeSalesTrendResp.Item item : list) {
                                long time = Utils.parseTime(Utils.FORMAT_API_TIME, item.getTime()) + Utils.MILLIS_OF_HOUR;
                                float x = Utils.encodeChartXAxisFloat(callback.getPeriod(), time);
                                dataSet.add(new ChartEntry(x, (float) Math.max(0, item.getOrderAmount()), time));
                            }
                        } catch (DateTimeException e) {
                            e.printStackTrace();
                        }
                        dataSet.add(new ChartEntry(1f, 0f, DateTimeUtils.getToday().getTime()));
                        Collections.sort(dataSet, (o1, o2) -> Long.compare(o1.getTime(), o2.getTime()));
                        float total = 0;
                        for (ChartEntry entry : dataSet) {
                            total += entry.getY();
                            entry.setY(total);
                        }
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, TotalRealtimeSalesTrendResp data) {
                        callback.onFail(code, msg, null);
                    }
                });
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        LineChart line = holder.getView(R.id.view_dashboard_line_chart);

        // 设置Tab样式
        setupLabelState(holder, model);

        // Get data set from model
        List<ChartEntry> dataSet = model.dataSets.get(model.type);
        if (dataSet == null) {
            dataSet = new ArrayList<>();
            model.dataSets.put(model.type, dataSet);
        }

        // Calculate min & max of axis value.
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

        Pair<Integer, Integer> xAxisRange = Utils.calcChartXAxisRange(Constants.TIME_PERIOD_DAY);
        line.getXAxis().setAxisMinimum(xAxisRange.first);
        line.getXAxis().setAxisMaximum(xAxisRange.second + 1);
        float maxAxis = lineYAxisRenderer.setMaxValue(max);
        line.getAxisLeft().setAxisMaximum(maxAxis);

        // Get color of line
        int color;
        int markerTitle;

        if (model.type == TYPE_SALES) {
            color = COLOR_SALES;
            markerTitle = R.string.dashboard_var_sales_amount;
        } else {
            color = COLOR_CUSTOMER;
            markerTitle = R.string.dashboard_var_customer_volume;
        }

        // Use correct chart marker & update it.
        mLineChartMarker.setTitle(markerTitle);
        mLineChartMarker.setPointColor(color);

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
            Utils.setupLineChartDataSet(holder.getContext(), set, color);
            data = new LineData(set);
            line.setData(data);
        }
        line.highlightValue(lastX, 0);
        line.animateX(300);
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.dataSets.get(model.type).clear();
        setupView(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.dataSets.get(model.type).clear();
        setupView(holder, model, position);
    }

    private void setupLabelState(@NonNull BaseViewHolder<Model> holder, Model model) {
        // Get views
        TextView tabCustomer = holder.getView(R.id.tv_dashboard_customer);
        TextView tabSales = holder.getView(R.id.tv_dashboard_sales);

        // Set button selected
        tabSales.setVisibility(mCondition.hasSaas ? View.VISIBLE : View.GONE);
        tabCustomer.setVisibility(mCondition.hasFs ? View.VISIBLE : View.GONE);
        tabCustomer.setSelected(model.type == TYPE_CUSTOMER);
        tabCustomer.setTypeface(null, model.type == TYPE_CUSTOMER ? Typeface.BOLD : Typeface.NORMAL);
        tabSales.setSelected(model.type == TYPE_SALES);
        tabSales.setTypeface(null, model.type == TYPE_SALES ? Typeface.BOLD : Typeface.NORMAL);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private int type;
        private SparseArray<List<ChartEntry>> dataSets = new SparseArray<>(2);

        public Model() {
            dataSets.put(TYPE_CUSTOMER, new ArrayList<>());
            dataSets.put(TYPE_SALES, new ArrayList<>());
        }

        @Override
        public void init(DashboardCondition condition) {
            type = condition.hasSaas ? TYPE_SALES : TYPE_CUSTOMER;
            for (int i = 0, size = dataSets.size(); i < size; i++) {
                int key = dataSets.keyAt(i);
                dataSets.get(key).clear();
            }
        }

    }
}
