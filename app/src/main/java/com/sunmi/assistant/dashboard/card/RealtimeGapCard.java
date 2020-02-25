package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

import com.sunmi.assistant.R;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;


/**
 * @author yinhui
 * @since 2019-07-01
 */
public class RealtimeGapCard extends BaseRefreshCard<RealtimeGapCard.Model, Object> {

    private static RealtimeGapCard sInstance;

    private RealtimeGapCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static RealtimeGapCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new RealtimeGapCard(presenter, source);
        } else {
            sInstance.reset(presenter, source);
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
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
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
        if (hasAuth() || hasFs()) {
            color = ContextCompat.getColor(context, R.color.common_fill);
        } else {
            color = 0xFFFFFFFF;
        }
        if (hasFloating()){
            height = (int) context.getResources().getDimension(R.dimen.dp_80);
        }else if (hasAuth() && hasFs()) {
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
