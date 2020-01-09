package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class ProfileWaitDataCard extends BaseRefreshCard<ProfileWaitDataCard.Model, Object> {

    private static ProfileWaitDataCard sInstance;

    private ProfileWaitDataCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static ProfileWaitDataCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new ProfileWaitDataCard(presenter, source);
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
        return R.layout.dashboard_recycle_item_empty;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, CardCallback callback) {
        return null;
    }

    @Override
    protected Model createModel() {
        return new Model();
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        BaseViewHolder<Model> holder = super.onCreateViewHolder(view, type);
        TextView tip = holder.getView(R.id.tv_dashboard_tip);
        tip.setText(R.string.dashboard_no_customer_wait_tip);
        int paddingTop = (int) view.getContext().getResources().getDimension(R.dimen.dp_120);
        view.setPaddingRelative(0, paddingTop, 0, 0);
        return holder;
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
