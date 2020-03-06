package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeNoDataCard extends BaseRefreshCard<RealtimeNoDataCard.Model, Object> {

    private static RealtimeNoDataCard sInstance;

    private RealtimeNoDataCard(Presenter presenter, DashboardCondition condition) {
        super(presenter, condition);
    }

    public static RealtimeNoDataCard get(Presenter presenter, DashboardCondition condition) {
        if (sInstance == null) {
            sInstance = new RealtimeNoDataCard(presenter, condition);
        } else {
            sInstance.reset(presenter, condition);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_empty;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
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
