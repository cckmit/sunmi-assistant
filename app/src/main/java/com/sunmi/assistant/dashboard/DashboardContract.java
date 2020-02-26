package com.sunmi.assistant.dashboard;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

public interface DashboardContract {

    interface View extends BaseView {

        PageContract.ParentPresenter getPresenter();

        int getHeaderHeight();

        void setShopList(List<FilterItem> list);

        void setPages(List<PageHost> pages);

        void setSource(int source);

        void updateTab(int page, int period);

        void updateTopPosition(int position);

        void resetTop();

        void loadDataFailed();

        void updateFloating(boolean showFloating);

    }

    interface Presenter extends PageContract.ParentPresenter {

        void init();

        void reload(int flag);

        void switchToTotalPerspective();

        void switchToShopPerspective();

        void switchShop(FilterItem shop);

        void switchPeriod(int period);

        void switchPage(int type);

        void scrollToTop();

        int getPageType();

        int getPeriod();

        void startAutoRefresh();

        void stopAutoRefresh();

    }

}
