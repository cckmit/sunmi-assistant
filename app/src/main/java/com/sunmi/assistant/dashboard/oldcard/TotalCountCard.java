package com.sunmi.assistant.dashboard.oldcard;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.PaymentApi;
import com.sunmi.assistant.data.response.OrderTotalCountResp;
import com.sunmi.assistant.order.model.OrderInfo;
import com.sunmi.assistant.utils.Utils;

import retrofit2.Call;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-07-23
 */
public class TotalCountCard extends BaseSmallCard<TotalCountCard.Model, OrderTotalCountResp> {

    public TotalCountCard(Context context, DashboardContract.Presenter presenter,
                          int companyId, int shopId) {
        super(context, presenter, companyId, shopId, OrderInfo.ORDER_TYPE_NORMAL);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_data_sales_volume));
    }

    @Override
    protected Call<BaseResponse<OrderTotalCountResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(Constants.TIME_PERIOD_TODAY);
        return PaymentApi.get().getOrderTotalCount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1, callback);
    }

    @Override
    protected void setupModel(Model model, OrderTotalCountResp response) {
        model.dataToday = response.getDayCount();
        model.dataWeek = response.getWeekCount();
        model.dataMonth = response.getMonthCount();
        model.trendDataToday = TextUtils.isEmpty(response.getDayRate()) ? DATA_NONE : response.getDayRate();
        model.trendDataWeek = TextUtils.isEmpty(response.getWeekRate()) ? DATA_NONE : response.getWeekRate();
        model.trendDataMonth = TextUtils.isEmpty(response.getMonthRate()) ? DATA_NONE : response.getMonthRate();
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
