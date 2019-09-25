package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
import com.sunmi.assistant.dashboard.DashboardContract;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class PeriodTabCard extends BaseRefreshItem<PeriodTabCard.Model, Object> {


    public PeriodTabCard(Context context, DashboardContract.Presenter presenter) {
        super(context, presenter);
        addOnViewClickListener(R.id.tv_dashboard_today, (adapter, holder, v, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_TODAY));
        addOnViewClickListener(R.id.tv_dashboard_week, (adapter, holder, v, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_WEEK));
        addOnViewClickListener(R.id.tv_dashboard_month, (adapter, holder, v, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_MONTH));
    }

    @Override
    protected Model createModel(Context context) {
        return new Model();
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_period_tab;
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
        TextView today = holder.getView(R.id.tv_dashboard_today);
        TextView week = holder.getView(R.id.tv_dashboard_week);
        TextView month = holder.getView(R.id.tv_dashboard_month);
        today.setSelected(model.period == Constants.TIME_PERIOD_TODAY);
        today.setTypeface(null, model.period == Constants.TIME_PERIOD_TODAY ? Typeface.BOLD : Typeface.NORMAL);
        week.setSelected(model.period == Constants.TIME_PERIOD_WEEK);
        week.setTypeface(null, model.period == Constants.TIME_PERIOD_WEEK ? Typeface.BOLD : Typeface.NORMAL);
        month.setSelected(model.period == Constants.TIME_PERIOD_MONTH);
        month.setTypeface(null, model.period == Constants.TIME_PERIOD_MONTH ? Typeface.BOLD : Typeface.NORMAL);
    }

    public static class Model extends BaseRefreshItem.BaseModel {
    }
}
