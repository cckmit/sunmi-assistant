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

    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;

    public NoOrderCard(Context context, DashboardContract.Presenter presenter, boolean isAllEmpty) {
        super(context, presenter, 0);
        Model model = getModel();
        model.isAllEmpty = isAllEmpty;
        model.isValid = true;
        mColorGray = ContextCompat.getColor(context, R.color.color_F5F7FA);
        mColorWhite = 0xFFFFFFFF;
        float radius = context.getResources().getDimension(R.dimen.dp_12);
        mContentBg = new GradientDrawable();
        mContentBg.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
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
        return null;
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        View root = holder.getView(R.id.layout_dashboard_root);
        View content = holder.getView(R.id.layout_dashboard_content);
        root.setBackgroundColor(model.isAllEmpty ? mColorWhite : mColorGray);
        mContentBg.setColor(model.isAllEmpty ? mColorGray : mColorWhite);
        content.setBackground(mContentBg);
    }

    public static class Model extends BaseRefreshItem.BaseModel {
        private boolean isAllEmpty;
    }
}
