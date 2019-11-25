package com.sunmi.assistant.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sunmi.assistant.R;

import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;

/**
 * Description:
 * Created by bruce on 2019/6/28.
 */
public class DeviceSettingMenu extends PopupWindow {

    private LinearLayout llRoot;
    private Context context;
    private OnSettingsClickListener onSettingsClickListener;
    private SunmiDevice device;

    public DeviceSettingMenu(Context context, SunmiDevice device) {
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
        llRoot = viewLayout.findViewById(R.id.pop_view);
        setContentView(viewLayout);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);//5.0必须调用setWidth,setHeight
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        viewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setTouchable(true); // 设置popupwindow可点击
        setOutsideTouchable(true); // 设置popupwindow外部可点击
        setFocusable(false); // 获取焦点
        setClippingEnabled(false);

        viewLayout.findViewById(R.id.tv_detail).setOnClickListener(v -> {
            dismiss();
            if (onSettingsClickListener != null) {
                onSettingsClickListener.onSettingsClick(device, 0);
            }
        });
        TextView tvDelete = viewLayout.findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(v -> {
            dismiss();
            if (onSettingsClickListener != null) {
                onSettingsClickListener.onSettingsClick(device, 1);
            }
        });
        View divider1 = viewLayout.findViewById(R.id.divider1);
        TextView tvSetting = viewLayout.findViewById(R.id.tv_setting);
        if ("IPC".equalsIgnoreCase(device.getType())) {
            divider1.setVisibility(View.VISIBLE);
            tvSetting.setVisibility(View.VISIBLE);
            tvSetting.setOnClickListener(v -> {
                dismiss();
                if (onSettingsClickListener != null) {
                    onSettingsClickListener.onSettingsClick(device, 2);
                }
            });
        } else if ("POS".equalsIgnoreCase(device.getType())) {
            divider1.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
        } else {
            divider1.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
        }
    }

    public void show(View parent) {
        int width = llRoot.getMeasuredWidth();
        int height = llRoot.getMeasuredHeight();
        if (!"IPC".equalsIgnoreCase(device.getType())) {
            height = height - CommonHelper.dp2px(context, 48);
        }
        int[] outLocation = new int[2];//锚点view的位置
        parent.getLocationInWindow(outLocation);
        if (CommonHelper.getScreenHeight(context) - parent.getHeight() - outLocation[1] < height) {//上方
            llRoot.setBackgroundResource(R.drawable.bg_device_setting_up);
            showAtLocation(parent, Gravity.TOP, CommonHelper.getScreenWidth(context) / 2 - width
                    + CommonHelper.dp2px(context, 30), outLocation[1] - height);
        } else {
            llRoot.setBackgroundResource(R.drawable.bg_device_setting_down);
            showAsDropDown(parent, parent.getWidth() - getContentView().getMeasuredWidth(),
                    0, Gravity.START);
        }
    }

    public interface OnSettingsClickListener {
        void onSettingsClick(SunmiDevice device, int type);
    }

}
