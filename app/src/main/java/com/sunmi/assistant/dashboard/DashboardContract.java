package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.card.BaseRefreshItem;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    interface View extends BaseView {

        Context getContext();

        void updateTab(int period);

        void setShopList(List<ShopItem> list);

        void setCards(List<BaseRefreshItem> data, int dataSource);

        void loadDataFailed();

    }

    interface Presenter {

        void init();

        void reload();

        boolean switchShopTo(ShopItem shop);

        void switchPeriodTo(int period);

        void refresh(boolean showLoading);

        void refresh(int position, boolean showLoading);

        void showFailedTip();

    }
}
