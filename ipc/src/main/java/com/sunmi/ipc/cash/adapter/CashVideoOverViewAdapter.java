package com.sunmi.ipc.cash.adapter;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.ipc.R;

import java.util.List;

import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.utils.GlideRoundTransform;
import sunmi.common.view.CircleImage;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-23.
 */
public class CashVideoOverViewAdapter extends BaseQuickAdapter<CashVideoServiceBean, BaseViewHolder> {


    private Context context;
    private OnItemClickListener onItemClickListener;

    public CashVideoOverViewAdapter(List<CashVideoServiceBean> data, Context context) {
        super(R.layout.item_cash_video_overview, data);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, CashVideoServiceBean item) {
        CircleImage civIpc = helper.getView(R.id.civ_ipc);
        Glide.with(context).load(item.getImgUrl()).transform(new GlideRoundTransform(context)).into(civIpc);
        helper.setText(R.id.tv_ipc_name, item.getDeviceName());
        helper.setText(R.id.tv_ipc_sn, context.getString(R.string.str_dev_sn, item.getDeviceSn()));
        helper.setText(R.id.tv_count_cash, String.valueOf(item.getTotalCount()));
        helper.setText(R.id.tv_count_abnormal, String.valueOf(item.getAbnormalVideoCount()));
        if (onItemClickListener != null) {
            helper.getView(R.id.cv_order).setOnClickListener(v -> onItemClickListener.onOrderClick(item, helper.getAdapterPosition()));
            helper.getView(R.id.cv_abnormal_order).setOnClickListener(v -> onItemClickListener.onAbnormalOrderClick(item, helper.getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onOrderClick(CashVideoServiceBean item, int position);

        void onAbnormalOrderClick(CashVideoServiceBean item, int position);
    }
}