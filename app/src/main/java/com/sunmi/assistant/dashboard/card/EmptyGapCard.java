package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.sunmi.assistant.R;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class EmptyGapCard extends BaseRefreshItem<EmptyGapCard.Model, Object> {

    private int mColor;
    private int mHeight;

    public EmptyGapCard(@ColorInt int color, int height) {
        super(null, null, 0);
        this.mColor = color;
        this.mHeight = height;
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
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

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = mHeight;
        view.setBackgroundColor(mColor);
        return super.onCreateViewHolder(view, type);
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
    }

    public static class Model extends BaseRefreshItem.BaseModel {
    }
}
