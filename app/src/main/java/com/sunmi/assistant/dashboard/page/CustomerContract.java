package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.dashboard.PageContract;
import com.sunmi.assistant.dashboard.card.BaseRefreshCard;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public interface CustomerContract {

    interface View extends PageContract.PageView {
        void removeFrequencyCard();

        void addFrequencyCard(List<BaseRefreshCard> data);
    }

    interface Presenter extends PageContract.PagePresenter {
    }
}
