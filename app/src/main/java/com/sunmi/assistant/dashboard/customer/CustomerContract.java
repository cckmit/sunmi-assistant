package com.sunmi.assistant.dashboard.customer;

import com.sunmi.assistant.dashboard.BaseRefreshCard;
import com.sunmi.assistant.dashboard.PageContract;

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
