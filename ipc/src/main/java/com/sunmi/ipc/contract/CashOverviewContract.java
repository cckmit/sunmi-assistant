package com.sunmi.ipc.contract;

import android.content.Intent;

import com.sunmi.ipc.model.CashVideoListBean;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.CashServiceInfo;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public interface CashOverviewContract {

    interface View extends BaseView {

        void getCashVideoTimeSlotsSuccess(List<Long> timeSlots);

        void getShopCashVideoCountSuccess(CashVideoListBean bean);

        void getIpcCashVideoCountSuccess(List<CashServiceInfo> beans);

        void netWorkError();

    }

    interface Presenter {

        void getCashVideoTimeSlots(int deviceId, long startTime, long endTime);

        void getShopCashVideoCount(long startTime, long endTime);

        void getIpcCashVideoCount(List<Integer> deviceId, long startTime, long endTime);

        void onServiceSubscribeResult(Intent intent);
    }
}
