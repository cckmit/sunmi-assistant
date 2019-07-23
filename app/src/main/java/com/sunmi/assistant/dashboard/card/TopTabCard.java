package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TopTabCard extends BaseRefreshCard<TopTabCard.Model, Object> {


    public TopTabCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter, -1, -1);
        addOnViewClickListener(R.id.tv_dashboard_today, (adapter, holder, v, model, position) -> {
            mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_TODAY);
        });
        addOnViewClickListener(R.id.tv_dashboard_week, (adapter, holder, v, model, position) -> {
            mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_WEEK);
        });
        addOnViewClickListener(R.id.tv_dashboard_month, (adapter, holder, v, model, position) -> {
            mPresenter.switchPeriodTo(DashboardContract.TIME_PERIOD_MONTH);
        });
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_tab;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @NonNull
    @Override
    public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view,
                                                    @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
        int topPadding = (int) view.getContext().getResources().getDimension(R.dimen.dp_12);
        view.setPaddingRelative(0, topPadding, 0, 0);
        return super.onCreateViewHolder(view, type);
    }

    @Override
    protected void load(int companyId, int shopId, int period, CardCallback callback) {
        callback.onSuccess();
    }

    @Override
    protected void setupModel(Model model, Object response) {
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView today = holder.getView(R.id.tv_dashboard_today);
        TextView week = holder.getView(R.id.tv_dashboard_week);
        TextView month = holder.getView(R.id.tv_dashboard_month);
        today.setSelected(model.period == DashboardContract.TIME_PERIOD_TODAY);
        week.setSelected(model.period == DashboardContract.TIME_PERIOD_WEEK);
        month.setSelected(model.period == DashboardContract.TIME_PERIOD_MONTH);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
