package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.model.Interval;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeNoDataCard extends BaseRefreshCard<RealtimeNoDataCard.Model, Object> {

    private static RealtimeNoDataCard sInstance;

    private RealtimeNoDataCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static RealtimeNoDataCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new RealtimeNoDataCard(presenter, condition, period, periodTime);
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
        return R.layout.dashboard_item_empty;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
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
