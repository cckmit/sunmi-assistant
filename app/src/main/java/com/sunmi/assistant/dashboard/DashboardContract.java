package com.sunmi.assistant.dashboard;

import com.sunmi.assistant.dashboard.data.DashboardCondition;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;
import sunmi.common.model.Interval;

public interface DashboardContract {

    interface View extends BaseView {

        PageContract.ParentPresenter getPresenter();

        int getHeaderHeight();

        void setShopList(List<FilterItem> list);

        void switchPerspective(int perspective);

        void setPages(List<PageHost> pages, int perspective);

        void setCondition(DashboardCondition condition);

        void updateTab(int page, int period);

        void updateTopPosition(int position);

        void resetTop();

        void loadDataFailed();

        void updateFloating(boolean showFloating);

    }

    interface Presenter extends PageContract.ParentPresenter {

        void init();

        void load(int flag, boolean clearCache, boolean onlyCurrentPage, boolean showLoading);

        void switchPerspective(int perspective, boolean refresh);

        void switchPeriod(int period, Interval periodTime);

        void switchPage(int type);

        void scrollToTop();

        void closeFloatingAd();

        int getPageType();

        int getPeriod();

        void startAutoRefresh();

        void stopAutoRefresh();

    }

}
