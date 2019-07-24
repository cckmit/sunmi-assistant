package com.sunmi.assistant.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sunmi.assistant.R;

import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/6/28.
 */
public class DeviceSettingDialog extends PopupWindow {

    private Context context;
    private OnSettingsClickListener onSettingsClickListener;
    private SunmiDevice device;

    public DeviceSettingDialog(Context context, SunmiDevice device) {
        super(context);
        this.context = context;
        this.device = device;
        init();
    }

    public void setOnSettingsClickListener(OnSettingsClickListener onSettingsClickListener) {
        this.onSettingsClickListener = onSettingsClickListener;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewLayout = inflater.inflate(R.layout.layout_device_setting, null);
        setContentView(viewLayout);

        viewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setTouchable(true); // 设置popupwindow可点击
        setOutsideTouchable(true); // 设置popupwindow外部可点击
        setFocusable(false); // 获取焦点

        viewLayout.findViewById(R.id.tv_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onSettingsClickListener != null) {
                    onSettingsClickListener.onSettingsClick(device, 0);
                }
            }
        });
        viewLayout.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onSettingsClickListener != null) {
                    onSettingsClickListener.onSettingsClick(device, 1);
                }
            }
        });
        View divider1 = viewLayout.findViewById(R.id.divider1);
        TextView tvSetting = viewLayout.findViewById(R.id.tv_setting);
        if ("IPC".equalsIgnoreCase(device.getType())) {
            divider1.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
            tvSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onSettingsClickListener != null) {
                        onSettingsClickListener.onSettingsClick(device, 2);
                    }
                }
            });
        } else {
            divider1.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
        }
    }

    public void show(View parent) {
        int offsetX = parent.getWidth() - getContentView().getMeasuredWidth();
        showAsDropDown(parent, offsetX, 0, Gravity.START);
    }

    public interface OnSettingsClickListener {
        void onSettingsClick(SunmiDevice device, int type);
    }

}
