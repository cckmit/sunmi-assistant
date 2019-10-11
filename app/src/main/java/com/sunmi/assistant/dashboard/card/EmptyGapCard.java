package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;


/**
 * @author yinhui
 * @since 2019-07-01
 */
public class EmptyGapCard extends BaseRefreshCard<EmptyGapCard.Model, Object> {

    private static EmptyGapCard sInstance;

    private EmptyGapCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static EmptyGapCard init(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new EmptyGapCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_gap;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        callback.onSuccess();
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
        Context context = holder.getContext();
        int color;
        int height;
        if (hasSaas() || hasFs()) {
            color = ContextCompat.getColor(context, R.color.color_F5F7FA);
        } else {
            color = 0xFFFFFFFF;
        }
        if (hasSaas() && hasFs()) {
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
