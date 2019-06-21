package com.sunmi.assistant.dashboard;

import com.sunmi.assistant.dashboard.data.OrderManagementRemote;
import com.sunmi.assistant.dashboard.data.response.TotalAmountResponse;
import com.sunmi.assistant.dashboard.model.DataCard;

import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * 为每个Card实现数据更新能力
 *
 * @author jacob
 * @since 2019-06-21
 */
public abstract class DataRefreshHelper<T> {

    public abstract void refresh(T model, boolean updateTitle);

    public static class TotalSalesAmountRefresh extends DataRefreshHelper<DataCard> {

        private int companyId;
        private int shopId;

        public TotalSalesAmountRefresh(int companyId, int shopId) {
            this.companyId = companyId;
            this.shopId = shopId;
        }

        @Override
        public void refresh(DataCard model, boolean updateTitle) {
            if (updateTitle) {
                model.trendName = Utils.getTrendNameByTimeSpan(model.timeSpan);
            }
            OrderManagementRemote.get().getTotalAmount(companyId, shopId,
                    model.timeSpanPair.first, model.timeSpanPair.second, 1,
                    new RetrofitCallback<TotalAmountResponse>() {
                        @Override
                        public void onSuccess(int code, String msg, TotalAmountResponse data) {
                            model.data = data.getTotal_amount();
                            if (model.timeSpan == DashboardContract.TIME_SPAN_MONTH) {
                                model.trendData = data.getMonth_rate();
                            } else if (model.timeSpan == DashboardContract.TIME_SPAN_WEEK) {
                                model.trendData = data.getWeek_rate();
                            } else {
                                model.trendData = data.getDay_rate();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg, TotalAmountResponse data) {
                        }
                    });
        }
    }
}
