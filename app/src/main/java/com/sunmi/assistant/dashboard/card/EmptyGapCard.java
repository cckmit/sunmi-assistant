package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sunmi.assistant.R;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class EmptyGapCard extends BaseRefreshItem<EmptyGapCard.Model, Object> {

    private int mColor;
    private int mHeight;

    public EmptyGapCard() {
        super(null, null);
        this.isInit = true;
    }

    public void setHeightAndColor(int height, int color) {
        if (this.mHeight == height && this.mColor == color) {
            return;
        }
        this.mHeight = height;
        this.mColor = color;
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

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        lp.height = mHeight;
        holder.itemView.setBackgroundColor(mColor);
    }

    public static class Model extends BaseRefreshItem.BaseModel {
    }
}
