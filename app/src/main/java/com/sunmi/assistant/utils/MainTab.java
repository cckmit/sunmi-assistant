package com.sunmi.assistant.utils;

import com.sunmi.apmanager.ui.fragment.StoreFragment;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardFragment_;
import com.sunmi.assistant.ui.activity.MainActivity;
import com.sunmi.assistant.ui.fragment.MineFragment_;
import com.sunmi.sunmiservice.SupportFragment_;

public enum MainTab {

    DATA(
            MainActivity.TAB_DATA,
            R.string.ic_tab_data_title,
            R.drawable.ic_tab_data,
            DashboardFragment_.class
    ),

    STORE(
            MainActivity.TAB_STORE,
            R.string.str_store,
            R.drawable.tab_store,
            StoreFragment.class
    ),

    SUPPORT(
            MainActivity.TAB_SUPPORT,
            R.string.str_support,
            R.drawable.tab_support,
            SupportFragment_.class
    ),

    MINE(
            MainActivity.TAB_MINE,
            R.string.str_mine,
            R.drawable.tab_mine,
            MineFragment_.class
    );

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    MainTab(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
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
