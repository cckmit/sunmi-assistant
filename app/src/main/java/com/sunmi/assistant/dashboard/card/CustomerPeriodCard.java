package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.Constants;

import retrofit2.Call;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class CustomerPeriodCard extends BaseRefreshCard<CustomerPeriodCard.Model, Object> {

    private static CustomerPeriodCard sInstance;

    private CustomerPeriodCard(Presenter presenter, int source) {
        super(presenter, source);
    }

    public static CustomerPeriodCard get(Presenter presenter, int source) {
        if (sInstance == null) {
            sInstance = new CustomerPeriodCard(presenter, source);
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
        return R.layout.dashboard_item_period_tab;
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
        holder.addOnClickListener(R.id.tv_dashboard_yesterday, (h, model, position) ->
                mPresenter.setPeriod(Constants.TIME_PERIOD_YESTERDAY));
        holder.addOnClickListener(R.id.tv_dashboard_week, (h, model, position) ->
                mPresenter.setPeriod(Constants.TIME_PERIOD_WEEK));
        holder.addOnClickListener(R.id.tv_dashboard_month, (h, model, position) ->
                mPresenter.setPeriod(Constants.TIME_PERIOD_MONTH));
        holder.getView(R.id.tv_dashboard_yesterday).setVisibility(View.VISIBLE);
        return holder;
    }

    @Override
    protected void setupView(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
        TextView yesterday = holder.getView(R.id.tv_dashboard_yesterday);
        TextView week = holder.getView(R.id.tv_dashboard_week);
        TextView month = holder.getView(R.id.tv_dashboard_month);
        yesterday.setSelected(model.period == Constants.TIME_PERIOD_YESTERDAY);
        yesterday.setTypeface(null, model.period == Constants.TIME_PERIOD_YESTERDAY ? Typeface.BOLD : Typeface.NORMAL);
        week.setSelected(model.period == Constants.TIME_PERIOD_WEEK);
        week.setTypeface(null, model.period == Constants.TIME_PERIOD_WEEK ? Typeface.BOLD : Typeface.NORMAL);
        month.setSelected(model.period == Constants.TIME_PERIOD_MONTH);
        month.setTypeface(null, model.period == Constants.TIME_PERIOD_MONTH ? Typeface.BOLD : Typeface.NORMAL);
    }

    public static class Model extends BaseRefreshCard.BaseModel {
    }
}
