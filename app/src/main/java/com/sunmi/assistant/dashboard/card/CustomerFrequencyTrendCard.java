package com.sunmi.assistant.dashboard.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;
import com.sunmi.assistant.dashboard.ui.chart.LineChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.MarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerFrequencyTrendResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-16.
 */
public class CustomerFrequencyTrendCard extends BaseRefreshCard<CustomerFrequencyTrendCard.Model, CustomerFrequencyTrendResp> {

    private static CustomerFrequencyTrendCard sInstance;

    private XAxisValueFormatter lineXAxisFormatter;
    private YAxisVolumeLabelsRenderer lineYAxisRenderer;
    private LineChartMarkerView mLineChartMarker;
    private MarkerFormatter mMarkerFormatter;

    private float mDashLength;
    private float mDashSpaceLength;

    private CustomerFrequencyTrendCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyTrendCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyTrendCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_customer_frequency_trend;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    protected Call<BaseResponse<CustomerFrequencyTrendResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        String group;
        if (period == Constants.TIME_PERIOD_MONTH) {
            group = "week";
        } else {
            group = "day";
        }
        SunmiStoreApi.getInstance().getCustomerFrequencyTrend(companyId, shopId, period, group, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyTrendResp response) {
        // 清空原有数据
        model.dataSet.clear();
        if (response == null || response.getFrequencyList() == null) {
            return;
        }
        // 遍历Response并创建Entry
        List<CustomerFrequencyTrendResp.Item> list = response.getFrequencyList();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            for (CustomerFrequencyTrendResp.Item item : list) {
                long time = format.parse(item.getTime()).getTime();
                int unique = item.getUniqPassengerCount();
                float y = unique <= 0 ? 0f : (float) item.getPassengerCount() / unique;
                model.dataSet.add(new ChartEntry(0f, y, time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 对Entry进行排序并填充x值
        Collections.sort(model.dataSet, (o1, o2) -> Long.compare(o1.getTime(), o2.getTime()));
        int i = 0;
        for (ChartEntry entry : model.dataSet) {
            entry.setX(++i);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();

        mDashLength = CommonHelper.dp2px(context, 4f);
        mDashSpaceLength = CommonHelper.dp2px(context, 2f);

        LineChart lineChart = holder.getView(R.id.view_dashboard_line_chart);

        // 设置图表坐标Label格式
        lineXAxisFormatter = new XAxisValueFormatter(context);
        lineYAxisRenderer = new YAxisVolumeLabelsRenderer(lineChart);
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
        lineXAxis.setValueFormatter(lineXAxisFormatter);
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
        lineYAxis.setMinWidth(36f);

        // 设置Line图
        mMarkerFormatter = new MarkerFormatter(context);
        mLineChartMarker = new LineChartMarkerView(context, mMarkerFormatter);
        mLineChartMarker.setChartView(lineChart);
        lineChart.setMarker(mLineChartMarker);

        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView tvTitle = holder.getView(R.id.tv_dashboard_title);
        LineChart chart = holder.getView(R.id.view_dashboard_line_chart);


    }

    private static class XAxisValueFormatter extends ValueFormatter {

        private String[] weekName;
        private String weekCountName;
        private int period = Constants.TIME_PERIOD_WEEK;

        public XAxisValueFormatter(Context context) {
            weekName = context.getResources().getStringArray(R.array.week_name);
            weekCountName = context.getString(R.string.dashboard_card_customer_week_count);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if (period == Constants.TIME_PERIOD_WEEK) {
                value = value == 7 ? 0 : value;
                return weekName[(int) value];
            } else {
                return String.format(Locale.getDefault(), weekCountName, (int) value);
            }
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private List<ChartEntry> dataSet = new ArrayList<>();
    }
}
