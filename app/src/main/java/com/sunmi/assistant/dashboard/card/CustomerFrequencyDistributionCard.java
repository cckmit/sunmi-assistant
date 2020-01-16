package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.ui.chart.BarChartRoundEdgeRenderer;
import com.sunmi.assistant.dashboard.ui.chart.XAxisFrequencyDistributionFormatter;
import com.sunmi.assistant.dashboard.ui.chart.YAxisVolumeLabelsRenderer;

import java.util.ArrayList;
import java.util.List;

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
    private XAxisFrequencyDistributionFormatter barXAxisFormatter;
    private int paddingBottom;


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
        barXAxisFormatter = new XAxisFrequencyDistributionFormatter(context);
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
        barXAxis.setValueFormatter(barXAxisFormatter);
        barXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barXAxis.setAxisMinimum(0f);

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
        int max, count = 0;
        if (model.period == Constants.TIME_PERIOD_YESTERDAY) {
            max = 4;
        } else if (model.period == Constants.TIME_PERIOD_WEEK) {
            max = 10;
        } else {
            max = 15;
        }
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
        } else {
            layout.setBackgroundResource(R.drawable.dashboard_bg_top_white_radius);
            model.setPadding(0, 0, 0, 0);
        }
        List<BarEntry> dataSet = model.dataSet;
        int maxValue = 0;
        float lastX = 0;
        for (BarEntry entry : dataSet) {
            if (entry.getX() > lastX) {
                lastX = entry.getX();
            }
            if (entry.getY() > maxValue) {
                maxValue = (int) Math.ceil(entry.getY());
            }
        }

        //更新横纵坐标
        float maxAxis = barYAxisRenderer.setMaxValue(maxValue);
        chart.getAxisLeft().setAxisMaximum(maxAxis);
        chart.getXAxis().setAxisMaximum(dataSet.size() + 1);
        barXAxisFormatter.setPeriod(model.period);

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
            set.setDrawValues(true);
            set.setColor(ContextCompat.getColor(holder.getContext(), R.color.color_FFD0B3));
            set.setHighLightColor(ContextCompat.getColor(holder.getContext(), R.color.common_orange));
            data = new BarData(set);
            data.setBarWidth(barWidthRatio);
            chart.setData(data);
        }
        chart.highlightValue(lastX, 0);
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
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return 0.35f;
        } else {
            return 0.45f;
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
