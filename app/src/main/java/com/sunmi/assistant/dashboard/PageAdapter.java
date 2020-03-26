package com.sunmi.assistant.dashboard;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yinhui
 * @date 2020-02-28
 */
public class PageAdapter extends FragmentAdapter {

    private int perspective;
    private List<PageHost> data = new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setPages(List<PageHost> pages, int perspective) {
        if (pages == null || pages.isEmpty()) {
            return;
        }
        clearCache();
        this.perspective = perspective;
        data = pages;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return data.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof PageContract.PageView) {
            PageContract.PageView page = (PageContract.PageView) object;
            return page.getPerspective() == this.perspective ? POSITION_UNCHANGED : POSITION_NONE;
        } else {
            return POSITION_NONE;
        }
    }

}
