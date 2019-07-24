package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
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
        return new Model(context.getString(R.string.dashboard_total_sales_volume));
    }

    @Override
    protected Call<BaseResponse<OrderTotalCountResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(DashboardContract.TIME_PERIOD_TODAY);
        return SunmiStoreRemote.get().getOrderTotalCount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1, callback);
    }

    @Override
    protected void setupModel(Model model, OrderTotalCountResp response) {
        model.dataToday = response.getDay_count();
        model.dataWeek = response.getWeek_count();
        model.dataMonth = response.getMonth_count();
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
