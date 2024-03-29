package com.sunmi.assistant.dashboard;

import android.support.v4.app.Fragment;

/**
 * @author yinhui
 * @date 2019-10-12
 */
public class PageHost {

    private int title;
    private int icon;
    private Fragment fragment;
    private int type;

    public PageHost(int title, int icon, Fragment fragment, int type) {
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
        this.type = type;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getType() {
        return type;
    }
}
