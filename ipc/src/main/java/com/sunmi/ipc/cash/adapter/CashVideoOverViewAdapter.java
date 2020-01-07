package com.sunmi.ipc.cash.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.ipc.R;

import java.util.List;

import sunmi.common.model.CashServiceInfo;
import sunmi.common.utils.GlideRoundTransform;
import sunmi.common.view.CircleImage;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-23.
 */
public class CashVideoOverViewAdapter extends BaseQuickAdapter<CashServiceInfo, BaseViewHolder> {


    private Context context;
    private OnItemClickListener onItemClickListener;
    private int behaviorPos;

    public CashVideoOverViewAdapter(List<CashServiceInfo> data, Context context) {
        super(R.layout.item_cash_video_overview, data);
        this.context = context;
        this.behaviorPos = 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, CashServiceInfo item) {
        CircleImage civIpc = helper.getView(R.id.civ_ipc);
        Glide.with(context).load(item.getImgUrl()).transform(new GlideRoundTransform(context)).into(civIpc);
        helper.setText(R.id.tv_ipc_name, item.getDeviceName());
        helper.setText(R.id.tv_ipc_sn, context.getString(R.string.str_dev_sn, item.getDeviceSn()));
        helper.setText(R.id.tv_count_cash, String.valueOf(item.getTotalCount()));
        helper.setText(R.id.tv_count_abnormal, String.valueOf(item.getAbnormalVideoCount()));
        CardView cardView = helper.getView(R.id.cv_abnormal_behavior);
        if (item.isHasCashLossPrevention()) {
            behaviorPos++;
            cardView.setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_count_abnormal_behavior, String.valueOf(item.getAbnormalBehaviorCount()));
        } else {
            cardView.setVisibility(View.GONE);
        }
        if (onItemClickListener != null) {
            helper.getView(R.id.cv_order).setOnClickListener(v -> onItemClickListener.onOrderClick(item, helper.getAdapterPosition()));
            helper.getView(R.id.cv_abnormal_order).setOnClickListener(v -> onItemClickListener.onAbnormalOrderClick(item, helper.getAdapterPosition()));
            cardView.setOnClickListener(v -> onItemClickListener.onAbnormalBehaviorClick(item, behaviorPos));
        }
    }

    public interface OnItemClickListener {
        void onOrderClick(CashServiceInfo item, int position);

        void onAbnormalOrderClick(CashServiceInfo item, int position);

        void onAbnormalBehaviorClick(CashServiceInfo item, int position);
    }
}
