package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.util.Constants;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderTotalAmountResp;
import com.sunmi.assistant.data.response.OrderTotalCountResp;
import com.sunmi.assistant.order.OrderListActivity_;
import com.sunmi.assistant.order.model.OrderInfo;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.model.Interval;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeOverviewCard extends BaseRefreshCard<RealtimeOverviewCard.Model, Object> {

    private static RealtimeOverviewCard sInstance;

    private RealtimeOverviewCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static RealtimeOverviewCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new RealtimeOverviewCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_realtime_overview;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        if (hasAuth()) {
            loadSales(companyId, shopId, period, callback);
        } else if (hasFs()) {
            loadCustomer(companyId, shopId, period, callback);
        }
        return null;
    }

    private void loadSales(int companyId, int shopId, int period, CardCallback callback) {
        Interval time = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        PaymentApi.get().getOrderTotalAmount(companyId, shopId,
                time.start / 1000, time.end / 1000, 1,
                new RetrofitCallback<OrderTotalAmountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderTotalAmountResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        Model model = getModel();
                        if (period == Constants.TIME_PERIOD_TODAY) {
                            model.sales = data.getDayAmount();
                            model.lastSales = data.getYesterdayAmount();
                        } else if (period == Constants.TIME_PERIOD_WEEK) {
                            model.sales = data.getWeekAmount();
                            model.lastSales = data.getLastWeekAmount();
                        } else {
                            model.sales = data.getMonthAmount();
                            model.lastSales = data.getLastMonthAmount();
                        }
                        loadVolume(companyId, shopId, period, callback);
                    }

                    @Override
                    public void onFail(int code, String msg, OrderTotalAmountResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    private void loadVolume(int companyId, int shopId, int period, CardCallback callback) {
        Interval time = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        PaymentApi.get().getOrderTotalCount(companyId, shopId,
                time.start / 1000, time.end / 1000, 1,
                new RetrofitCallback<OrderTotalCountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderTotalCountResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        Model model = getModel();
                        if (period == Constants.TIME_PERIOD_TODAY) {
                            model.volume = data.getDayCount();
                            model.lastVolume = data.getYesterdayCount();
                        } else if (period == Constants.TIME_PERIOD_WEEK) {
                            model.volume = data.getWeekCount();
                            model.lastVolume = data.getLastWeekCount();
                        } else {
                            model.volume = data.getMonthCount();
                            model.lastVolume = data.getLastMonthCount();
                        }
                        if (hasFs()) {
                            loadCustomer(companyId, shopId, period, callback);
                        } else {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, OrderTotalCountResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    private void loadCustomer(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomer(companyId, shopId, period,
                new RetrofitCallback<CustomerCountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerCountResp data) {
                        if (data == null) {
                            onFail(code, msg, null);
                            return;
                        }
                        Model model = getModel();
                        model.customer = data.getLatestCount() + data.getLatestEntryHeadCount();
                        model.lastCustomer = data.getEarlyCount() + data.getEarlyEntryHeadCount();
                        if (hasAuth()) {
                            model.rate = model.customer == 0 ?
                                    0f : Math.min((float) model.volume / model.customer, 1f);
                            model.lastRate = model.lastCustomer == 0 ?
                                    0f : Math.min((float) model.lastVolume / model.lastCustomer, 1f);
                        }
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerCountResp data) {
                        callback.onFail(code, msg, data);
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

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = holder.getContext();
        holder.addOnClickListener(R.id.layout_dashboard_main, (h, model, position) -> {
            if (!hasAuth() && hasFs()) {
                goToCustomerList(context);
            } else {
                goToOrderList(context);
            }
        });
        holder.addOnClickListener(R.id.layout_dashboard_volume, (h, model, position)
                -> goToOrderList(context));
        holder.addOnClickListener(R.id.layout_dashboard_customer, (h, model, position)
                -> goToCustomerList(context));
        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder, model.period);
        Context context = holder.getContext();

        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subdata = holder.getView(R.id.tv_dashboard_subdata);
        TextView volumeValue = holder.getView(R.id.tv_dashboard_volume);
        TextView volumeSubdata = holder.getView(R.id.tv_dashboard_volume_subdata);
        TextView customerValue = holder.getView(R.id.tv_dashboard_customer);
        TextView customerSubdata = holder.getView(R.id.tv_dashboard_customer_subdata);
        TextView rateValue = holder.getView(R.id.tv_dashboard_rate);
        TextView rateSubdata = holder.getView(R.id.tv_dashboard_rate_subdata);
        if (hasAuth() && !hasFs()) {
            value.setText(model.getSales(context));
            subdata.setText(model.getLastSales(context));
            volumeValue.setText(model.getVolume(context));
            volumeSubdata.setText(model.getLastVolume(context));
        } else if (!hasAuth() && hasFs()) {
            value.setText(model.getCustomer(context));
            subdata.setText(model.getLastCustomer(context));
        } else {
            value.setText(model.getSales(context));
            subdata.setText(model.getLastSales(context));
            volumeValue.setText(model.getVolume(context));
            volumeSubdata.setText(model.getLastVolume(context));
            customerValue.setText(model.getCustomer(context));
            customerSubdata.setText(model.getLastCustomer(context));
            rateValue.setText(model.getRate());
            rateSubdata.setText(model.getLastRate());
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        View main = holder.getView(R.id.layout_dashboard_main);
        View volume = holder.getView(R.id.layout_dashboard_volume);
        View customer = holder.getView(R.id.layout_dashboard_customer);
        View rate = holder.getView(R.id.layout_dashboard_rate);
        ImageView loading = holder.getView(R.id.iv_dashboard_loading);
        if (!hasAuth() && hasFs()) {
            main.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.GONE);
            customer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_single);
        } else {
            main.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.INVISIBLE);
            customer.setVisibility(View.INVISIBLE);
            rate.setVisibility(View.INVISIBLE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_multi);
        }
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder, mPeriod);
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subdata = holder.getView(R.id.tv_dashboard_subdata);
        TextView volumeValue = holder.getView(R.id.tv_dashboard_volume);
        TextView volumeSubdata = holder.getView(R.id.tv_dashboard_volume_subdata);
        TextView customerValue = holder.getView(R.id.tv_dashboard_customer);
        TextView customerSubdata = holder.getView(R.id.tv_dashboard_customer_subdata);
        TextView rateValue = holder.getView(R.id.tv_dashboard_rate);
        TextView rateSubdata = holder.getView(R.id.tv_dashboard_rate_subdata);
        value.setText(Utils.DATA_NONE);
        subdata.setText(Utils.DATA_NONE);
        volumeValue.setText(Utils.DATA_NONE);
        volumeSubdata.setText(Utils.DATA_NONE);
        customerValue.setText(Utils.DATA_NONE);
        customerSubdata.setText(Utils.DATA_NONE);
        rateValue.setText(Utils.DATA_NONE);
        rateSubdata.setText(Utils.DATA_NONE);
    }

    private void setupVisible(@NonNull BaseViewHolder<Model> holder, int period) {
        View main = holder.getView(R.id.layout_dashboard_main);
        View volume = holder.getView(R.id.layout_dashboard_volume);
        View customer = holder.getView(R.id.layout_dashboard_customer);
        View rate = holder.getView(R.id.layout_dashboard_rate);
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView subtitle = holder.getView(R.id.tv_dashboard_subtitle);
        TextView volumeSubtitle = holder.getView(R.id.tv_dashboard_volume_subtitle);
        TextView customerSubtitle = holder.getView(R.id.tv_dashboard_customer_subtitle);
        TextView rateSubtitle = holder.getView(R.id.tv_dashboard_rate_subtitle);
        holder.getView(R.id.iv_dashboard_loading).setVisibility(View.GONE);
        // 根据数据来源变更View展示的部分和文案
        if (hasAuth() && !hasFs()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            customer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_var_total_sales_amount);
        } else if (!hasAuth() && hasFs()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.GONE);
            customer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_var_customer);
        } else {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            customer.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
            title.setText(R.string.dashboard_var_total_sales_amount);
        }
        // 根据当前选择的时间筛选展示不同的文案
        if (period == Constants.TIME_PERIOD_TODAY) {
            subtitle.setText(R.string.dashboard_time_yesterday);
            volumeSubtitle.setText(R.string.dashboard_time_yesterday);
            customerSubtitle.setText(R.string.dashboard_time_yesterday);
            rateSubtitle.setText(R.string.dashboard_time_yesterday);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            subtitle.setText(R.string.dashboard_time_last_week);
            volumeSubtitle.setText(R.string.dashboard_time_last_week);
            customerSubtitle.setText(R.string.dashboard_time_last_week);
            rateSubtitle.setText(R.string.dashboard_time_last_week);
        } else {
            subtitle.setText(R.string.dashboard_time_last_month);
            volumeSubtitle.setText(R.string.dashboard_time_last_month);
            customerSubtitle.setText(R.string.dashboard_time_last_month);
            rateSubtitle.setText(R.string.dashboard_time_last_month);
        }
    }

    private void goToOrderList(Context context) {
        Interval periodTimestamp = Utils.getPeriodTimestamp(mPeriod);
        OrderListActivity_.intent(context)
                .mTimeStart(periodTimestamp.start)
                .mTimeEnd(periodTimestamp.end)
                .mInitOrderType(OrderInfo.ORDER_TYPE_ALL)
                .start();
    }

    private void goToCustomerList(Context context) {
        // TODO: Customer List
    }

    public static class Model extends BaseRefreshCard.BaseModel {

        double sales;
        double lastSales;
        int volume;
        int lastVolume;
        int customer;
        int lastCustomer;
        float rate;
        float lastRate;

        private CharSequence getSales(Context context) {
            return Utils.formatNumber(context, sales, true, true);
        }

        private CharSequence getLastSales(Context context) {
            return Utils.formatNumber(context, lastSales, true, false);
        }

        private CharSequence getVolume(Context context) {
            return Utils.formatNumber(context, volume, false, true);
        }

        private CharSequence getLastVolume(Context context) {
            return Utils.formatNumber(context, lastVolume, false, false);
        }

        private CharSequence getCustomer(Context context) {
            return Utils.formatNumber(context, customer, false, true);
        }

        private CharSequence getLastCustomer(Context context) {
            return Utils.formatNumber(context, lastCustomer, false, false);
        }

        private CharSequence getRate() {
            return Utils.formatPercent(rate, true, true);
        }

        private CharSequence getLastRate() {
            return Utils.formatPercent(lastRate, true, false);
        }

    }
}
