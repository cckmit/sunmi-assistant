package com.sunmi.assistant.dashboard;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

public interface DashboardContract {

    interface View extends BaseView {

        int getHeaderHeight();

        void setShopList(List<FilterItem> list);

        void setSource(int source);

        void updateTab(int page, int period);

        void updateTopPosition(int position);

        void resetTop();

        void loadDataFailed();

    }

    interface Presenter extends PageContract.ParentPresenter {

        void init();

        void reload(int flag);

        void setShop(FilterItem shop);

        void setPeriod(int period);

        void setPage(int index);

        void scrollToTop();

        List<PageHost> getPages();

        int getPageIndex();

        int getPeriod();

    }
//
//    interface PagePresenter {
//
//        int getType();
//
//        int getScrollY();
//
//        void scrollTo(int y);
//
//        void refresh(boolean showLoading);
//
//        void refresh(int position, boolean showLoading);
//
//
//
//    }
}
