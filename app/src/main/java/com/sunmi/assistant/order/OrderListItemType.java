package com.sunmi.assistant.order;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Utils;
import com.sunmi.assistant.order.model.OrderInfo;

import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class OrderListItemType extends ItemType<OrderInfo, BaseViewHolder<OrderInfo>> {

    @Override
    public int getLayoutId(int type) {
        return R.layout.order_list_item;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<OrderInfo> holder, OrderInfo model, int position) {
        TextView orderNo = holder.getView(R.id.order_item_no);
        TextView orderAmount = holder.getView(R.id.order_item_amount);
        TextView orderPayType = holder.getView(R.id.order_item_pay_type);
        TextView orderTime = holder.getView(R.id.order_item_time);
        orderNo.setText(model.getNo());
        orderAmount.setText(holder.getContext().getResources().getString(
                R.string.order_amount, model.getAmount()));
        orderPayType.setText(holder.getContext().getResources().getString(
                R.string.order_pay_method_colon, model.getPurchaseType()));
        orderTime.setText(Utils.getHourMinute(model.getPurchaseTime()));
        if (model.getAmount() < 0) {
            orderAmount.setTextColor(holder.getContext().getResources().getColor(R.color.color_F35000));
        } else {
            orderAmount.setTextColor(holder.getContext().getResources().getColor(R.color.text_main));
        }
    }

}