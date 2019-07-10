package com.sunmi.assistant.dashboard.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class TopTabCard extends BaseRefreshCard<TopTabCard.Model> {

    private static final String TAG = "TopTabCard";

    public TopTabCard(Context context, int period) {
        super(context, -1, -1, period);
    }

    @Override
    protected Model createData() {
        return new Model();
    }

    @Override
    protected ItemType<Model, BaseViewHolder<Model>> createType() {
        return new TopTabType();
    }

    @Override
    protected void load(int companyId, int shopId, int period, Model o) {
    }

    public class TopTabType extends ItemType<Model, BaseViewHolder<Model>> {

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
        public BaseViewHolder<Model> onCreateViewHolder(@NonNull View view, @NonNull ItemType<Model, BaseViewHolder<Model>> type) {
            int topPadding = (int) view.getContext().getResources().getDimension(R.dimen.dp_12);
            view.setPaddingRelative(0, topPadding, 0, 0);
            return super.onCreateViewHolder(view, type);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<Model> holder, Model model, int position) {
            LogCat.d(TAG, "Top tab CARD view setup.");
            TextView today = holder.getView(R.id.tv_dashboard_today);
            TextView week = holder.getView(R.id.tv_dashboard_week);
            TextView month = holder.getView(R.id.tv_dashboard_month);
            today.setSelected(getPeriod() == DashboardContract.TIME_PERIOD_TODAY);
            week.setSelected(getPeriod() == DashboardContract.TIME_PERIOD_WEEK);
            month.setSelected(getPeriod() == DashboardContract.TIME_PERIOD_MONTH);
        }

    }

    public static class Model {
    }
}
