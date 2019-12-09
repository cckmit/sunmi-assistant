package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoResp;

import java.util.List;
import java.util.Map;

import sunmi.common.base.BaseView;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public interface CashVideoContract {
    interface View extends BaseView {

        void updateTagSuccess(int videoType, String description);

        void updateTagFail(int code, String msg, int videoType);

        void getOrderInfoSuccess(CashOrderResp data);

        void getOrderInfoFail(int code, String msg);


        void cashVideoListSuccess(List<CashVideoResp.AuditVideoListBean> videoList);

        void cashVideoListFail(int code, String msg);
    }

    interface Presenter {
        void updateTag(int auditVideoId, String description, int videoType);

        void getOrderInfo(String orderNo);

        void getCashVideoList(Map<Integer, String> ipcName, int deviceId, int videoType,
                              long startTime, long endTime, int pageNum, int pageSize);
    }
}
