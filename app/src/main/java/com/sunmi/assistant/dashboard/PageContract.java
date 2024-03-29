package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.card.BaseRefreshCard;
import com.sunmi.assistant.dashboard.data.DashboardCondition;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.Interval;

/**
 * @author yinhui
 * @date 2019-10-21
 */
public interface PageContract {

    interface ParentPresenter {

        DashboardCondition onChildCreate(PagePresenter presenter);

        void refresh(boolean reloadCondition, boolean clearCache, boolean onlyCurrentPage, boolean showLoading);

    }

    interface PageView extends BaseView {

        Context getContext();

        int getPerspective();

        void updateTab(int period);

        void setCards(List<BaseRefreshCard> data);

        void scrollToTop(boolean animated);
    }

    interface PagePresenter {

        void init();

        int getType();

        void refresh(boolean force, boolean showLoading);

        void setCondition(DashboardCondition condition);

        void setPeriod(int period, Interval periodTime);

        void scrollToTop(boolean animated);

        int getPeriod();

        Interval getPeriodTime();

        void release();
    }
}
