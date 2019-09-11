package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
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
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderPayTypeRankResp;
import com.sunmi.assistant.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-07-22
 */
public class PurchaseTypeCard extends BaseRefreshCard<PurchaseTypeCard.Model, OrderPayTypeRankResp> {

    private static final int MAX_ITEM_COUNT = 6;

    private static final String HOLDER_TAG_LEGENDS = "legends";
    private static final String HOLDER_TAG_LEGENDS_DATA = "legends_data";

    private static final Integer[] PIE_COLORS = {
            0xFF2997FF, 0xFF09E896, 0xFFED9600, 0xFFFFA100, 0xFFFC5656, 0xFF8766FF, 0xFFC0C0C0
    };

    private static final HashMap<String, Integer> PURCHASE_TYPE_INDEX = new HashMap<>(6);
    private static String[] PURCHASE_TYPE_NAME;

    public PurchaseTypeCard(Context context, DashboardContract.Presenter presenter, int companyId, int shopId) {
        super(context, presenter, companyId, shopId);
        if (PURCHASE_TYPE_NAME == null) {
            PURCHASE_TYPE_NAME = context.getResources().getStringArray(R.array.dashboard_purchase_type);
        }
        if (PURCHASE_TYPE_INDEX.isEmpty()) {
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-alipay", 1);
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-wechat", 2);
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-cash", 3);
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-card", 4);
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-unionpayqr", 5);
            PURCHASE_TYPE_INDEX.put("payment-purchase-type-other", 6);
        }
        addOnViewClickListener(R.id.tv_dashboard_radio_by_sales, (adapter, holder, v, model, position) -> {
            model.dataSource = Constants.DATA_MODE_SALES;
            updateView();
        });
        addOnViewClickListener(R.id.tv_dashboard_radio_by_order, (adapter, holder, v, model, position) -> {
            model.dataSource = Constants.DATA_MODE_ORDER;
            updateView();
        });
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_purchase_rank),
                Constants.DATA_MODE_SALES);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_old_chart_pie;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view,
                                                    @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<PurchaseTypeCard.Model> holder = new BaseViewHolder<>(view, type);
        PieChart chart = holder.getView(R.id.chart_dashboard_pie);

        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setUsePercentValues(true);
        chart.setTransparentCircleRadius(0f);
        chart.getLegend().setEnabled(false);

        List<TextView> legends = new ArrayList<>(MAX_ITEM_COUNT);
        legends.add(holder.getView(R.id.chart_dashboard_legend1));
        legends.add(holder.getView(R.id.chart_dashboard_legend2));
        legends.add(holder.getView(R.id.chart_dashboard_legend3));
        legends.add(holder.getView(R.id.chart_dashboard_legend4));
        legends.add(holder.getView(R.id.chart_dashboard_legend5));
        legends.add(holder.getView(R.id.chart_dashboard_legend6));
        List<TextView> legendsData = new ArrayList<>(MAX_ITEM_COUNT);
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
    protected Call<BaseResponse<OrderPayTypeRankResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(period);
        return PaymentApi.get().getOrderPurchaseTypeRank(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, callback);
    }

    @Override
    protected void setupModel(Model model, OrderPayTypeRankResp response) {
        List<OrderPayTypeRankResp.PayTypeRankItem> list = response.getPurchase_type_list();
        Collections.sort(list, (o1, o2) ->
                getIndexOfPayMethod(o1.getPurchase_type_tag()) - getIndexOfPayMethod(o2.getPurchase_type_tag()));
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
            if (size > MAX_ITEM_COUNT && i >= 5) {
                otherAmount += item.getAmount();
                otherCount += item.getCount();
                list.remove(i);
            }
        }
        for (OrderPayTypeRankResp.PayTypeRankItem item : list) {
            String label = item.getPurchase_type_name();
            amountList.add(new PieEntry(totalAmount <= 0 ? 0 : item.getAmount() / totalAmount, label));
            countList.add(new PieEntry(totalCount <= 0 ? 0 : (float) (item.getCount()) / totalCount, label));
        }
        if (size > MAX_ITEM_COUNT) {
            amountList.add(new PieEntry(totalAmount <= 0 ? 0 : otherAmount / totalAmount, ""));
            countList.add(new PieEntry(totalCount <= 0 ? 0 : otherCount / totalCount, ""));
        }
        model.dataSets.put(Constants.DATA_MODE_SALES, amountList);
        model.dataSets.put(Constants.DATA_MODE_ORDER, countList);
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView bySales = holder.getView(R.id.tv_dashboard_radio_by_sales);
        TextView byOrder = holder.getView(R.id.tv_dashboard_radio_by_order);
        PieChart chart = holder.getView(R.id.chart_dashboard_pie);
        title.setText(model.title);
        bySales.setSelected(model.dataSource == Constants.DATA_MODE_SALES);
        byOrder.setSelected(model.dataSource == Constants.DATA_MODE_ORDER);

        List<PieEntry> newDataSet = model.dataSets.get(model.dataSource);
        if (newDataSet == null) {
            newDataSet = new ArrayList<>();
            model.dataSets.put(model.dataSource, newDataSet);
        }
        if (newDataSet.isEmpty()) {
            for (String name : PURCHASE_TYPE_NAME) {
                newDataSet.add(new PieEntry(0f, name));
            }
        }

        PieDataSet dataSet;
        PieEntry last = newDataSet.get(newDataSet.size() - 1);
        if (TextUtils.isEmpty(last.getLabel())) {
            last.setLabel(holder.getContext().getString(R.string.dashboard_purchase_type_other));
        }
        legendSetUp(holder, newDataSet);

        // Handle empty data.
        float total = 0;
        for (PieEntry entry : newDataSet) {
            total += entry.getValue();
        }
        if (total <= 0) {
            newDataSet.add(new PieEntry(1));
        }

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (PieDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(newDataSet);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new PieDataSet(newDataSet, "data");
            dataSet.setColors(Arrays.asList(PIE_COLORS));
            dataSet.setDrawValues(false);
            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(0f);
            PieData data = new PieData(dataSet);
            chart.setData(data);
        }
        chart.animateY(300, Easing.EaseOutCubic);
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        holder.getView(R.id.layout_dashboard_content).setVisibility(View.GONE);
        holder.getView(R.id.pb_dashboard_loading).setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupView(holder, model, position);
    }

    private int getIndexOfPayMethod(String tag) {
        Integer index = PURCHASE_TYPE_INDEX.get(tag);
        if (index != null) {
            return index;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private void legendSetUp(@NonNull BaseViewHolder<PurchaseTypeCard.Model> holder, List<PieEntry> dataList) {
        int size = dataList.size();
        List<TextView> legends = holder.getTag(HOLDER_TAG_LEGENDS);
        List<TextView> legendsData = holder.getTag(HOLDER_TAG_LEGENDS_DATA);
        for (int i = 0; i < MAX_ITEM_COUNT; i++) {
            TextView legend = legends.get(i);
            TextView legendData = legendsData.get(i);
            if (i < size) {
                PieEntry entry = dataList.get(i);
                legend.setText(entry.getLabel());
                legendData.setText(String.format(Locale.getDefault(), "%.1f%%", entry.getValue() * 100));
                legend.setVisibility(View.VISIBLE);
                legendData.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(holder.getContext(),
                        R.drawable.dashboard_pie_chart_legend_form);
                if (drawable == null) {
                    return;
                }
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, PIE_COLORS[i]);
                if (i < 3) {
                    legend.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                } else {
                    legend.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                }
            } else {
                legend.setVisibility(View.INVISIBLE);
                legendData.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private String title;
        private int dataSource;
        private SparseArray<List<PieEntry>> dataSets = new SparseArray<>(2);

        private Model(String title, int dataSource) {
            this.title = title;
            this.dataSource = dataSource;
        }
    }

}
