package com.sunmi.ipc.presenter;

import android.annotation.SuppressLint;

import com.sunmi.ipc.contract.CashOverviewContract;
import com.sunmi.ipc.model.CashVideoCountResp;
import com.sunmi.ipc.model.CashVideoListBean;
import com.sunmi.ipc.model.CashVideoTimeSlotBean;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public class CashOverviewPresenter extends BasePresenter<CashOverviewContract.View> implements CashOverviewContract.Presenter {

    private Map<Integer, CashVideoServiceBean> beanMap;

    @SuppressLint("UseSparseArrays")
    public CashOverviewPresenter(List<CashVideoServiceBean> serviceBeans) {
        beanMap = new HashMap<>(serviceBeans.size());
        for (CashVideoServiceBean bean : serviceBeans) {
            beanMap.put(bean.getDeviceId(), bean);
        }
    }

    @Override
    public void getCashVideoTimeSlots(int deviceId, long startTime, long endTime) {
        IpcCloudApi.getInstance().getCashVideoTimeSlots(deviceId, startTime, endTime, new RetrofitCallback<CashVideoTimeSlotBean>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoTimeSlotBean data) {
                if (isViewAttached()) {
                    mView.getCashVideoTimeSlotsSuccess(data.getTimeslots());
                }
            }

            @Override
            public void onFail(int code, String msg, CashVideoTimeSlotBean data) {
            }
        });

    }

    @Override
    public void getShopCashVideoCount(long startTime, long endTime) {
        IpcCloudApi.getInstance().getShopCashVideoCount(startTime, endTime, new RetrofitCallback<CashVideoListBean>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoListBean data) {
                if (isViewAttached()) {
                    mView.getShopCashVideoCountSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, CashVideoListBean data) {
                if (isViewAttached()) {
                    mView.netWorkError();
                }
            }
        });
    }

    @Override
    public void getIpcCashVideoCount(List<Integer> deviceId, long startTime, long endTime) {
        IpcCloudApi.getInstance().getIpcCashVideoCount(deviceId, startTime, endTime, new RetrofitCallback<CashVideoCountResp>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoCountResp data) {
                ThreadPool.getCachedThreadPool().submit(() -> {
                    List<CashVideoListBean> list = data.getStatInfoList();
                    if (list != null) {
                        for (CashVideoListBean bean : list) {
                            CashVideoServiceBean info = beanMap.get(bean.getDeviceId());
                            if (info != null) {
                                info.setTotalCount(bean.getTotalCount());
                                info.setAbnormalVideoCount(bean.getAbnormalVideoCount());
                                info.setAbnormalBehaviorCount(bean.getAbnormalBehaviorVideoCount());
                            }
                        }
                    }
                    Collection<CashVideoServiceBean> collection = beanMap.values();
                    if (isViewAttached()) {
                        mView.getIpcCashVideoCountSuccess(new ArrayList<>(collection));
                    }
                });
            }

            @Override
            public void onFail(int code, String msg, CashVideoCountResp data) {
                if (isViewAttached()) {
                    mView.netWorkError();
                }
            }
        });
    }
}
