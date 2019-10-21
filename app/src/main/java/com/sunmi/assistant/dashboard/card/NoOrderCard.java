package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.importorder.ImportOrderPreviewActivity_;

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

    private NoOrderCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static NoOrderCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new NoOrderCard(presenter, source);
        } else {
            sInstance.reset(source);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {
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
    protected Model createModel() {
        return new Model();
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        Context context = holder.getContext();
        this.mColorGray = ContextCompat.getColor(context, R.color.common_fill);
        this.mColorWhite = 0xFFFFFFFF;
        float radius = context.getResources().getDimension(R.dimen.dp_12);
        this.mContentBg = new GradientDrawable();
        this.mContentBg.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});

        holder.addOnClickListener(R.id.btn_dashboard_dock, (h, model, position) -> {
            ImportOrderPreviewActivity_.intent(context).start();
        });

        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        View root = holder.getView(R.id.layout_dashboard_root);
        View content = holder.getView(R.id.layout_dashboard_content);
        root.setBackgroundColor(!hasAuth() && !hasFs() ? mColorWhite : mColorGray);
        mContentBg.setColor(!hasAuth() && !hasFs() ? mColorGray : mColorWhite);
        content.setBackground(mContentBg);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
