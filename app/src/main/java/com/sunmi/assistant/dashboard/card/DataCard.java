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

    private static final int NUM_100_MILLION = 100000000;
    private static final int NUM_10_THOUSANDS = 10000;

    public DataCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter);
        addOnViewClickListener(R.id.layout_dashboard_main, (adapter, holder, v, model, position) -> {
            if (!showTransactionData() && showConsumerData()) {
                goToConsumerList(context);
            } else {
                goToOrderList(context);
            }
        });
        addOnViewClickListener(R.id.layout_dashboard_volume, (adapter, holder, v, model, position)
                -> goToOrderList(context));
        addOnViewClickListener(R.id.layout_dashboard_consumer, (adapter, holder, v, model, position)
                -> goToConsumerList(context));
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_data;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
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
                                    0f : Math.min((float) model.volume / model.consumer, 1f);
                            model.lastRate = model.lastConsumer == 0 ?
                                    0f : Math.min((float) model.lastVolume / model.lastConsumer, 1f);
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
        View main = holder.getView(R.id.layout_dashboard_main);
        View volume = holder.getView(R.id.layout_dashboard_volume);
        View consumer = holder.getView(R.id.layout_dashboard_consumer);
        View rate = holder.getView(R.id.layout_dashboard_rate);
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
        setupVisible(holder, getPeriod());
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
        View main = holder.getView(R.id.layout_dashboard_main);
        View volume = holder.getView(R.id.layout_dashboard_volume);
        View consumer = holder.getView(R.id.layout_dashboard_consumer);
        View rate = holder.getView(R.id.layout_dashboard_rate);
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

    private void goToOrderList(Context context) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(getPeriod());
        OrderListActivity_.intent(context)
                .mTimeStart(periodTimestamp.first)
                .mTimeEnd(periodTimestamp.second)
                .mInitOrderType(OrderInfo.ORDER_TYPE_ALL)
                .start();
    }

    private void goToConsumerList(Context context) {
        // TODO: Consumer List
    }

    public static class Model extends BaseRefreshItem.BaseModel {

        private String mNum100Million;
        private String mNum10Thousands;

        float sales;
        float lastSales;
        int volume;
        int lastVolume;
        int consumer;
        int lastConsumer;
        float rate;
        float lastRate;

        public Model(Context context) {
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
                return FORMAT_THOUSANDS.format(volume / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(volume);
            }
        }

        public String getLastVolume() {
            if (lastVolume > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS.format(lastVolume / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(lastVolume);
            }
        }

        public String getConsumer() {
            if (consumer > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS.format(consumer / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(consumer);
            }
        }

        public String getLastConsumer() {
            if (lastConsumer > NUM_10_THOUSANDS) {
                return FORMAT_THOUSANDS.format(lastConsumer / NUM_10_THOUSANDS) + mNum10Thousands;
            } else {
                return FORMAT_THOUSANDS.format(lastConsumer);
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
