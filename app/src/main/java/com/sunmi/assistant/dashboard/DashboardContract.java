package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.newcard.BaseRefreshCard;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    interface View extends BaseView {

        void updateTab(int period);

        void setShopList(List<ShopItem> list);

        void setCards(List<BaseRefreshCard> data, int dataSource);

        void loadDataFailed();

    }

    interface Presenter {

        Context getContext();

        void init(Context context);

        void reload();

        boolean switchShopTo(ShopItem shop);

        void switchPeriodTo(int period);

        void refresh(boolean showLoading);

        void refresh(int position, boolean showLoading);

        void showFailedTip();

    }
}
