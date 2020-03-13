package com.sunmi.assistant.ui.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;

import java.util.List;

import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.DeviceTypeUtils;

/**
 * Description: 设备列表adapter
 * Created by bruce on 2019/6/28.
 */
public class DeviceListAdapter extends BaseQuickAdapter<SunmiDevice, BaseViewHolder> {

    private OnDeviceClickListener clickListener;

    public DeviceListAdapter(Context context, List<SunmiDevice> list) {
        super(R.layout.item_sunmi_device, list);
    }

    public void setClickListener(OnDeviceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void convert(BaseViewHolder holder, SunmiDevice item) {
        String model;
        if ("POS".equals(item.getType()) && !TextUtils.isEmpty(item.getDisplayModel())) {
            model = item.getDisplayModel();
        } else {
            model = item.getModel();
        }
        holder.setText(R.id.tv_name, model);
        if (TextUtils.isEmpty(item.getName())) {
            holder.setText(R.id.tv_sn, item.getDeviceid());
        } else {
            holder.setText(R.id.tv_sn, item.getName());
        }
        holder.setImageResource(R.id.iv_device, DeviceTypeUtils.getInstance().getSunmiDeviceImage(item.getModel()));
        showStatus(holder, item);
        holder.getView(R.id.rl_device_item).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDeviceClick(item);
            }
        });

        holder.getView(R.id.ll_more).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onMoreClick(item, holder.getAdapterPosition());
            }
        });
    }

    private void showStatus(BaseViewHolder holder, SunmiDevice item) {
        holder.setText(R.id.tv_status, DeviceStatus.valueOf(item.getStatus()).getValue());
        if (item.getStatus() == DeviceStatus.ONLINE.ordinal()) {
            holder.setImageResource(R.id.iv_status, R.drawable.ic_device_status_normal);
        } else if (item.getStatus() == DeviceStatus.OFFLINE.ordinal()
                || item.getStatus() == DeviceStatus.EXCEPTION.ordinal()) {
            holder.setImageResource(R.id.iv_status, R.drawable.ic_device_status_offline);
        } else {
            holder.setImageDrawable(R.id.iv_status, null);
        }
    }

    public interface OnDeviceClickListener {

        void onDeviceClick(SunmiDevice item);

        void onMoreClick(SunmiDevice item, int position);

    }

}