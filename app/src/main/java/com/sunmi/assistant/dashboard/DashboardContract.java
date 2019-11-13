package com.sunmi.assistant.dashboard;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

public interface DashboardContract {

    interface View extends BaseView {

        PageContract.ParentPresenter getPresenter();

        int getHeaderHeight();

        void setShopList(List<FilterItem> list);

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

        void setShop(FilterItem shop);

        void setPeriod(int period);

        void setPage(int type);

        void scrollToTop();

        List<PageHost> createPages();

        int getPageType();

        int getPeriod();

    }

}
