package com.sunmi.assistant.dashboard;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.sunmi.assistant.dashboard.model.BarChartCard;
import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.model.DataCard;
import com.sunmi.assistant.dashboard.model.ListCard;
import com.sunmi.assistant.dashboard.model.PieChartCard;
import com.sunmi.assistant.data.OrderManagementRemote;
import com.sunmi.assistant.data.response.AvgUnitSaleResponse;
import com.sunmi.assistant.data.response.PurchaseTypeRankResponse;
import com.sunmi.assistant.data.response.QuantityRankResponse;
import com.sunmi.assistant.data.response.TimeDistributionResponse;
import com.sunmi.assistant.data.response.TotalAmountResponse;
import com.sunmi.assistant.data.response.TotalCountResponse;
import com.sunmi.assistant.data.response.TotalRefundCountResponse;
import com.sunmi.assistant.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 为每个Card实现数据更新能力
 *
 * @author jacob
 * @since 2019-06-21
 */
public interface DataRefreshHelper<T> {

    String TAG = "DataRefreshHelper";

    void refresh(T model, DataRefreshCallback callback);

    class TotalSalesAmountRefresh implements DataRefreshHelper<DataCard> {

        private Context context;

        TotalSalesAmountRefresh(Context context) {
            this.context = context;
        }

        @Override
        public void refresh(DataCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request total sales amount.");
            model.state = BaseRefreshCard.STATE_LOADING;
            model.trendName = Utils.getTrendNameByTimeSpan(context, model.timeSpan);
            OrderManagementRemote.get().getTotalAmount(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, 1,
                    new CardCallback<DataCard, TotalAmountResponse>(model, callback) {
                        @Override
                        public void success(TotalAmountResponse data) {
                            Log.d(TAG, "HTTP request total sales amount success.");
                            model.data = data.getTotal_amount();
                            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH
                                    && !TextUtils.isEmpty(data.getMonth_rate())) {
                                model.trendData = Float.valueOf(data.getMonth_rate());
                            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK
                                    && !TextUtils.isEmpty(data.getWeek_rate())) {
                                model.trendData = Float.valueOf(data.getWeek_rate());
                            } else if (!TextUtils.isEmpty(data.getDay_rate())) {
                                model.trendData = Float.valueOf(data.getDay_rate());
                            }
                        }
                    });
        }
    }

    class CustomerPriceRefresh implements DataRefreshHelper<DataCard> {

        private Context context;

        CustomerPriceRefresh(Context context) {
            this.context = context;
        }

        @Override
        public void refresh(DataCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request customer price.");
            model.state = BaseRefreshCard.STATE_LOADING;
            model.trendName = Utils.getTrendNameByTimeSpan(context, model.timeSpan);
            OrderManagementRemote.get().getAvgUnitSale(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, 1,
                    new CardCallback<DataCard, AvgUnitSaleResponse>(model, callback) {
                        @Override
                        public void success(AvgUnitSaleResponse data) {
                            Log.d(TAG, "HTTP request customer price success.");
                            model.data = data.getAus();
                            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH
                                    && !TextUtils.isEmpty(data.getMonth_rate())) {
                                model.trendData = Float.valueOf(data.getMonth_rate());
                            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK
                                    && !TextUtils.isEmpty(data.getWeek_rate())) {
                                model.trendData = Float.valueOf(data.getWeek_rate());
                            } else if (!TextUtils.isEmpty(data.getDay_rate())) {
                                model.trendData = Float.valueOf(data.getDay_rate());
                            }
                        }
                    });
        }
    }

    class TotalSalesVolumeRefresh implements DataRefreshHelper<DataCard> {

        private Context context;

        TotalSalesVolumeRefresh(Context context) {
            this.context = context;
        }

        @Override
        public void refresh(DataCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request total sales volume.");
            model.state = BaseRefreshCard.STATE_LOADING;
            model.trendName = Utils.getTrendNameByTimeSpan(context, model.timeSpan);
            OrderManagementRemote.get().getTotalCount(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, 1,
                    new CardCallback<DataCard, TotalCountResponse>(model, callback) {
                        @Override
                        public void success(TotalCountResponse data) {
                            Log.d(TAG, "HTTP request total sales volume success.");
                            model.data = data.getTotal_count();
                            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH
                                    && !TextUtils.isEmpty(data.getMonth_rate())) {
                                model.trendData = Float.valueOf(data.getMonth_rate());
                            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK
                                    && !TextUtils.isEmpty(data.getWeek_rate())) {
                                model.trendData = Float.valueOf(data.getWeek_rate());
                            } else if (!TextUtils.isEmpty(data.getDay_rate())) {
                                model.trendData = Float.valueOf(data.getDay_rate());
                            }
                        }
                    });
        }
    }

    class TotalRefundsRefresh implements DataRefreshHelper<DataCard> {

        private Context context;

        TotalRefundsRefresh(Context context) {
            this.context = context;
        }

        @Override
        public void refresh(DataCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request total refunds.");
            model.state = BaseRefreshCard.STATE_LOADING;
            model.trendName = Utils.getTrendNameByTimeSpan(context, model.timeSpan);
            OrderManagementRemote.get().getRefundCount(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, 1,
                    new CardCallback<DataCard, TotalRefundCountResponse>(model, callback) {
                        @Override
                        public void success(TotalRefundCountResponse data) {
                            Log.d(TAG, "HTTP request total refunds success.");
                            model.data = data.getRefund_count();
                            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH
                                    && !TextUtils.isEmpty(data.getMonth_rate())) {
                                model.trendData = Float.valueOf(data.getMonth_rate());
                            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK
                                    && !TextUtils.isEmpty(data.getWeek_rate())) {
                                model.trendData = Float.valueOf(data.getWeek_rate());
                            } else if (!TextUtils.isEmpty(data.getDay_rate())) {
                                model.trendData = Float.valueOf(data.getDay_rate());
                            }
                        }
                    });
        }
    }

    class TimeDistributionRefresh implements DataRefreshHelper<BarChartCard> {

        @Override
        public void refresh(BarChartCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request time distribution detail.");
            model.state = BaseRefreshCard.STATE_LOADING;
            int interval;
            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH) {
                interval = 86400;
            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK) {
                interval = 86400;
            } else {
                interval = 3600;
            }
            OrderManagementRemote.get().getTimeDistribution(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, interval,
                    new CardCallback<BarChartCard, TimeDistributionResponse>(model, callback) {
                        @Override
                        public void success(TimeDistributionResponse data) {
                            Log.d(TAG, "HTTP request time distribution detail success.");
                            List<TimeDistributionResponse.TimeSpanItem> list = data.getOrder_list();
                            List<BarEntry> amountList = new ArrayList<>(list.size());
                            List<BarEntry> countList = new ArrayList<>(list.size());
                            for (TimeDistributionResponse.TimeSpanItem item : list) {
                                float x = Utils.encodeBarChartXAxisFloat(model.timeSpan, item.getTime());
                                amountList.add(new BarEntry(x, item.getAmount()));
                                countList.add(new BarEntry(x, item.getCount()));
                            }
                            model.dataSets[0] = new BarChartCard.BarChartDataSet(amountList);
                            model.dataSets[1] = new BarChartCard.BarChartDataSet(countList);
                        }
                    });
        }
    }

    class PurchaseTypeRankRefresh implements DataRefreshHelper<PieChartCard> {

        @Override
        public void refresh(PieChartCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request purchase type rank.");
            model.state = BaseRefreshCard.STATE_LOADING;
            OrderManagementRemote.get().getPurchaseTypeRank(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second,
                    new CardCallback<PieChartCard, PurchaseTypeRankResponse>(model, callback) {
                        @Override
                        public void success(PurchaseTypeRankResponse data) {
                            Log.d(TAG, "HTTP request purchase type rank success.");
                            List<PurchaseTypeRankResponse.PurchaseTypeRankItem> list = data.getPurchase_type_list();
                            List<PieEntry> amountList = new ArrayList<>(list.size());
                            List<PieEntry> countList = new ArrayList<>(list.size());
                            float amountTotal = 0;
                            float countTotal = 0;
                            for (PurchaseTypeRankResponse.PurchaseTypeRankItem item : list) {
                                String label = item.getPurchase_type_name();
                                float amount = item.getAmount();
                                int count = item.getCount();
                                amountTotal += amount;
                                countTotal += count;
                                if (amount > 0) {
                                    amountList.add(new PieEntry(amount, label));
                                }
                                if (count > 0) {
                                    countList.add(new PieEntry(count, label));
                                }
                            }
                            Collections.sort(amountList, (o1, o2) -> {
                                if ("其他".equals(o2.getLabel())) {
                                    return -1;
                                } else if ("其他".equals(o1.getLabel())) {
                                    return 1;
                                } else {
                                    return o2.getValue() - o1.getValue() > 0 ? 1 :
                                            (o2.getValue() - o1.getValue() < 0 ? -1 : 0);
                                }
                            });
                            Collections.sort(countList, (o1, o2) -> {
                                if ("其他".equals(o2.getLabel())) {
                                    return -1;
                                } else if ("其他".equals(o1.getLabel())) {
                                    return 1;
                                } else {
                                    return o2.getValue() - o1.getValue() > 0 ? 1 :
                                            (o2.getValue() - o1.getValue() < 0 ? -1 : 0);
                                }
                            });
                            int amountSize = amountList.size();
                            int countSize = countList.size();
                            float other = 0f;
                            if (amountSize > 6) {
                                for (int i = amountSize - 1; i >= 5; i--) {
                                    other += amountList.get(i).getValue();
                                    amountList.remove(i);
                                }
                                amountList.add(new PieEntry(other, ""));
                            }
                            other = 0f;
                            if (countSize > 6) {
                                for (int i = countSize - 1; i >= 5; i--) {
                                    other += countList.get(i).getValue();
                                    countList.remove(i);
                                }
                                countList.add(new PieEntry(other, ""));
                            }
                            for (PieEntry entry : amountList) {
                                entry.setY(entry.getValue() / amountTotal);
                            }
                            for (PieEntry entry : countList) {
                                entry.setY(entry.getValue() / countTotal);
                            }
                            model.dataSets[0] = new PieChartCard.PieChartDataSet(amountList);
                            model.dataSets[1] = new PieChartCard.PieChartDataSet(countList);
                        }
                    });
        }
    }

    class QuantityRankRefresh implements DataRefreshHelper<ListCard> {

        @Override
        public void refresh(ListCard model, DataRefreshCallback callback) {
            Log.d(TAG, "HTTP request quantity rank.");
            model.state = BaseRefreshCard.STATE_LOADING;
            OrderManagementRemote.get().getQuantityRank(model.companyId, model.shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second,
                    new CardCallback<ListCard, QuantityRankResponse>(model, callback) {
                        @Override
                        public void success(QuantityRankResponse data) {
                            Log.d(TAG, "HTTP request quantity rank success.");
                            List<QuantityRankResponse.QuantityRankItem> list = data.getQuantity_rank();
                            Collections.sort(list, (o1, o2) -> o2.getQuantity() - o1.getQuantity());
                            int size = list.size();
                            model.list = new ArrayList<>(size);
                            for (int i = 0; i < size; i++) {
                                QuantityRankResponse.QuantityRankItem item = list.get(i);
                                model.list.add(new ListCard.Item(i + 1, item.getName(), item.getQuantity()));
                            }
                        }
                    });
        }
    }

    abstract class CardCallback<Model extends BaseRefreshCard<Model>, Response>
            extends RetrofitCallback<Response> {

        private Model model;
        private DataRefreshCallback callback;

        CardCallback(Model model, DataRefreshCallback callback) {
            this.model = model;
            this.callback = callback;
        }

        public abstract void success(Response data);

        @Override
        public void onSuccess(int code, String msg, Response data) {
            success(data);
            model.state = BaseRefreshCard.STATE_SUCCESS;
            model.flag = BaseRefreshCard.FLAG_NORMAL;
            if (model.callback != null) {
                model.callback.onSuccess();
            }
            if (callback != null) {
                callback.onSuccess();
            }
        }

        @Override
        public void onFail(int code, String msg, Response data) {
            Log.e(TAG, "HTTP request Failed. " + msg);
            model.state = BaseRefreshCard.STATE_FAILED;
            if (model.callback != null) {
                model.callback.onFail();
            }
            if (callback != null) {
                callback.onFail();
            }
        }
    }
}
