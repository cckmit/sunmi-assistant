package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.Interval;
import sunmi.common.model.TotalCustomerDataResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.DateTimeUtils;

public class TotalCustomerOverviewCard extends BaseRefreshCard<TotalCustomerOverviewCard.Model, TotalCustomerDataResp> {

    private static TotalCustomerOverviewCard sInstance;

    private TotalCustomerOverviewCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static TotalCustomerOverviewCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new TotalCustomerOverviewCard(presenter, condition, period, periodTime);
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
        return R.layout.dashboard_item_total_customer_overview;
    }

    @Override
    protected Call<BaseResponse<TotalCustomerDataResp>> load(int companyId, int shopId, int period, Interval periodTime, CardCallback callback) {
        String startTime = DateTimeUtils.secondToDate(periodTime.start, "yyyy-MM-dd");
        SunmiStoreApi.getInstance().getTotalCustomerData(companyId, startTime, period, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return null;
    }

    @Override
    protected void setupModel(Model model, TotalCustomerDataResp response) {

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {

    }

    @Override
    protected void showLoading(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showLoading(holder, model, position);
    }

    @Override
    protected void showError(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        super.showError(holder, model, position);
    }

    public static class Model extends BaseRefreshCard.BaseModel {

    }
}
