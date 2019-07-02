package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.ui.PieChartDataUpdateAnim;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderPayTypeRankResp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class PayMethodCard extends BaseRefreshCard<PayMethodCard.Model> {

    private static final String TAG = "PayMethodCard";

    private static final String HOLDER_TAG_LEGENDS = "legends";
    private static final String HOLDER_TAG_LEGENDS_DATA = "legends_data";

    private static final Integer[] PIE_COLORS = {
            0xFF2997FF, 0xFF09E896, 0xFFED9600, 0xFFFFA100, 0xFFFC5656, 0xFF8766FF
    };

    public PayMethodCard(Context context) {
        super(context);
    }

    @Override
    protected Model createData() {
        return new Model(mContext.getString(R.string.dashboard_purchase_rank),
                DashboardContract.DATA_MODE_SALES);
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new PayMethodType();
    }

    @Override
    protected void onPeriodChange(int period) {
    }

    @Override
    public void reload(int companyId, int shopId, int period, Pair<Long, Long> periodTimestamp, Model model) {
        Log.d(TAG, "HTTP request pay method rank.");
        SunmiStoreRemote.get().getOrderPurchaseTypeRank(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second,
                new CardCallback<OrderPayTypeRankResp>() {
                    @Override
                    public void success(OrderPayTypeRankResp data) {
                        Log.d(TAG, "HTTP request pay method rank success.");
                        List<OrderPayTypeRankResp.PayTypeRankItem> list = data.getPurchase_type_list();
                        Collections.sort(list, (o1, o2) ->
                                getIndexOfPayMethod(o1.getPurchase_type_name()) - getIndexOfPayMethod(o2.getPurchase_type_name()));
                        int size = list.size();
                        List<PieEntry> amountList = new ArrayList<>(size);
                        List<PieEntry> countList = new ArrayList<>(size);
                        float totalAmount = 0;
                        float totalCount = 0;
                        float otherAmount = 0;
                        float otherCount = 0;

                        for (int i = size - 1; i >= 0; i--) {
                            OrderPayTypeRankResp.PayTypeRankItem item = list.get(i);
                            totalAmount += item.getAmount();
                            totalCount += item.getCount();
                            if (size > 6 && i >= 5) {
                                otherAmount += item.getAmount();
                                otherCount += item.getCount();
                                list.remove(i);
                            }
                        }
                        for (OrderPayTypeRankResp.PayTypeRankItem item : list) {
                            String label = item.getPurchase_type_name();
                            amountList.add(new PieEntry(item.getAmount() / totalAmount, label));
                            countList.add(new PieEntry((float) (item.getCount()) / totalCount, label));
                        }
                        if (size > 6) {
                            amountList.add(new PieEntry(otherAmount / totalAmount, ""));
                            countList.add(new PieEntry(otherCount / totalCount, ""));
                        }
                        model.dataSets.put(DashboardContract.DATA_MODE_SALES, amountList);
                        model.dataSets.put(DashboardContract.DATA_MODE_ORDER, countList);
                    }
                });
    }

    private int getIndexOfPayMethod(String name) {
        switch (name) {
            case "支付宝":
                return 0;
            case "微信":
                return 1;
            case "现金":
                return 2;
            case "银行卡刷卡":
                return 3;
            case "银联二维码":
                return 4;
            case "其他":
                return 1000;
            default:
                return 100;
        }
    }

    public class PayMethodType extends ItemType<Model, BaseViewHolder<Model>> {

        @Override
        public int getLayoutId(int type) {
            return R.layout.dashboard_recycle_item_chart_pie;
        }

        @Override
        public int getSpanSize() {
            return 2;
        }

        @NonNull
        @Override
        public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
            BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
            PieChart chart = holder.getView(R.id.chart_dashboard_pie);

            chart.setTouchEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawEntryLabels(false);
            chart.setUsePercentValues(true);
            chart.setTransparentCircleRadius(0f);
            chart.getLegend().setEnabled(false);

            List<TextView> legends = new ArrayList<>(6);
            legends.add(holder.getView(R.id.chart_dashboard_legend1));
            legends.add(holder.getView(R.id.chart_dashboard_legend2));
            legends.add(holder.getView(R.id.chart_dashboard_legend3));
            legends.add(holder.getView(R.id.chart_dashboard_legend4));
            legends.add(holder.getView(R.id.chart_dashboard_legend5));
            legends.add(holder.getView(R.id.chart_dashboard_legend6));
            List<TextView> legendsData = new ArrayList<>(6);
            legendsData.add(holder.getView(R.id.chart_dashboard_legend1_data));
            legendsData.add(holder.getView(R.id.chart_dashboard_legend2_data));
            legendsData.add(holder.getView(R.id.chart_dashboard_legend3_data));
            legendsData.add(holder.getView(R.id.chart_dashboard_legend4_data));
            legendsData.add(holder.getView(R.id.chart_dashboard_legend5_data));
            legendsData.add(holder.getView(R.id.chart_dashboard_legend6_data));
            holder.putTag(HOLDER_TAG_LEGENDS, legends);
            holder.putTag(HOLDER_TAG_LEGENDS_DATA, legendsData);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            mHolder = holder;
            TextView title = holder.getView(R.id.tv_dashboard_title);
            TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
            TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);
            PieChart chart = holder.getView(R.id.chart_dashboard_pie);
            title.setText(model.title);
            bySales.setSelected(model.dataSource == DashboardContract.DATA_MODE_SALES);
            byOrder.setSelected(model.dataSource == DashboardContract.DATA_MODE_ORDER);

            if (mState == com.sunmi.assistant.dashboard.model.BaseRefreshCard.FLAG_INIT) {
                Log.d(TAG, "Card data setup view skip.");
                chart.setVisibility(View.INVISIBLE);
                return;
            }

            chart.setVisibility(View.VISIBLE);
            List<PieEntry> newDataSet = model.dataSets.get(model.dataSource);
            if (newDataSet == null || newDataSet.isEmpty()) {
                chart.setData(null);
                chart.invalidate();
                holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
                return;
            }

            PieDataSet dataSet;
            PieEntry last = newDataSet.get(newDataSet.size() - 1);
            if (TextUtils.isEmpty(last.getLabel())) {
                last.setLabel(holder.getContext().getResources().getString(R.string.dashboard_purchase_others));
            }
            legendSetUp(holder, newDataSet);
            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                dataSet = (PieDataSet) chart.getData().getDataSetByIndex(0);
//            dataSet.setValues(dataList);
//            chart.getData().notifyDataChanged();
//            chart.notifyDataSetChanged();
//            chart.invalidate();
                PieChartDataUpdateAnim anim = new PieChartDataUpdateAnim(300, chart,
                        dataSet.getValues(), newDataSet);
                anim.run();
            } else {
                dataSet = new PieDataSet(newDataSet, "data");
                dataSet.setColors(Arrays.asList(PIE_COLORS));
                dataSet.setDrawValues(false);
                dataSet.setDrawIcons(false);
                PieData data = new PieData(dataSet);
                chart.animateY(300, Easing.EaseOutCubic);
                chart.setData(data);
                chart.invalidate();
            }
            holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
        }

        private void legendSetUp(@NonNull BaseViewHolder<Model> holder, List<PieEntry> dataList) {
            int size = dataList.size();
            List<TextView> legends = holder.getTag(HOLDER_TAG_LEGENDS);
            List<TextView> legendsData = holder.getTag(HOLDER_TAG_LEGENDS_DATA);
            for (int i = 0; i < 6; i++) {
                TextView legend = legends.get(i);
                TextView legendData = legendsData.get(i);
                if (i < size) {
                    Drawable drawable = holder.getContext().getResources()
                            .getDrawable(R.drawable.dashboard_pie_chart_legend_form);
                    drawable = DrawableCompat.wrap(drawable);
                    PieEntry entry = dataList.get(i);
                    DrawableCompat.setTint(drawable, PIE_COLORS[i]);
                    if (i < 3) {
                        legend.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    } else {
                        legend.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    }
                    legend.setText(entry.getLabel());
                    legendData.setText(String.format(Locale.getDefault(), "%.0f%%", entry.getValue() * 100));
                    legend.setVisibility(View.VISIBLE);
                    legendData.setVisibility(View.VISIBLE);
                } else {
                    legend.setVisibility(View.INVISIBLE);
                    legendData.setVisibility(View.INVISIBLE);
                }
            }
        }

    }

    static class Model {
        private String title;
        private int dataSource;
        private SparseArray<List<PieEntry>> dataSets = new SparseArray<>(2);

        public Model(String title, int dataSource) {
            this.title = title;
            this.dataSource = dataSource;
        }
    }

}
