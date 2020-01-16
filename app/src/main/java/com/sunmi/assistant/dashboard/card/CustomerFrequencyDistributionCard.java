package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.ui.chart.ChartEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.CustomerFrequencyDistributionResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-15.
 */
public class CustomerFrequencyDistributionCard extends BaseRefreshCard<CustomerFrequencyDistributionCard.Model,
        CustomerFrequencyDistributionResp> {

    private static CustomerFrequencyDistributionCard sInstance;


    private CustomerFrequencyDistributionCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerFrequencyDistributionCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerFrequencyDistributionCard(presenter, source);
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
    protected Call<BaseResponse<CustomerFrequencyDistributionResp>> load(int companyId, int shopId, int period, CardCallback callback) {
        SunmiStoreApi.getInstance().getCustomerFrequencyDistribution(companyId, shopId, period, callback);
        return null;
    }

    @Override
    protected Model createModel() {
        return null;
    }

    @Override
    protected void setupModel(Model model, CustomerFrequencyDistributionResp response) {

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {

    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private List<ChartEntry> dataSet = new ArrayList<>();
    }
}
