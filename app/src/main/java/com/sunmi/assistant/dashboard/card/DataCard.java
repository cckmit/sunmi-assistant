package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
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

import java.util.Locale;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.ConsumerCountResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class DataCard extends BaseRefreshItem<DataCard.Model, Object> {


    public DataCard(Context context, DashboardContract.Presenter presenter, int source) {
        super(context, presenter, source);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_data;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        if (showTransactionData()) {
            loadSales(companyId, shopId, period, callback);
        } else if (showConsumerData()) {
            loadConsumer(companyId, shopId, period, callback);
        }
        return null;
    }

    private void loadSales(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> time = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        PaymentApi.get().getOrderTotalAmount(companyId, shopId, time.first, time.second, 1,
                new RetrofitCallback<OrderTotalAmountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, OrderTotalAmountResp data) {
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
                        if (showConsumerData()) {
                            loadConsumer(companyId, shopId, period, callback);
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

    private void loadConsumer(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getConsumer(companyId, shopId, period,
                new RetrofitCallback<ConsumerCountResp>() {
                    @Override
                    public void onSuccess(int code, String msg, ConsumerCountResp data) {
                        Model model = getModel();
                        model.consumer = data.getLatestCount();
                        model.lastConsumer = data.getEarlyCount();
                        if (showTransactionData()) {
                            model.rate = model.consumer == 0 ?
                                    0f : (float) model.volume / model.consumer;
                            model.lastRate = model.lastConsumer == 0 ?
                                    0f : (float) model.lastVolume / model.lastConsumer;
                        }
                        callback.onSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg, ConsumerCountResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder, model.period);
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subdata = holder.getView(R.id.tv_dashboard_subdata);
        TextView volumeValue = holder.getView(R.id.tv_dashboard_volume);
        TextView volumeSubdata = holder.getView(R.id.tv_dashboard_volume_subdata);
        TextView consumerValue = holder.getView(R.id.tv_dashboard_consumer);
        TextView consumerSubdata = holder.getView(R.id.tv_dashboard_consumer_subdata);
        TextView rateValue = holder.getView(R.id.tv_dashboard_rate);
        TextView rateSubdata = holder.getView(R.id.tv_dashboard_rate_subdata);
        if (showTransactionData() && !showConsumerData()) {
            value.setText(model.getSales());
            subdata.setText(model.getLastSales());
            volumeValue.setText(model.getVolume());
            volumeSubdata.setText(model.getLastVolume());
        } else if (!showTransactionData() && showConsumerData()) {
            value.setText(model.getConsumer());
            subdata.setText(model.getLastConsumer());
        } else {
            value.setText(model.getSales());
            subdata.setText(model.getLastSales());
            volumeValue.setText(model.getVolume());
            volumeSubdata.setText(model.getLastVolume());
            consumerValue.setText(model.getConsumer());
            consumerSubdata.setText(model.getLastConsumer());
            rateValue.setText(model.getRate());
            rateSubdata.setText(model.getLastRate());
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        Group main = holder.getView(R.id.group_dashboard_main);
        Group volume = holder.getView(R.id.group_dashboard_volume);
        Group consumer = holder.getView(R.id.group_dashboard_consumer);
        Group rate = holder.getView(R.id.group_dashboard_rate);
        ImageView loading = holder.getView(R.id.iv_dashboard_loading);
        if (!showTransactionData() && showConsumerData()) {
            main.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.GONE);
            consumer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_single);
        } else {
            main.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.INVISIBLE);
            consumer.setVisibility(View.INVISIBLE);
            rate.setVisibility(View.INVISIBLE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_multi);
        }
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder, model.period);
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subdata = holder.getView(R.id.tv_dashboard_subdata);
        TextView volumeValue = holder.getView(R.id.tv_dashboard_volume);
        TextView volumeSubdata = holder.getView(R.id.tv_dashboard_volume_subdata);
        TextView consumerValue = holder.getView(R.id.tv_dashboard_consumer);
        TextView consumerSubdata = holder.getView(R.id.tv_dashboard_consumer_subdata);
        TextView rateValue = holder.getView(R.id.tv_dashboard_rate);
        TextView rateSubdata = holder.getView(R.id.tv_dashboard_rate_subdata);
        value.setText(DATA_NONE);
        subdata.setText(DATA_NONE);
        volumeValue.setText(DATA_NONE);
        volumeSubdata.setText(DATA_NONE);
        consumerValue.setText(DATA_NONE);
        consumerSubdata.setText(DATA_NONE);
        rateValue.setText(DATA_NONE);
        rateSubdata.setText(DATA_NONE);
    }

    private void setupVisible(@NonNull BaseViewHolder<Model> holder, int period) {
        Group main = holder.getView(R.id.group_dashboard_main);
        Group volume = holder.getView(R.id.group_dashboard_volume);
        Group consumer = holder.getView(R.id.group_dashboard_consumer);
        Group rate = holder.getView(R.id.group_dashboard_rate);
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView subtitle = holder.getView(R.id.tv_dashboard_subtitle);
        TextView volumeTitle = holder.getView(R.id.tv_dashboard_volume_title);
        TextView volumeSubtitle = holder.getView(R.id.tv_dashboard_volume_subtitle);
        TextView consumerTitle = holder.getView(R.id.tv_dashboard_consumer_title);
        TextView consumerSubtitle = holder.getView(R.id.tv_dashboard_consumer_subtitle);
        TextView rateTitle = holder.getView(R.id.tv_dashboard_rate_title);
        TextView rateSubtitle = holder.getView(R.id.tv_dashboard_rate_subtitle);
        holder.getView(R.id.iv_dashboard_loading).setVisibility(View.GONE);
        // 根据数据来源变更View展示的部分和文案
        if (showTransactionData() && !showConsumerData()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            consumer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_data_sales_amount);
        } else if (!showTransactionData() && showConsumerData()) {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.GONE);
            consumer.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            title.setText(R.string.dashboard_data_consumer);
        } else {
            main.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
            consumer.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
            title.setText(R.string.dashboard_data_sales_amount);
        }
        // 根据当前选择的时间筛选展示不同的文案
        if (period == Constants.TIME_PERIOD_TODAY) {
            subtitle.setText(R.string.dashboard_yesterday);
            volumeSubtitle.setText(R.string.dashboard_yesterday);
            consumerSubtitle.setText(R.string.dashboard_yesterday);
            rateSubtitle.setText(R.string.dashboard_yesterday);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            subtitle.setText(R.string.dashboard_last_week);
            volumeSubtitle.setText(R.string.dashboard_last_week);
            consumerSubtitle.setText(R.string.dashboard_last_week);
            rateSubtitle.setText(R.string.dashboard_last_week);
        } else {
            subtitle.setText(R.string.dashboard_last_month);
            volumeSubtitle.setText(R.string.dashboard_last_month);
            consumerSubtitle.setText(R.string.dashboard_last_month);
            rateSubtitle.setText(R.string.dashboard_last_month);
        }
    }

    public static class Model extends BaseRefreshItem.BaseModel {
        float sales;
        float lastSales;
        int volume;
        int lastVolume;
        int consumer;
        int lastConsumer;
        float rate;
        float lastRate;

        public String getSales() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_DECIMAL, sales);
        }

        public String getLastSales() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_DECIMAL, lastSales);
        }

        public String getVolume() {
            return String.valueOf(volume);
        }

        public String getLastVolume() {
            return String.valueOf(lastVolume);
        }

        public String getConsumer() {
            return String.valueOf(consumer);
        }

        public String getLastConsumer() {
            return String.valueOf(lastConsumer);
        }

        public String getRate() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_PERCENT, rate * 100);
        }

        public String getLastRate() {
            return String.format(Locale.getDefault(), FORMAT_FLOAT_DOUBLE_PERCENT, lastRate * 100);
        }
    }
}
