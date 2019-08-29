package com.sunmi.assistant.utils;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardFragment_;
import com.sunmi.assistant.mine.MineFragment_;
import com.sunmi.assistant.ui.fragment.DeviceFragment_;
import com.sunmi.sunmiservice.SupportFragment_;

public enum MainTab {

    DATA(
            R.string.str_tab_dashboard,
            R.drawable.ic_tab_data,
            DashboardFragment_.class
    ),

    STORE(
            R.string.str_tab_device,
            R.drawable.ic_tab_store,
            DeviceFragment_.class
    ),

    SUPPORT(
            R.string.str_tab_support,
            R.drawable.ic_tab_support,
            SupportFragment_.class
    ),

    MINE(
            R.string.str_tab_mine,
            R.drawable.ic_tab_mine,
            MineFragment_.class
    );

    private int resName;
    private int resIcon;
    private Class<?> clz;

    MainTab(int resName, int resIcon, Class<?> clz) {
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

}
