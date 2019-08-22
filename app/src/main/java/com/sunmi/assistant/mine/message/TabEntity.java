package com.sunmi.assistant.mine.message;

import sunmi.common.view.tablayout.listener.CustomTabEntity;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-15.
 */
public class TabEntity implements CustomTabEntity {

    public String title;

    public TabEntity(String title) {
        this.title = title;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return 0;
    }

    @Override
    public int getTabUnselectedIcon() {
        return 0;
    }
}
