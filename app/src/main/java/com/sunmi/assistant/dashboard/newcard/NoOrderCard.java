package com.sunmi.assistant.dashboard.newcard;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class NoOrderCard extends BaseRefreshCard<NoOrderCard.Model, Object> {

    private static NoOrderCard sInstance;

    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;

    private NoOrderCard(DashboardContract.Presenter presenter, int source) {
        super(presenter, source);
    }

    public static NoOrderCard init(DashboardContract.Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new NoOrderCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
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
    protected List<Model> createModel() {
        ArrayList<Model> models = new ArrayList<>();
        models.add(new Model());
        return models;
    }

    @Override
    protected void setupModel(List<Model> models, Object response) {
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = holder.getContext();
        this.mColorGray = ContextCompat.getColor(context, R.color.color_F5F7FA);
        this.mColorWhite = 0xFFFFFFFF;
        float radius = context.getResources().getDimension(R.dimen.dp_12);
        this.mContentBg = new GradientDrawable();
        this.mContentBg.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        View root = holder.getView(R.id.layout_dashboard_root);
        View content = holder.getView(R.id.layout_dashboard_content);
        root.setBackgroundColor(model.isAllEmpty ? mColorWhite : mColorGray);
        mContentBg.setColor(model.isAllEmpty ? mColorGray : mColorWhite);
        content.setBackground(mContentBg);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
        private boolean isAllEmpty;

        public void setAllEmpty(boolean allEmpty) {
            isAllEmpty = allEmpty;
        }
    }
}
