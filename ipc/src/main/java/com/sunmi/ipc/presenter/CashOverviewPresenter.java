package com.sunmi.ipc.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;

import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CashOverviewContract;
import com.sunmi.ipc.model.CashVideoCountResp;
import com.sunmi.ipc.model.CashVideoListBean;
import com.sunmi.ipc.model.CashVideoTimeSlotBean;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public class CashOverviewPresenter extends BasePresenter<CashOverviewContract.View> implements CashOverviewContract.Presenter {

    private HashMap<Integer, CashServiceInfo> beanMap;

    @SuppressLint("UseSparseArrays")
    public CashOverviewPresenter(List<CashServiceInfo> serviceBeans) {
        beanMap = new HashMap<>(serviceBeans.size());
        for (CashServiceInfo bean : serviceBeans) {
            beanMap.put(bean.getDeviceId(), bean);
        }
    }

    @Override
    public void getCashVideoTimeSlots(int deviceId, long startTime, long endTime) {
        IpcCloudApi.getInstance().getCashVideoTimeSlots(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId, startTime, endTime, new RetrofitCallback<CashVideoTimeSlotBean>() {
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
        IpcCloudApi.getInstance().getShopCashVideoCount(SpUtils.getCompanyId(), SpUtils.getShopId(), startTime, endTime, new RetrofitCallback<CashVideoListBean>() {
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
        IpcCloudApi.getInstance().getIpcCashVideoCount(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId, startTime, endTime, new RetrofitCallback<CashVideoCountResp>() {
            @Override
            public void onSuccess(int code, String msg, CashVideoCountResp data) {
                ThreadPool.getCachedThreadPool().submit(() -> {
                    List<CashVideoListBean> list = data.getStatInfoList();
                    if (list != null) {
                        for (CashVideoListBean bean : list) {
                            CashServiceInfo info = beanMap.get(bean.getDeviceId());
                            if (info != null) {
                                info.setTotalCount(bean.getTotalCount());
                                info.setAbnormalVideoCount(bean.getAbnormalVideoCount());
                                info.setAbnormalBehaviorCount(bean.getAbnormalBehaviorVideoCount());
                            }
                        }
                    }
                    Collection<CashServiceInfo> collection = beanMap.values();
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

    @Override
    public void onServiceSubscribeResult(Intent intent) {
        String args = intent.getStringExtra("args");
        if (!TextUtils.isEmpty(args)) {
            try {
                JSONObject jsonObject = new JSONObject(args);
                int code = jsonObject.getInt("code");
                JSONObject data = jsonObject.getJSONObject("data");
                Set<String> snSet = new HashSet<>();
                if (code == 100) {
                    JSONArray list = data.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject serviceObject = list.optJSONObject(i);
                        int service = serviceObject.getInt("service");
                        int status = serviceObject.getInt("status");
                        if (service == IpcConstants.SERVICE_TYPE_CASH_PREVENT
                                && status == CommonConstants.RESULT_OK) {
                            snSet.add(serviceObject.getString("sn"));
                        }
                    }
                    BaseNotification.newInstance().postNotificationName(CommonNotifications.cashPreventSubscribe, snSet);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, CashServiceInfo> getCashServiceMap() {
        return beanMap;
    }

}
