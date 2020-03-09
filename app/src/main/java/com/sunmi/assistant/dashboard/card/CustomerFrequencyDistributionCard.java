package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.ui.chart.BarChartMarkerView;
import com.sunmi.assistant.dashboard.ui.chart.BarChartRoundEdgeRenderer;
import com.sunmi.assistant.dashboard.ui.chart.IMarkerFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisFrequencyDistributionFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerFrequencyDistributionResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.CommonHelper;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-15.
 */
public class CustomerFrequencyDistributionCard extends BaseRefreshCard<CustomerFrequencyDistributionCard.Model,
        CustomerFrequencyDistributionResp> {

    private static CustomerFrequencyDistributionCard sInstance;
    private YAxisVolumeLabelsRenderer barYAxisRenderer;
    private XAxisLabelRenderer barXAxisRenderer;
    private XAxisFrequencyDistributionFormatter barXAxisFormatter;
    private int paddingBottom;
    private MarkerFormatter markerFormatter;


    private CustomerFrequencyDistributionCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyDistributionCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyDistributionCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_customer_frequency_distribution;
    }

    @Override
    public void init(Context context) {
        paddingBottom = (int) context.getResources().getDimension(R.dimen.dp_24);
    }

    @Override
    protected Call<BaseResponse<CustomerFrequencyDistributionResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerFrequencyDistribution(companyId, shopId, period, callback);
        return null;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = view.getContext();
        BarChart chart = holder.getView(R.id.view_dashboard_bar_chart);

        // 设置图表坐标Label格式
        barYAxisRenderer = new YAxisVolumeLabelsRenderer(chart);
        barXAxisRenderer = new XAxisLabelRenderer(chart);
        barXAxisFormatter = new XAxisFrequencyDistributionFormatter(context);
        chart.setRendererLeftYAxis(barYAxisRenderer);
        chart.setXAxisRenderer(barXAxisRenderer);

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
        chart.setFitBars(true);
        chart.setDrawBarShadow(false);

        // 设置X轴
        XAxis barXAxis = chart.getXAxis();
        barXAxis.setDrawAxisLine(true);
        barXAxis.setDrawGridLines(false);
        barXAxis.setTextSize(10f);
        barXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        barXAxis.setValueFormatter(barXAxisFormatter);
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
        barYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        barYAxis.setYOffset(-5f);
        barYAxis.setXOffset(-1f);
        barYAxis.setValueFormatter(new YAxisVolumeLabelFormatter(context));

        // 设置Marker和Bar样式
        float barRadius = CommonHelper.dp2px(context, 2f);
        BarChartRoundEdgeRenderer renderer = new BarChartRoundEdgeRenderer(chart, barRadius);
        chart.setRenderer(renderer);
        markerFormatter = new MarkerFormatter(context);
        BarChartMarkerView marker = new BarChartMarkerView(context, markerFormatter);
        marker.setTitle(R.string.dashboard_card_customer_frequency_marker_title);
        marker.setChartView(chart);
        chart.setMarker(marker);
        return holder;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyDistributionResp response) {
        model.dataSet.clear();
        if (response == null || response.getFrequencyList() == null) {
            return;
        }
        int count = 0;
        int max = getMax(model.period);
        List<CustomerFrequencyDistributionResp.Item> items = response.getFrequencyList();
        for (CustomerFrequencyDistributionResp.Item item : items) {
            if (item.getFrequency() <= max) {
                model.dataSet.add(new BarEntry(item.getFrequency(), item.getUniqPassengerCount()));
            } else {
                count += item.getUniqPassengerCount();
            }
        }
        model.dataSet.add(new BarEntry(max + 1, count));
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        BarChart chart = holder.getView(R.id.view_dashboard_bar_chart);
        ConstraintLayout layout = holder.getView(R.id.layout_frequency_chart);
        if (model.period == Constants.TIME_PERIOD_YESTERDAY) {
            layout.setBackgroundResource(R.drawable.dashboard_bg_white_radius);
            model.setPadding(0, 0, 0, paddingBottom);
            chart.getXAxis().setAxisMinimum(0f);
        } else {
            layout.setBackgroundResource(R.drawable.dashboard_bg_top_white_radius);
            model.setPadding(0, 0, 0, 0);
            chart.getXAxis().setAxisMinimum(-0.5f);
        }
        List<BarEntry> dataSet = model.dataSet;
        int maxValue = 0;
        float lastX = 0;
        int index = 0;
        for (BarEntry entry : dataSet) {
            if (entry.getY() > maxValue) {
                maxValue = (int) Math.ceil(entry.getY());
                lastX = entry.getX();
                index = dataSet.indexOf(entry);
            }
        }

        //更新横纵坐标
        float maxAxis = barYAxisRenderer.setMaxValue(maxValue);
        chart.getAxisLeft().setAxisMaximum(maxAxis);
        int max = getMax(model.period);
        chart.getXAxis().setAxisMaximum(max + 2);
        barXAxisFormatter.setPeriod(model.period);
        barXAxisRenderer.setPeriod(model.period);
        markerFormatter.setMax(max);

        //更新数据
        float barWidthRatio = calcBarWidth(model.period);
        BarDataSet set;
        BarData data = chart.getData();
        if (data != null && data.getDataSetCount() > 0) {
            set = (BarDataSet) data.getDataSetByIndex(0);
            set.setValues(dataSet);
            data.setBarWidth(barWidthRatio);
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(dataSet, "data");
            set.setDrawValues(false);
            set.setColor(ContextCompat.getColor(holder.getContext(), R.color.color_FFD0B3));
            set.setHighLightColor(ContextCompat.getColor(holder.getContext(), R.color.common_orange));
            data = new BarData(set);
            data.setBarWidth(barWidthRatio);
            chart.setData(data);
        }
        chart.highlightValue(lastX, index);
        chart.animateY(300, Easing.EaseOutCubic);

    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.period = mPeriod;
        model.dataSet.clear();
        super.showLoading(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        model.period = mPeriod;
        model.dataSet.clear();
        super.showError(holder, model, position);
    }

    private float calcBarWidth(int period) {
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            return 0.25f;
        } else {
            return 0.45f;
        }
    }

    private int getMax(int period) {
        int max;
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            max = 4;
        } else {
            max = 10;
        }
        return max;
    }

    private static class MarkerFormatter implements IMarkerFormatter {

        private int max;
        private String valueFormat;
        private String labelFormat;
        private String labelAboveFormat;

        private MarkerFormatter(Context context) {
            valueFormat = context.getString(R.string.str_num_people);
            labelFormat = context.getString(R.string.dashboard_card_customer_frequency_marker_value);
            labelAboveFormat = context.getString(R.string.dashboard_card_customer_frequency_marker_count_above);
        }

        private void setMax(int max) {
            this.max = max;
        }

        @Override
        public CharSequence valueFormat(Context context, float value) {
            String str = String.format(Locale.getDefault(), valueFormat, (int) value);
            SpannableString s = new SpannableString(str);
            s.setSpan(new RelativeSizeSpan(0.6f), s.length() - 1, s.length(), 0);
            return s;
        }

        @Override
        public CharSequence xAxisFormat(Context context, float x) {
            if (x <= max) {
                return String.format(Locale.getDefault(), labelFormat, (int) x);
            } else {
                return String.format(Locale.getDefault(), labelAboveFormat, (int) x - 1);
            }
        }

        @Override
        public CharSequence timeFormat(Context context, long time) {
            return "";
        }
    }

    private static class XAxisLabelRenderer extends XAxisRenderer {

        private float[] labels;

        private XAxisLabelRenderer(BarLineChartBase chart) {
            super(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT));
        }

        private void setPeriod(int period) {
            if (period == Constants.TIME_PERIOD_YESTERDAY) {
                labels = new float[]{1, 2, 3, 4, 5};
            } else {
                labels = new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            }
        }

        @Override
        protected void computeAxisValues(float min, float max) {
            if (labels == null) {
                super.computeAxisValues(min, max);
                return;
            }
            mAxis.mEntryCount = labels.length;
            mAxis.mEntries = labels;
            mAxis.setCenterAxisLabels(false);

            computeSize();
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private List<BarEntry> dataSet = new ArrayList<>();

        @Override
        public void init(int source) {
            super.init(source);
            dataSet.clear();
        }
    }
}
