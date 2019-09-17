package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.card.BaseRefreshItem;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

public interface DashboardContract {

    interface View extends BaseView {

        Context getContext();

        void updateTab(int period);

        void setShopList(List<FilterItem> list);

        void setCards(List<BaseRefreshItem> data);

    }

    interface Presenter {

        void init();

        void switchCompanyTo(int companyId, int shopId);

        void switchShopTo(int shopId);

        void switchPeriodTo(int period);

        void refresh();

        void refresh(int position);

        void showFailedTip();

    }
}
