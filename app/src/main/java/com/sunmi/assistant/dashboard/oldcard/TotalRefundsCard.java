package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderTotalRefundsResp;
import com.sunmi.assistant.order.model.OrderInfo;

import retrofit2.Call;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-07-23
 */
public class TotalRefundsCard extends BaseSmallCard<TotalRefundsCard.Model, OrderTotalRefundsResp> {

    public TotalRefundsCard(Context context, DashboardContract.Presenter presenter,
                            int companyId, int shopId) {
        super(context, presenter, companyId, shopId, OrderInfo.ORDER_TYPE_REFUNDS);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_total_refunds));
    }

    @Override
    protected Call<BaseResponse<OrderTotalRefundsResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        return PaymentApi.get().getOrderRefundCount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1, callback);
    }

    @Override
    protected void setupModel(Model model, OrderTotalRefundsResp response) {
        model.dataToday = response.getDay_refund();
        model.dataWeek = response.getWeek_refund();
        model.dataMonth = response.getMonth_refund();
        model.trendDataToday = TextUtils.isEmpty(response.getDay_rate()) ? DATA_NONE : response.getDay_rate();
        model.trendDataWeek = TextUtils.isEmpty(response.getWeek_rate()) ? DATA_NONE : response.getWeek_rate();
        model.trendDataMonth = TextUtils.isEmpty(response.getMonth_rate()) ? DATA_NONE : response.getMonth_rate();
    }

    @Override
    protected String getDataFormat() {
        return FORMAT_FLOAT_NO_DECIMAL;
    }

    public static class Model extends BaseSmallCard.BaseSmallModel {
        Model(String title) {
            super(title);
        }
    }
}
