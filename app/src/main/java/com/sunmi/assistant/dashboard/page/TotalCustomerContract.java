package com.sunmi.assistant.dashboard.page;

import com.sunmi.assistant.dashboard.PageContract;

/**
 * @author yinhui
 * @date 2019-10-14
 */
public interface TotalCustomerContract {

    interface View extends PageContract.PageView {
        void showTimeDialog(int period, long periodTime);
    }

    interface Presenter extends PageContract.PagePresenter {
    }
}
