package com.sunmi.ipc.cash.adapter;

import android.content.Context;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.CashOrderResp;

import java.util.List;

import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * @author yangShiJie
 * @date 2019-12-04
 */
public class CashAdapter extends CommonListAdapter<CashOrderResp.ProductListBean> {

    /**
     * @param context 上下文
     * @param list    列表数据
     */
    public CashAdapter(Context context, List<CashOrderResp.ProductListBean> list) {
        super(context, R.layout.cash_item_goods_details, list);
    }

    @Override
    public void convert(ViewHolder holder, CashOrderResp.ProductListBean resp) {
        holder.setText(R.id.tv_goods_name, resp.getName());
        holder.setText(R.id.tv_goods_quality, "x" + resp.getQuantity());
    }
}
