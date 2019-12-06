package com.sunmi.ipc.contract;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-06.
 */
public interface CashVideoListConstract {

    interface View extends BaseView {
        void getCashVideoSuccess();

        void netWorkError();
    }

    interface Presenter {
        void load(int deviceId, int videoType, long startTime, long endTime);

        void loadMore();
    }
}

