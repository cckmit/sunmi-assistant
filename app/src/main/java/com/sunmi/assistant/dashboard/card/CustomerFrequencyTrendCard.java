package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.CustomerFrequencyTrendResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-16.
 */
public class CustomerFrequencyTrendCard extends BaseRefreshCard<CustomerFrequencyTrendCard.Model, CustomerFrequencyTrendResp> {

    private static CustomerFrequencyTrendCard sInstance;

    private CustomerFrequencyTrendCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyTrendCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyTrendCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return 0;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    protected Call<BaseResponse<CustomerFrequencyTrendResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        String group = "day";
        if (period == Constants.TIME_PERIOD_MONTH) {
            group = "week";
        }
        SunmiStoreApi.getInstance().getCustomerFrequencyTrend(companyId, shopId, period, group, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return null;
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyTrendResp response) {

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {

    }

    public static class Model extends BaseRefreshCard.BaseModel {

    }
}
