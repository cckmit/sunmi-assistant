package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.CashOrderResp;

import sunmi.common.base.BaseView;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public interface CashVideoContract {
    interface View extends BaseView {

        void updateTagSuccess(int videoType);

        void updateTagFail(int code, String msg, int videoType);

        void getOrderInfoSuccess(CashOrderResp data);

        void getOrderInfoFail(int code, String msg);
    }

    interface Presenter {
        void updateTag(int auditVideoId, String description, int videoType);

        void getOrderInfo(String orderNo);
    }
}
