package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.model.Tab;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class TabType extends ItemType<Tab, BaseViewHolder<Tab>> {

    private static final String TAG = "TabType";

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_tab;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Tab> holder, Tab model, int position) {
        TextView today = holder.getView(R.id.tv_dashboard_today);
        TextView week = holder.getView(R.id.tv_dashboard_week);
        TextView month = holder.getView(R.id.tv_dashboard_month);
        today.setSelected(model.timeSpan == DashboardContract.TIME_SPAN_TODAY);
        week.setSelected(model.timeSpan == DashboardContract.TIME_SPAN_WEEK);
        month.setSelected(model.timeSpan == DashboardContract.TIME_SPAN_MONTH);
    }

}
