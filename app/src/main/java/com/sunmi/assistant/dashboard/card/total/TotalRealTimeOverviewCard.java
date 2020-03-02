package com.sunmi.assistant.dashboard.card.total;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.dashboard.card.BaseRefreshCard;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

public class TotalRealTimeOverviewCard extends BaseRefreshCard<TotalRealTimeOverviewCard.Model, Object> {

    private static TotalRealTimeOverviewCard sInstance;

    private TotalRealTimeOverviewCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public TotalRealTimeOverviewCard get(Presenter presenter, int source){
        if (sInstance == null){
            sInstance = new TotalRealTimeOverviewCard(presenter,source);
        }else {
            sInstance.reset(presenter, source);
        }
        return sInstance;
    }


    @Override
    public void init(Context context) {

    }

    @Override
    public int getLayoutId(int type) {
        return 0;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        return null;
    }

    @Override
    protected Model createModel() {
        return null;
    }

    @Override
    protected void setupModel(Model model, Object response) {

    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {

    }

    public static class Model extends BaseRefreshCard.BaseModel {

    }
}
