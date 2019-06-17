package com.sunmi.assistant.dashboard.type;

import android.support.annotation.NonNull;

import com.sunmi.assistant.dashboard.model.BarChartCard;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class BarChartCardType extends ItemType<BarChartCard, BaseViewHolder<BarChartCard>> {

    @Override
    public int getLayoutId(int type) {
        return 0;
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<BarChartCard> holder, BarChartCard model, int position) {

    }
}
