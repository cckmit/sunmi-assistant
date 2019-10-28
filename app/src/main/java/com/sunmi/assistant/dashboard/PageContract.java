package com.sunmi.assistant.dashboard;

import android.content.Context;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-10-21
 */
public interface PageContract {

    interface ParentPresenter {

        void refresh(boolean forceReload, boolean showLoading);

    }

    interface PageView extends BaseView {

        Context getContext();

        void updateTab(int period);

        void setCards(List<BaseRefreshCard> data);

        void scrollToTop();
    }

    interface PagePresenter {

        void load();

        void setSource(int source, boolean showLoading);

        void setPeriod(int period);

        void scrollToTop();

        void refresh(boolean showLoading);

        int getIndex();

        int getPeriod();

        void release();
    }
}
