package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.OrderTotalAmountResp;
import com.sunmi.assistant.order.model.OrderInfo;
import com.sunmi.assistant.utils.Utils;

/**
 * @author yinhui
 * @date 2019-07-23
 */
public class TotalSalesCard extends BaseSmallCard<TotalSalesCard.Model, OrderTotalAmountResp> {

    public TotalSalesCard(Context context, DashboardContract.Presenter presenter,
                          int companyId, int shopId) {
        super(context, presenter, companyId, shopId, OrderInfo.ORDER_TYPE_ALL);
    }

    @Override
    protected Model createModel(Context context) {
        return new Model(context.getString(R.string.dashboard_total_sales_amount));
    }

    @Override
    protected void load(int companyId, int shopId, int period, CardCallback callback) {
        Pair<Long, Long> periodTimestamp = Utils.getPeriodTimestamp(DashboardContract.TIME_PERIOD_TODAY);
        SunmiStoreRemote.get().getOrderTotalAmount(companyId, shopId,
                periodTimestamp.first, periodTimestamp.second, 1, callback);
    }

    @Override
    protected void setupModel(Model model, OrderTotalAmountResp response) {
        model.dataToday = response.getDay_amount();
        model.dataWeek = response.getWeek_amount();
        model.dataMonth = response.getMonth_amount();
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
