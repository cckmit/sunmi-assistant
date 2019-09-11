package com.sunmi.assistant.dashboard;

import android.content.Context;

import com.sunmi.assistant.dashboard.card.BaseRefreshItem;

import java.util.List;

import sunmi.common.base.BaseView;

public interface DashboardContract {

    interface View extends BaseView {

        Context getContext();

        void initData(List<BaseRefreshItem> data);

        void updateTab(int period);
    }

    interface Presenter {

        void init();

        void switchPeriodTo(int period);

        void switchCompanyTo(int companyId, int shopId);

        void switchShopTo(int shopId);

        void refresh();

        void refresh(int position);

        void showFailedTip();

    }
}
