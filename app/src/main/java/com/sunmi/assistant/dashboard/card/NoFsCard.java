package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.constant.CommonConstants;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.activity.StartConfigSMDeviceActivity_;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class NoFsCard extends BaseRefreshCard<NoFsCard.Model, Object> {

    private static NoFsCard sInstance;

    private int mColorGray;
    private int mColorWhite;
    private GradientDrawable mContentBg;

    private NoFsCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static NoFsCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new NoFsCard(presenter, source);
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
        return R.layout.dashboard_recycle_item_no_fs;
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

        holder.addOnClickListener(R.id.btn_dashboard_add, (h, model, position) ->
                StartConfigSMDeviceActivity_.intent(context)
                        .deviceType(CommonConstants.TYPE_IPC_FS)
                        .shopId(SpUtils.getShopId() + "")
                        .start());

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
