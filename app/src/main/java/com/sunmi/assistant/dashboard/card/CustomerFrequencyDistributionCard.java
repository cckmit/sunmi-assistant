package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.ui.chart.BarChartRoundEdgeRenderer;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelFormatter;
import com.sunmi.assistant.dashboard.ui.chart.XAxisLabelRenderer;
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
        XAxisLabelRenderer barXAxisRenderer = new XAxisLabelRenderer(chart);
        YAxisVolumeLabelsRenderer barYAxisRenderer = new YAxisVolumeLabelsRenderer(chart);
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


    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        BarChart chart = holder.getView(R.id.view_dashboard_bar_chart);
        Context context = holder.getContext();


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
