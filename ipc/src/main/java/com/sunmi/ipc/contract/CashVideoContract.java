package com.sunmi.ipc.contract;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.sunmi.ipc.cash.model.CashBox;
import com.sunmi.ipc.cash.model.CashTagFilter;
import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.model.CashOrderResp;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.ServiceResp;

/**
 * @author yangShiJie
 * @date 2019-12-05
 */
public interface CashVideoContract {
    interface View extends BaseView {

        void updateTagSuccess(CashTagFilter tag);

        void updateTagFail(int code, String msg, CashTagFilter tag);

        void getOrderInfoSuccess(CashOrderResp data);

        void getOrderInfoFail(int code, String msg);

        void cashVideoListSuccess(List<CashVideo> videoList);

        void cashVideoListFail(int code, String msg);

        void getStorageSuccess(ServiceResp.Info data);

        void getAbnormalEventSuccess(float riskScore, List<CashBox> boxes);

        void getAbnormalEventFail(int code, String msg);
    }

    interface Presenter {

        void updateTag(long videoId, int source, CashTagFilter selected);

        void getOrderInfo(String orderNo);

        void getCashVideoList(int deviceId, int videoType,
                              long startTime, long endTime, int pageNum, int pageSize);

        void getAbnormalBehaviorList(int deviceId, int videoType,
                                     long startTime, long endTime, int pageNum, int pageSize);

        void getStorageList(String deviceSn);

        void getAbnormalEvent(long eventId, long beginTime);

        void onServiceSubscribeResult(@NonNull Intent intent);
    }
}
