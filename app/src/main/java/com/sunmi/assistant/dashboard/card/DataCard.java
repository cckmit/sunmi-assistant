package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderTotalAmountResp;
import com.sunmi.assistant.data.response.OrderTotalCountResp;
import com.sunmi.assistant.order.OrderListActivity_;
import com.sunmi.assistant.order.model.OrderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class DataCard extends BaseRefreshCard<DataCard.Model, Object> {

    private static DataCard sInstance;

    private static final int NUM_100_MILLION = 100000000;
    private static final int NUM_10_THOUSANDS = 10000;

    private DataCard(DashboardContract.Presenter presenter, int source) {
        super(presenter, source);
    }

    public static DataCard init(DashboardContract.Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new DataCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_data;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        if (hasSaas()) {
            loadSales(companyId, shopId, period, callback);
        } else if (hasFs()) {
            loadCustomer(companyId, shopId, period, callback);
        }
        return null;
    }

    private void loadSales(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> time = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        PaymentApi.get().getOrderTotalAmount(companyId, shopId, time.first, time.second, 1,
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
        Pair<Long, Long> time = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        PaymentApi.get().getOrderTotalCount(companyId, shopId, time.first, time.second, 1,
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
                        model.customer = data.getLatestCount();
                        model.lastCustomer = data.getEarlyCount();
                        if (hasSaas()) {
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
    protected List<Model> createModel() {
        ArrayList<Model> models = new ArrayList<>();
        models.add(new Model());
        return models;
    }

    @Override
    protected void setupModel(List<Model> models, Object response) {
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = holder.getContext();
        for (Model model : getModels()) {
            model.init(context);
        }
        holder.addOnClickListener(R.id.layout_dashboard_main, (h, model, position) -> {
            if (!hasSaas() && hasFs()) {
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
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subdata = holder.getView(R.id.tv_dashboard_subdata);
        TextView volumeValue = holder.getView(R.id.tv_dashboard_volume);
        TextView volumeSubdata = holder.getView(R.id.tv_dashboard_volume_subdata);
        TextView customerValue = holder.getView(R.id.tv_dashboard_customer);
        TextView customerSubdata = holder.getView(R.id.tv_dashboard_customer_subdata);
        TextView rateValue = holder.getView(R.id.tv_dashboard_rate);
        TextView rateSubdata = holder.getView(R.id.tv_dashboard_rate_subdata);
        if (hasSaas() && !hasFs()) {
            value.setText(model.getSales());
            subdata.setText(model.getLastSales());
            volumeValue.setText(model.getVolume());
            volumeSubdata.setText(model.getLastVolume());
        } else if (!hasSaas() && hasFs()) {
            value.setText(model.getCustomer());
            subdata.setText(model.getLastCustomer());
        } else {
            value.setText(model.getSales());
            subdata.setText(model.getLastSales());
            volumeValue.setText(model.getVolume());
            volumeSubdata.setText(model.getLastVolume());
            customerValue.setText(model.getCustomer());
            customerSubdata.setText(model.getLastCustomer());
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
        if (!hasSaas() && hasFs()) {
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
        value.setText(DATA_NONE);
        subdata.setText(DATA_NONE);
        volumeValue.setText(DATA_NONE);
        volumeSubdata.setText(DATA_NONE);
        customerValue.setText(DATA_NONE);
        customerSubdata.setText(DATA_NONE);
        rateValue.setText(DATA_NONE);
        rateSubdata.setText(DATA_NONE);
    }

    private void setupVisible(@NonNull BaseViewHolder<Model> holder, int period) {
        View main = holder.getView(R.id.layout_dashboard_main);
        View volume = holder.getView(R.id.layout_dashboard_volume);
        View customer = holder.getView(R.id.layout_dashboard_customer);
        View rate = holder.getView(R.id.layout_dashboard_rate);
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView subtitle = holder.getView(R.id.tv_dashboard_subtitle);
        TextView volumeTitle = holder.getView(R.id.tv_dashboard_volume_title);
        TextView volumeSubtitle = holder.getView(R.id.tv_dashboard_volume_subtitle);
        TextView customerTitle = holder.getView(R.id.tv_dashboard_customer_title);
        TextView customerSubtitle = holder.getView(R.id.tv_dashboard_customer_subtitle);
        TextView rateTitle = holder.getView(R.id.tv_dashboard_rate_title);
        TextView rateSubtitle = holder.getView(R.id.tv_dashboard_rate_subtitle);
        holder.getView(R.id.iv_dashboard_loading).setVisibility(View.GONE);
        // 根据数据来源变更View展示的部分和文案
        if (hasSaas() && !hasFs()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            customer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_data_sales_amount);
        } else if (!hasSaas() && hasFs()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.GONE);
            customer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_data_customer);
        } else {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            customer.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
            title.setText(R.string.dashboard_data_sales_amount);
        }
        // 根据当前选择的时间筛选展示不同的文案
        if (period == Constants.TIME_PERIOD_TODAY) {
            subtitle.setText(R.string.dashboard_yesterday);
            volumeSubtitle.setText(R.string.dashboard_yesterday);
            customerSubtitle.setText(R.string.dashboard_yesterday);
            rateSubtitle.setText(R.string.dashboard_yesterday);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            subtitle.setText(R.string.dashboard_last_week);
            volumeSubtitle.setText(R.string.dashboard_last_week);
            customerSubtitle.setText(R.string.dashboard_last_week);
            rateSubtitle.setText(R.string.dashboard_last_week);
        } else {
            subtitle.setText(R.string.dashboard_last_month);
            volumeSubtitle.setText(R.string.dashboard_last_month);
            customerSubtitle.setText(R.string.dashboard_last_month);
            rateSubtitle.setText(R.string.dashboard_last_month);
        }
    }

    private void goToOrderList(Context context) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(mPeriod);
        OrderListActivity_.intent(context)
                .mTimeStart(periodTimestamp.first)
                .mTimeEnd(periodTimestamp.second)
                .mInitOrderType(OrderInfo.ORDER_TYPE_ALL)
                .start();
    }

    private void goToCustomerList(Context context) {
        // TODO: Customer List
    }

    public static class Model extends BaseRefreshCard.BaseModel {

        private String mNum100Million;
        private String mNum10Thousands;

        float sales;
        float lastSales;
        int volume;
        int lastVolume;
        int customer;
        int lastCustomer;
        float rate;
        float lastRate;

        public void init(Context context) {
            mNum10Thousands = context.getString(R.string.str_num_10_thousands);
            mNum100Million = context.getString(R.string.str_num_100_million);
        }

        public String getSales() {
            if (sales > NUM_100_MILLION) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(sales / NUM_100_MILLION) + mNum100Million;
            } else {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(sales);
            }
        }

        public String getLastSales() {
            if (lastSales > NUM_100_MILLION) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(lastSales / NUM_100_MILLION) + mNum100Million;
            } else {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(lastSales);
            }
        }

        public String getVolume() {
            if (volume > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(
                        (float) volume / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(volume);
            }
        }

        public String getLastVolume() {
            if (lastVolume > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(
                        (float) lastVolume / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(lastVolume);
            }
        }

        public String getCustomer() {
            if (customer > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(
                        (float) customer / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(customer);
            }
        }

        public String getLastCustomer() {
            if (lastCustomer > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(
                        (float) lastCustomer / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(lastCustomer);
            }
        }

        public String getRate() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_PERCENT, rate * 100);
        }

        public String getLastRate() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_PERCENT, lastRate * 100);
        }

    }
}
