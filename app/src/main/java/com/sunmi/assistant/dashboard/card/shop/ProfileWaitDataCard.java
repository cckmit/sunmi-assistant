package com.sunmi.assistant.dashboard.card.shop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.model.Interval;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class ProfileWaitDataCard extends BaseRefreshCard<ProfileWaitDataCard.Model, Object> {

    private static ProfileWaitDataCard sInstance;

    private ProfileWaitDataCard(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        super(presenter, condition, period, periodTime);
    }

    public static ProfileWaitDataCard get(Presenter presenter, DashboardCondition condition, int period, Interval periodTime) {
        if (sInstance == null) {
            sInstance = new ProfileWaitDataCard(presenter, condition, period, periodTime);
        } else {
            sInstance.reset(presenter, condition, period, periodTime);
        }
        return sInstance;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_item_empty;
    }

    @Override
    protected Call<BaseResponse<Object>> load(int companyId, int shopId, int period, Interval periodTime,
                                              CardCallback callback) {
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
        tip.setText(R.string.dashboard_tip_no_customer_wait);
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
