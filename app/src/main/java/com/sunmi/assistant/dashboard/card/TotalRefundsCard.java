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
import com.sunmi.assistant.data.response.OrderTotalRefundsResp;
import com.sunmi.assistant.utils.Utils;

import java.text.DecimalFormat;
import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

public class TotalRefundsCard extends BaseRefreshCard<TotalRefundsCard.Model> {

    private static final String TAG = "CustomerPriceCard";

    public TotalRefundsCard(Context context, int companyId, int shopId, int period) {
        super(context, companyId, shopId, period);
    }

    @Override
    protected Model createData() {
        return new Model(getContext().getString(R.string.dashboard_total_refunds));
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new CustomerPriceType();
    }

    @Override
    protected void onPeriodChange(Model model, int period) {
        model.trendName = Utils.getTrendNameByPeriod(getContext(), period);
    }

    @Override
    protected void load(int companyId, int shopId, int period, Pair<Long, Long> periodTimestamp,
                        Model model) {
        Log.d(TAG, "HTTP request total refunds volume.");
        SunmiStoreRemote.get().getOrderRefundCount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1,
                new CardCallback<OrderTotalRefundsResp>() {
                    @Override
                    public void success(OrderTotalRefundsResp data) {
                        Log.d(TAG, "HTTP request total refunds success.");
                        model.data = data.getRefund_count();
                        if (period == DashboardContract.TIME_PERIOD_MONTH
                                && !TextUtils.isEmpty(data.getMonth_rate())) {
                            model.trendData = Float.valueOf(data.getMonth_rate());
                        } else if (period == DashboardContract.TIME_PERIOD_WEEK
                                && !TextUtils.isEmpty(data.getWeek_rate())) {
                            model.trendData = Float.valueOf(data.getWeek_rate());
                        } else if (!TextUtils.isEmpty(data.getDay_rate())) {
                            model.trendData = Float.valueOf(data.getDay_rate());
                        }
                    }
                });
    }

    private class CustomerPriceType extends ItemType<Model, BaseViewHolder<Model>> {

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
            TextView title = holder.getView(R.id.tv_dashboard_title);
            TextView data = holder.getView(R.id.tv_dashboard_data);
            TextView trendName = holder.getView(R.id.tv_dashboard_trend_name);
            TextView trendData = holder.getView(R.id.tv_dashboard_trend_data);
            title.setText(model.title);
            if (!TextUtils.isEmpty(model.trendName)) {
                trendName.setText(model.trendName);
            }

            if (getState() == STATE_INIT || getState() == STATE_LOADING) {
                Log.d(TAG, "Card data setup view skip.");
                return;
            }

            data.setText(String.format(Locale.getDefault(), "%.0f", model.data));
            trendData.setText(holder.getContext().getResources().getString(R.string.dashboard_data_format,
                    format.format(model.trendData * 100)));

            if (model.trendData >= 0) {
                trendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_up, 0, 0, 0);
                trendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_FF0000));
            } else {
                trendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_down, 0, 0, 0);
                trendData.setTextColor(holder.getContext().getResources().getColor(R.color.color_00B552));
            }
            holder.getView(R.id.pb_dashboard_loading).setVisibility(View.GONE);
            Log.d(TAG, "Card data setup complete.");
        }

    }

    public static class Model {
        public String title;
        public float data;
        public String trendName;
        public float trendData;

        public Model(String title) {
            this.title = title;
        }
    }
}
