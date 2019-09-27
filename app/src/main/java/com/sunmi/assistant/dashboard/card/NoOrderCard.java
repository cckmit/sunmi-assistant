package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class NoOrderCard extends BaseRefreshItem<NoOrderCard.Model, Object> {

    private boolean isAllEmpty;
    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;

    public NoOrderCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter);
        this.mColorGray = ContextCompat.getColor(context, R.color.color_F5F7FA);
        this.mColorWhite = 0xFFFFFFFF;
        float radius = context.getResources().getDimension(R.dimen.dp_12);
        this.mContentBg = new GradientDrawable();
        this.mContentBg.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        this.isInit = true;
    }

    public void setIsAllEmpty(boolean isAllEmpty) {
        this.isAllEmpty = isAllEmpty;
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_no_order;
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
        View root = holder.getView(R.id.layout_dashboard_root);
        View content = holder.getView(R.id.layout_dashboard_content);
        root.setBackgroundColor(isAllEmpty ? mColorWhite : mColorGray);
        mContentBg.setColor(isAllEmpty ? mColorGray : mColorWhite);
        content.setBackground(mContentBg);
    }

    public static class Model extends BaseRefreshItem.BaseModel {
    }
}