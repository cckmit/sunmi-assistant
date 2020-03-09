package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

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
public class RealtimeGapCard extends BaseRefreshCard<RealtimeGapCard.Model, Object> {

    private static RealtimeGapCard sInstance;

    private RealtimeGapCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static RealtimeGapCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new RealtimeGapCard(presenter, condition, period, periodTime);
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
        return R.layout.dashboard_item_realtime_gap;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
        callback.onSuccess();
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
        Context context = holder.getContext();
        int color;
        int height;
        if (mCondition.hasSaas || mCondition.hasFs) {
            color = ContextCompat.getColor(context, R.color.common_fill);
        } else {
            color = 0xFFFFFFFF;
        }
        if (mCondition.hasFloating) {
            height = (int) context.getResources().getDimension(R.dimen.dp_80);
        } else if (mCondition.hasSaas && mCondition.hasFs) {
            height = (int) context.getResources().getDimension(R.dimen.dp_24);
        } else {
            height = (int) context.getResources().getDimension(R.dimen.dp_32);
        }
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        lp.height = height;
        holder.itemView.setBackgroundColor(color);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
