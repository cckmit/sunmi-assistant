package com.sunmi.ipc.cash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.CashTagManager;
import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.model.CashTag;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import sunmi.common.model.CashServiceInfo;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.GlideRoundTransform;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-06.
 */
public class CashVideoAdapter extends RecyclerView.Adapter<CashVideoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CashVideo> data;
    private HashMap<Integer, CashServiceInfo> serviceInfo;
    private boolean isAbnormalBehavior;
    private GlideRoundTransform transform;

    private OnItemClickListener listener;

    private int selectPosition = -1;
    private NumberFormat numberFormat;
    private CashTagManager tagManager;

    public CashVideoAdapter(Context context, ArrayList<CashVideo> data, HashMap<Integer, CashServiceInfo> serviceInfo,
                            boolean isAbnormalBehavior) {
        this.context = context;
        this.data = data;
        this.serviceInfo = serviceInfo;
        this.isAbnormalBehavior = isAbnormalBehavior;
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        tagManager = CashTagManager.get(context);
        transform = new GlideRoundTransform(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cash_item_trade_details,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (selectPosition != -1) {
            viewHolder.itemView.setSelected(selectPosition == i);
        }
        CashVideo bean = data.get(i);

        int[] tags = bean.getVideoTag();
        int tagId = tags != null && tags.length > 0 ? tags[0] : -1;
        CashTag tag = tagManager.getTag(tagId);
        if (bean.getVideoType() == IpcConstants.CASH_VIDEO_ABNORMAL) {
            viewHolder.tvDescription.setVisibility(View.VISIBLE);
            if (tagId == IpcConstants.CASH_VIDEO_TAG_CUSTOM) {
                viewHolder.tvDescription.setText(bean.getDescription());
            } else {
                viewHolder.tvDescription.setText(tag.getName());
            }
        } else {
            viewHolder.tvDescription.setVisibility(View.GONE);
        }
        CashServiceInfo info = serviceInfo.get(bean.getDeviceId());
        viewHolder.tvName.setText(info == null ? "--" : info.getDeviceName());
        Glide.with(context).load(bean.getSnapshotUrl()).transform(transform).into(viewHolder.ivPreview);
        if (isAbnormalBehavior) {
            viewHolder.tvTime.setText(DateTimeUtils.secondToDate(bean.getStartTime(), "HH:mm:ss"));
            if (!TextUtils.isEmpty(tag.getTip())) {
                viewHolder.tvSuggest.setText(tag.getTip());
            } else {
                viewHolder.tvSuggest.setText(R.string.tip_other_exception);
            }
        } else {
            viewHolder.tvTime.setText(DateTimeUtils.secondToDate(bean.getPurchaseTime(), "HH:mm:ss"));
            viewHolder.tvAmount.setText(String.format("¥%s", numberFormat.format(bean.getAmount())));
            viewHolder.tvOrderNum.setText(bean.getOrderNo());
        }
        if (data.size() > 1) {
            if (i == data.size() - 1) {
                viewHolder.tvLineTop.setVisibility(View.VISIBLE);
                viewHolder.tvLineBottom.setVisibility(View.INVISIBLE);
            } else if (i == 0) {
                viewHolder.tvLineTop.setVisibility(View.INVISIBLE);
                viewHolder.tvLineBottom.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvLineTop.setVisibility(View.VISIBLE);
                viewHolder.tvLineBottom.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tvLineTop.setVisibility(View.INVISIBLE);
            viewHolder.tvLineBottom.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ArrayList<CashVideo> data, int pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTime;
        TextView tvDescription;
        TextView tvAmount;
        TextView tvOrderNum;
        ImageView ivPreview;
        TextView tvName;
        TextView tvLineTop;
        TextView tvLineBottom;
        TextView tvSuggest;
        Group groupContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDescription = itemView.findViewById(R.id.tv_exception_des);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvOrderNum = itemView.findViewById(R.id.tv_order_num);
            tvName = itemView.findViewById(R.id.tv_pos);
            ivPreview = itemView.findViewById(R.id.iv_preview_img);
            tvLineTop = itemView.findViewById(R.id.tv_left_top_line);
            tvLineBottom = itemView.findViewById(R.id.tv_left_bottom_line);
            tvSuggest = itemView.findViewById(R.id.tv_suggest);
            groupContent = itemView.findViewById(R.id.group_content);
            if (isAbnormalBehavior) {
                groupContent.setVisibility(View.GONE);
                tvSuggest.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(data, getAdapterPosition());
            }
        }
    }
}
