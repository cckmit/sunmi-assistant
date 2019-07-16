package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderAvgUnitSaleResp;
import com.sunmi.assistant.utils.Utils;

import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.log.LogCat;

public class CustomerPriceCard extends BaseRefreshCard<CustomerPriceCard.Model> {

    public CustomerPriceCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model(getContext().getString(R.string.dashboard_customer_price));
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new CustomerPriceType();
    }

    @Override
    protected void onCompanyChange(Model model, int companyId, int shopId) {
        model.needLoad = true;
    }

    @Override
    protected void onShopChange(Model model, int shopId) {
        model.needLoad = true;
    }

    @Override
    protected void onPeriodChange(Model model, int period) {
        model.trendName = Utils.getTrendNameByPeriod(getContext(), period);
        updateView();
    }

    @Override
    protected void onRefresh(Model model, int period) {
        model.needLoad = true;
    }

    @Override
    protected void load(int companyId, int shopId, int period, CustomerPriceCard.Model model) {
        if (!model.needLoad) {
            return;
        }
        LogCat.d(TAG, "HTTP request customer unit price.");
        toStateLoading();
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(DashboardContract.TIME_PERIOD_TODAY);
        SunmiStoreRemote.get().getOrderAvgUnitSale(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1,
                new CardCallback<OrderAvgUnitSaleResp>() {
                    @Override
                    public void success(OrderAvgUnitSaleResp data) {
                        LogCat.d(TAG, "HTTP request customer price success.");
                        model.needLoad = false;
                        model.dataToday = data.getDay_unit_sale();
                        model.dataWeek = data.getWeek_unit_sale();
                        model.dataMonth = data.getMonth_unit_sale();
                        model.trendDataToday = TextUtils.isEmpty(data.getDay_rate()) ? DATA_NONE : data.getDay_rate();
                        model.trendDataWeek = TextUtils.isEmpty(data.getWeek_rate()) ? DATA_NONE : data.getWeek_rate();
                        model.trendDataMonth = TextUtils.isEmpty(data.getMonth_rate()) ? DATA_NONE : data.getMonth_rate();
                    }
                });
    }

    private class CustomerPriceType extends ItemType<Model, BaseViewHolder<Model>> {

        @Override
        public int getLayoutId(int type) {
            return R.layout.dashboard_recycle_item_data_card;
        }

        @Override
        public int getSpanSize() {
            return 1;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            LogCat.d(TAG, "Setup card view.");
            if (isStateInit()) {
                LogCat.d(TAG, "Card data setup view skip.");
                return;
            }

            holder.getView(R.id.layout_dashboard_content).setVisibility(View.VISIBLE);
            holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);

            if (TextUtils.isEmpty(model.trendName)) {
                model.trendName = Utils.getTrendNameByPeriod(getContext(), getPeriod());
            }
            String trendData = model.getTrendData(getPeriod());

            TextView tvTitle = holder.getView(R.id.tv_dashboard_title);
            TextView tvData = holder.getView(R.id.tv_dashboard_data);
            TextView tvTrendName = holder.getView(R.id.tv_dashboard_trend_name);
            TextView tvTrendData = holder.getView(R.id.tv_dashboard_trend_data);

            tvTitle.setText(model.title);
            tvData.setText(String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_DECIMAL, model.getData(getPeriod())));
            tvTrendName.setText(model.trendName);

            if (TextUtils.equals(trendData, DATA_NONE)) {
                tvTrendData.setText(trendData);
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_333338));
                return;
            }

            float trendDataFloat = Float.valueOf(trendData);
            String trendDataFormat = FORMAT_MAX_DOUBLE_DECIMAL.format(Math.abs(trendDataFloat * 100));
            tvTrendData.setText(holder.getContext().getResources()
                    .getString(R.string.dashboard_data_format, trendDataFormat));

            if (TextUtils.equals(trendDataFormat, DATA_ZERO)) {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_333338));
            } else if (trendDataFloat > 0) {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_up, 0, 0, 0);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_FF0000));
            } else {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_down, 0, 0, 0);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_00B552));
            }
        }

    }

    public static class Model {
        private String title;
        private float dataToday;
        private float dataWeek;
        private float dataMonth;
        private String trendName;
        private String trendDataToday;
        private String trendDataWeek;
        private String trendDataMonth;
        private boolean needLoad = true;

        private Model(String title) {
            this.title = title;
        }

        private float getData(int period) {
            if (period == DashboardContract.TIME_PERIOD_TODAY) {
                return dataToday;
            } else if (period == DashboardContract.TIME_PERIOD_WEEK) {
                return dataWeek;
            } else {
                return dataMonth;
            }
        }

        private String getTrendData(int period) {
            if (period == DashboardContract.TIME_PERIOD_TODAY) {
                return trendDataToday;
            } else if (period == DashboardContract.TIME_PERIOD_WEEK) {
                return trendDataWeek;
            } else {
                return trendDataMonth;
            }
        }
    }
}
