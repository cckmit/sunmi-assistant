package com.sunmi.assistant.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.sunmi.assistant.R;

import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/6/28.
 */
public class DeviceSettingDialog extends PopupWindow {

    private Context context;
    private int width, height;
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
        //get viewLayout
        viewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        width = viewLayout.getWidth();
        height = viewLayout.getHeight();
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
    }

    public void show(View parent) {
        //set location up or down ,left right
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        showAtLocation(parent, Gravity.NO_GRAVITY,
                (location[0] + parent.getWidth() / 2) - width / 2, location[1] - height);
    }

    public interface OnSettingsClickListener {
        void onSettingsClick(SunmiDevice device, int type);
    }

}
