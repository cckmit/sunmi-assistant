package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;
import com.sunmi.assistant.dashboard.util.Utils;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.model.Interval;
import sunmi.common.model.SaleDataResp;
import sunmi.common.model.TotalCustomerDataResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;

public class TotalRealtimeOverviewCard extends BaseRefreshCard<TotalRealtimeOverviewCard.Model, Object> {

    private static TotalRealtimeOverviewCard sInstance;
    private String startTime = DateTimeUtils.getYesterday("yyyy-MM-dd");

    private TotalRealtimeOverviewCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static TotalRealtimeOverviewCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new TotalRealtimeOverviewCard(presenter, condition, period, periodTime);
        } else {
            sInstance.reset(presenter, condition, period, periodTime);
        }
        return sInstance;
    }


    @Override
    public void init(Context context) {

    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_total_realtime_overview;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
        if (mCondition.hasFs) {
            loadCustomerLatest(companyId, callback);
        } else if (mCondition.hasSaas) {
            loadSaleData(companyId, callback);
        }
        return null;
    }

    //今日总客流量
    private void loadCustomerLatest(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeCustomer(companyId, new RetrofitCallback<CustomerCountResp>() {
            @Override
            public void onSuccess(int code, String msg, CustomerCountResp data) {
                Model model = getModel();
                model.customerCount = Math.max(0, data.getLatestCount());
                loadCustomerYesterday(companyId, callback);
            }

            @Override
            public void onFail(int code, String msg, CustomerCountResp data) {
                callback.onFail(code, msg, data);
            }
        });
    }

    private void loadCustomerYesterday(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalCustomer(companyId, startTime, 1,
                new RetrofitCallback<TotalCustomerDataResp>() {
                    @Override
                    public void onSuccess(int code, String msg, TotalCustomerDataResp data) {
                        Model model = getModel();
                        model.customerCountLast = Math.max(0, data.getPassengerCount());
                        if (mCondition.hasSaas) {
                            loadSaleData(companyId, callback);
                        } else {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, TotalCustomerDataResp data) {
                        callback.onFail(code, msg, data);
                    }
                });
    }

    //今日销售额和总交易数
    private void loadSaleData(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalRealtimeSales(companyId, new RetrofitCallback<SaleDataResp>() {
            @Override
            public void onSuccess(int code, String msg, SaleDataResp data) {
                Model model = getModel();
                model.salesAmount = data.getLatestOrderAmount();
                model.salesVolume = Math.max(0, data.getLatestOrderCount());
                loadSaleDataYesterday(companyId, callback);
            }

            @Override
            public void onFail(int code, String msg, SaleDataResp data) {
                callback.onFail(code, msg, data);
            }
        });
    }

    private void loadSaleDataYesterday(int companyId, CardCallback callback) {
        SunmiStoreApi.getInstance().getTotalSales(companyId, startTime, 1, new RetrofitCallback<SaleDataResp>() {
            @Override
            public void onSuccess(int code, String msg, SaleDataResp data) {
                Model model = getModel();
                model.salesAmountLast = data.getOrderAmount();
                model.salesVolumeLast = Math.max(0, data.getOrderCount());
                callback.onSuccess();
            }

            @Override
            public void onFail(int code, String msg, SaleDataResp data) {
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

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder);
        Context context = holder.getContext();
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subValue = holder.getView(R.id.tv_dashboard_subdata);
        TextView amount = holder.getView(R.id.tv_sales_amount);
        TextView amountSubData = holder.getView(R.id.tv_sales_amount_subdata);
        TextView volume = holder.getView(R.id.tv_sales_volume);
        TextView volumeSubData = holder.getView(R.id.tv_sales_volume_subdata);
        if (mCondition.hasSaas && !mCondition.hasFs) {
            amount.setText(model.getSalesAmount(context));
            amountSubData.setText(model.getSalesAmountLast(context));
            volume.setText(model.getSalesVolume(context));
            volumeSubData.setText(model.getSalesVolumeLast(context));
        } else if (!mCondition.hasSaas && mCondition.hasFs) {
            value.setText(model.getCustomerCount(context));
            subValue.setText(model.getCustomerCountLast(context));
        } else {
            value.setText(model.getCustomerCount(context));
            subValue.setText(model.getCustomerCountLast(context));
            amount.setText(model.getSalesAmount(context));
            amountSubData.setText(model.getSalesAmountLast(context));
            volume.setText(model.getSalesVolume(context));
            volumeSubData.setText(model.getSalesVolumeLast(context));
        }
    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        View main = holder.getView(R.id.layout_dashboard_main);
        View amount = holder.getView(R.id.layout_sales_amount);
        View volume = holder.getView(R.id.layout_sales_volume);
        ImageView loading = holder.getView(R.id.iv_dashboard_loading);
        if (mCondition.hasSaas && mCondition.hasFs) {
            main.setVisibility(View.INVISIBLE);
            amount.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.INVISIBLE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_multi);
        } else {
            main.setVisibility(View.INVISIBLE);
            volume.setVisibility(View.GONE);
            amount.setVisibility(View.GONE);
            loading.setImageResource(R.mipmap.dashboard_skeleton_single_three);
        }
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        setupVisible(holder);
        TextView value = holder.getView(R.id.tv_dashboard_value);
        TextView subValue = holder.getView(R.id.tv_dashboard_subdata);
        TextView amount = holder.getView(R.id.tv_sales_amount);
        TextView amountSubData = holder.getView(R.id.tv_sales_amount_subdata);
        TextView volume = holder.getView(R.id.tv_sales_volume);
        TextView volumeSubData = holder.getView(R.id.tv_sales_volume_subdata);
        value.setText(Utils.DATA_NONE);
        subValue.setText(Utils.DATA_NONE);
        amount.setText(Utils.DATA_NONE);
        amountSubData.setText(Utils.DATA_NONE);
        volume.setText(Utils.DATA_NONE);
        volumeSubData.setText(Utils.DATA_NONE);
    }

    private void setupVisible(@NonNull BaseViewHolder<Model> holder) {
        View main = holder.getView(R.id.layout_dashboard_main);
        View amount = holder.getView(R.id.layout_sales_amount);
        View volume = holder.getView(R.id.layout_sales_volume);
        holder.getView(R.id.iv_dashboard_loading).setVisibility(View.GONE);
        if (mCondition.hasSaas && !mCondition.hasFs) {
            main.setVisibility(View.GONE);
            amount.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
        } else if (!mCondition.hasSaas && mCondition.hasFs) {
            main.setVisibility(View.VISIBLE);
            amount.setVisibility(View.GONE);
            volume.setVisibility(View.GONE);
        } else {
            main.setVisibility(View.VISIBLE);
            amount.setVisibility(View.VISIBLE);
            volume.setVisibility(View.VISIBLE);
        }
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        int customerCount;
        int customerCountLast;
        double salesAmount;
        double salesAmountLast;
        int salesVolume;
        int salesVolumeLast;

        private CharSequence getCustomerCount(Context context) {
            return Utils.formatNumber(context, customerCount, false, true);
        }

        private CharSequence getCustomerCountLast(Context context) {
            return Utils.formatNumber(context, customerCountLast, false, false);
        }

        private CharSequence getSalesAmount(Context context) {
            return Utils.formatNumber(context, salesAmount, true, true);
        }

        private CharSequence getSalesAmountLast(Context context) {
            return Utils.formatNumber(context, salesAmountLast, true, false);
        }

        private CharSequence getSalesVolume(Context context) {
            return Utils.formatNumber(context, salesVolume, false, true);
        }

        private CharSequence getSalesVolumeLast(Context context) {
            return Utils.formatNumber(context, salesVolumeLast, false, false);
        }
    }
}
