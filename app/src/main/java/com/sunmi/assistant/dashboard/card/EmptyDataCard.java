package com.sunmi.assistant.dashboard.card;

import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class EmptyDataCard extends BaseRefreshCard<EmptyDataCard.Model, Object> {

    private static EmptyDataCard sInstance;

    private EmptyDataCard(DashboardContract.Presenter presenter, int source) {
        super(presenter, source);
    }

    public static EmptyDataCard init(DashboardContract.Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new EmptyDataCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_empty;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        return null;
    }

    @Override
    protected List<Model> createModel() {
        ArrayList<Model> models = new ArrayList<>();
        models.add(new Model());
        return models;
    }

    @Override
    protected void setupModel(List<Model> models, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
