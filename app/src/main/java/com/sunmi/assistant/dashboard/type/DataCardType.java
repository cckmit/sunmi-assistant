package com.sunmi.assistant.dashboard.type;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.model.BaseRefreshCard;
import com.sunmi.assistant.dashboard.model.DataCard;

import java.util.Locale;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class DataCardType extends ItemType<DataCard, BaseViewHolder<DataCard>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.dashboard_recycle_item_data_card;
    }

    @Override
    public int getSpanSize() {
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<DataCard> holder, DataCard model, int position) {
        TextView title = holder.getView(R.id.tv_dashboard_title);
        TextView data = holder.getView(R.id.tv_dashboard_data);
        TextView trendName = holder.getView(R.id.tv_dashboard_trend_name);
        TextView trendData = holder.getView(R.id.tv_dashboard_trend_data);
        title.setText(model.title);
        if (model.state == BaseRefreshCard.STATE_INIT) {
            return;
        }
        if (model.state == BaseRefreshCard.STATE_NEED_REFRESH) {
            // TODO
        } else {
            data.setText(String.format(Locale.getDefault(), model.dataFormat, model.data));
            trendName.setText(model.trendName);
            trendData.setText(String.format(Locale.getDefault(), "%.0f%%", model.trendData * 100));
            if (model.trendData >= 0) {
                trendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_up, 0, 0, 0);
                trendData.setTextColor(Color.parseColor("#FF0000"));
            } else {
                trendData.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.dashboard_ic_trend_down, 0, 0, 0);
                trendData.setTextColor(Color.parseColor("#00B552"));
            }
        }
    }
}
