package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderAvgUnitSaleResp;
import com.sunmi.assistant.order.model.OrderInfo;
import com.sunmi.assistant.utils.Utils;

import retrofit2.Call;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-07-23
 */
public class CustomerPriceCard extends BaseSmallCard<CustomerPriceCard.Model, OrderAvgUnitSaleResp> {

    public CustomerPriceCard(Context context, DashboardContract.Presenter presenter,
                             int companyId, int shopId) {
        super(context, presenter, companyId, shopId, OrderInfo.ORDER_TYPE_NORMAL);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_customer_price));
    }

    @Override
    protected Call<BaseResponse<OrderAvgUnitSaleResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(DashboardContract.TIME_PERIOD_TODAY);
        return SunmiStoreRemote.get().getOrderAvgUnitSale(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1, callback);
    }

    @Override
    protected void setupModel(Model model, OrderAvgUnitSaleResp response) {
        model.dataToday = response.getDay_unit_sale();
        model.dataWeek = response.getWeek_unit_sale();
        model.dataMonth = response.getMonth_unit_sale();
        model.trendDataToday = TextUtils.isEmpty(response.getDay_rate()) ? DATA_NONE : response.getDay_rate();
        model.trendDataWeek = TextUtils.isEmpty(response.getWeek_rate()) ? DATA_NONE : response.getWeek_rate();
        model.trendDataMonth = TextUtils.isEmpty(response.getMonth_rate()) ? DATA_NONE : response.getMonth_rate();
    }

    @Override
    protected String getDataFormat() {
        return FORMAT_FLOAT_DOUBLE_DECIMAL;
    }

    public static class Model extends BaseSmallCard.BaseSmallModel {
        Model(String title) {
            super(title);
        }
    }
}
