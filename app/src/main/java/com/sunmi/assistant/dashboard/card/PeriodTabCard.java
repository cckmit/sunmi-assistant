package com.sunmi.assistant.dashboard.card;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;
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
public class PeriodTabCard extends BaseRefreshCard<PeriodTabCard.Model, Object> {

    private static PeriodTabCard sInstance;

    private PeriodTabCard(DashboardContract.Presenter presenter, int source) {
        super(presenter, source);
    }

    public static PeriodTabCard init(DashboardContract.Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new PeriodTabCard(presenter, source);
        } else {
            sInstance.init(source);
        }
        return sInstance;
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
        holder.addOnClickListener(R.id.tv_dashboard_today, (h, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_TODAY));
        holder.addOnClickListener(R.id.tv_dashboard_week, (h, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_WEEK));
        holder.addOnClickListener(R.id.tv_dashboard_month, (h, model, position) ->
                mPresenter.switchPeriodTo(Constants.TIME_PERIOD_MONTH));
        return holder;
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

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
