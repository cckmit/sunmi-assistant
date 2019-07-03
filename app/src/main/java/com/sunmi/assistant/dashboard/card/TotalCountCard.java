package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderTotalCountResp;
import com.sunmi.assistant.utils.Utils;

import java.text.DecimalFormat;
import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

public class TotalCountCard extends BaseRefreshCard<TotalCountCard.Model> {

    private static final String TAG = "TotalCountCard";

    public TotalCountCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model(getContext().getString(R.string.dashboard_total_sales_volume));
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new TotalCountType();
    }

    @Override
    protected void onPeriodChange(Model model, int period) {
        model.trendName = Utils.getTrendNameByPeriod(getContext(), period);
    }

    @Override
    protected void onRefresh(TotalCountCard.Model model, int period) {
        model.needLoad = true;
    }

    @Override
    protected void load(int companyId, int shopId, int period, TotalCountCard.Model model) {
        if (!model.needLoad) {
            return;
        }
        Log.d(TAG, "HTTP request total sales volume.");
        setState(STATE_LOADING);
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(DashboardContract.TIME_PERIOD_TODAY);
        SunmiStoreRemote.get().getOrderTotalCount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1,
                new CardCallback<OrderTotalCountResp>() {
                    @Override
                    public void success(OrderTotalCountResp data) {
                        Log.d(TAG, "HTTP request total sales volume success.");
                        model.needLoad = false;
                        model.dataToday = data.getDay_count();
                        model.dataWeek = data.getWeek_count();
                        model.dataMonth = data.getMonth_count();
                        model.trendDataToday = TextUtils.isEmpty(data.getDay_rate()) ? 0f : Float.valueOf(data.getDay_rate());
                        model.trendDataWeek = TextUtils.isEmpty(data.getWeek_rate()) ? 0f : Float.valueOf(data.getWeek_rate());
                        model.trendDataMonth = TextUtils.isEmpty(data.getMonth_rate()) ? 0f : Float.valueOf(data.getMonth_rate());
                    }
                });
    }

    private class TotalCountType extends ItemType<Model, BaseViewHolder<Model>> {

        private DecimalFormat format = new DecimalFormat("#.##");

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
            Log.d(TAG, "Setup card view.");
            setHolder(holder);
            TextView tvTitle = holder.getView(R.id.tv_dashboard_title);
            TextView tvData = holder.getView(R.id.tv_dashboard_data);
            TextView tvTrendName = holder.getView(R.id.tv_dashboard_trend_name);
            TextView tvTrendData = holder.getView(R.id.tv_dashboard_trend_data);
            tvTitle.setText(model.title);
            if (!TextUtils.isEmpty(model.trendName)) {
                tvTrendName.setText(model.trendName);
            }

            if (getState() == STATE_INIT || getState() == STATE_LOADING) {
                Log.d(TAG, "Card data setup view skip.");
                return;
            }

            tvData.setText(String.format(Locale.getDefault(), "%.0f", model.getData(getPeriod())));
            float trendData = model.getTrendData(getPeriod());
            String trendFormatNumber = this.format.format(Math.abs(trendData * 100));
            tvTrendData.setText(holder.getContext().getResources()
                    .getString(R.string.dashboard_data_format, trendFormatNumber));

            if (TextUtils.equals(trendFormatNumber, "0")) {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_333338));
            } else if (trendData > 0) {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_up, 0, 0, 0);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_FF0000));
            } else {
                tvTrendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_down, 0, 0, 0);
                tvTrendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_00B552));
            }
            holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
            Log.d(TAG, "Card data setup complete.");
        }

    }

    public static class Model {
        private String title;
        private float dataToday;
        private float dataWeek;
        private float dataMonth;
        private String trendName;
        private float trendDataToday;
        private float trendDataWeek;
        private float trendDataMonth;
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

        private float getTrendData(int period) {
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
