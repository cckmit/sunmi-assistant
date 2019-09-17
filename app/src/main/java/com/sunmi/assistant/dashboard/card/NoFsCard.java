package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class NoFsCard extends BaseRefreshItem<NoFsCard.Model, Object> {

    private boolean mIsAllEmpty;

    public NoFsCard(Context context, DashboardContract.Presenter presenter, boolean isAllEmpty) {
        super(context, presenter, 0);
        this.mIsAllEmpty = isAllEmpty;
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_no_fs;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        return null;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = new BaseViewHolder<>(view, type);
        View root = holder.getView(R.id.layout_dashboard_root);
        View content = holder.getView(R.id.layout_dashboard_content);
        int gray = ContextCompat.getColor(view.getContext(), R.color.color_F5F7FA);
        int white = 0xFFFFFFFF;
        root.setBackgroundColor(mIsAllEmpty ? white : gray);
        content.setBackgroundColor(mIsAllEmpty ? gray : white);
        return holder;
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
