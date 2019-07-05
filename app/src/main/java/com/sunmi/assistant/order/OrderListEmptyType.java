package com.sunmi.assistant.order;

import android.support.annotation.NonNull;

import com.sunmi.assistant.R;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-05
 */
public class OrderListEmptyType extends ItemType<Object, BaseViewHolder<Object>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.order_list_empty;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Object> holder, Object model, int position) {
    }

}