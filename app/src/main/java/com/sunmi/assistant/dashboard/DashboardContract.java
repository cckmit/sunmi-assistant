package com.sunmi.assistant.dashboard;

import android.content.Context;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    interface View extends BaseView {

        int getHeaderHeight();

        void setShopList(List<ShopItem> list);

        void setSource(int source);

        void updateTab(int page, int period);

        void updateTopPosition(int position);

        void resetTop();

        void loadDataFailed();

    }

    interface Presenter {

        void init(Context context);

        void reload();

        void setShop(ShopItem shop);

        void setPeriod(int period);

        void setPage(int index);

        List<PageHost> getPages();

        int getPeriod();

    }

    interface PagePresenter {

        int getIndex();

        void setSource(int source);

        void setPeriod(int period);

        int getPeriod();

        void refresh(boolean showLoading);

        void refresh(int position, boolean showLoading);

        void showFailedTip();

        void release();
    }
}
