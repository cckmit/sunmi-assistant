package com.sunmi.assistant.ui.adapter;

import android.content.Context;
import android.view.View;

import com.sunmi.apmanager.constant.enums.DeviceStatus;
import com.sunmi.assistant.R;

import java.util.List;

import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.SunmiDevUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * Description:
 * Created by bruce on 2019/6/28.
 */
public class DeviceListAdapter extends CommonListAdapter<SunmiDevice> {

    private OnDeviceClickListener clickListener;

    public DeviceListAdapter(Context context, List<SunmiDevice> list) {
        super(context, R.layout.item_sunmi_device, list);
    }

    public void setClickListener(OnDeviceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void convert(ViewHolder holder, SunmiDevice item) {
        holder.setText(R.id.tv_name, item.getModel());
        holder.setText(R.id.tv_sn, item.getDeviceid());
        holder.setImageResource(R.id.iv_device, SunmiDevUtils.setSearchLogo(item.getModel()));
        showStatus(holder, item);
        holder.getView(R.id.rl_device_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onDeviceClick(item);
                }
            }
        });

        holder.getView(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onMoreClick(item, holder.getAdapterPosition());
                }
            }
        });
    }

    private void showStatus(ViewHolder holder, SunmiDevice item) {
        holder.setText(R.id.tv_status, DeviceStatus.valueOf(item.getStatus()).getValue());
        if (item.getStatus() == DeviceStatus.ONLINE.ordinal()) {
            holder.setImageResource(R.id.iv_status, R.drawable.ic_device_status_normal);
        } else if (item.getStatus() == DeviceStatus.OFFLINE.ordinal()
                || item.getStatus() == DeviceStatus.EXCEPTION.ordinal()) {
            holder.setImageResource(R.id.iv_status, R.drawable.ic_device_status_offline);
        } else {
            holder.setImageResource(R.id.iv_status, -1);
        }
    }

    public interface OnDeviceClickListener {

        void onDeviceClick(SunmiDevice item);

        void onMoreClick(SunmiDevice item, int position);

    }

}