package com.sunmi.assistant.dashboard.overview;

import android.content.Context;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.util.List;

import sunmi.common.base.BaseView;

public interface OverviewContract {

    interface View extends BaseView {

        void updateTab(int period);

        void setCards(List<BaseRefreshCard> data);

    }

    interface Presenter extends DashboardContract.PagePresenter {

        void init(Context context);

        void load();

    }
}
