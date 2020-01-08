package com.sunmi.presenter;

import android.util.SparseArray;

import com.sunmi.contract.SupportContract;
import com.xiaojinzi.component.impl.service.ServiceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.ServiceResp;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-30.
 */
public class SupportPresenter extends BasePresenter<SupportContract.View> implements SupportContract.Presenter {

    private IpcCloudApiAnno ipcCloudApi;
    private SparseArray<CashServiceInfo> cashServiceInfoList = new SparseArray<>();

    public SupportPresenter() {
        ipcCloudApi = ServiceManager.get(IpcCloudApiAnno.class);
    }

    @Override
    public void load() {
        // 清空获取到的服务信息
        cashServiceInfoList.clear();
        if (ipcCloudApi == null) {
            if (isViewAttached()) {
                mView.loadFailed();
            }
        }
        // 依次获取收银服务状态，防损服务状态和云存储服务状态
        getCashVideoService();
    }

    private void getCashVideoService() {
        ipcCloudApi.getAuditVideoServiceList(null, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                // 响应校验
                if (data == null || data.getList() == null) {
                    if (isViewAttached()) {
                        mView.loadSuccess(Collections.emptyList());
                    }
                    return;
                }
                // 遍历响应列表，创建设备服务信息
                List<ServiceResp.Info> list = data.getList();
                for (ServiceResp.Info bean : list) {
                    if (bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                        cashServiceInfoList.put(bean.getDeviceId(), new CashServiceInfo(bean));
                    }
                }
                if (cashServiceInfoList.size() > 0) {
                    getCashLossPreventionService();
                } else {
                    if (isViewAttached()) {
                        mView.loadSuccess(Collections.emptyList());
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.loadFailed();
                }
            }
        });
    }

    private void getCashLossPreventionService() {
        int size = cashServiceInfoList.size();
        if (size <= 0) {
            if (isViewAttached()) {
                mView.loadSuccess(Collections.emptyList());
            }
            return;
        }
        List<String> snList = getDeviceSnList();
        ipcCloudApi.getAuditSecurityPolicyList(snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                // 响应校验，如果列表为空则说明没有设备开通收银防损，直接下一步获取云存储开通状态
                if (data == null || data.getList() == null) {
                    getCloudStorageService();
                    return;
                }
                // 遍历响应列表，更新集合中的设备服务信息
                List<ServiceResp.Info> list = data.getList();
                for (ServiceResp.Info bean : list) {
                    CashServiceInfo info = cashServiceInfoList.get(bean.getDeviceId());
                    if (info != null) {
                        info.setHasCashLossPrevention(bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED);
                    }
                }
                getCloudStorageService();
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.loadFailed();
                }
            }
        });
    }

    private void getCloudStorageService() {
        int size = cashServiceInfoList.size();
        if (size <= 0) {
            if (isViewAttached()) {
                mView.loadSuccess(Collections.emptyList());
            }
            return;
        }
        List<String> snList = getDeviceSnList();
        ipcCloudApi.getStorageList(snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                if (data == null || data.getList() == null) {
                    // 说明没有设备开通了云存储
                    if (isViewAttached()) {
                        mView.loadSuccess(getServiceList());
                    }
                    return;
                }
                // 遍历响应列表，更新集合中的设备服务信息
                List<ServiceResp.Info> list = data.getList();
                for (ServiceResp.Info bean : list) {
                    CashServiceInfo info = cashServiceInfoList.get(bean.getDeviceId());
                    if (info != null) {
                        info.setHasCloudStorage(bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED);
                    }
                }
                if (isViewAttached()) {
                    mView.loadSuccess(getServiceList());
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.loadFailed();
                }
            }
        });
    }

    private List<String> getDeviceSnList() {
        int size = cashServiceInfoList.size();
        List<String> snList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            snList.add(cashServiceInfoList.valueAt(i).getDeviceSn());
        }
        return snList;
    }

    private List<CashServiceInfo> getServiceList() {
        List<CashServiceInfo> result = new ArrayList<>();
        int size = cashServiceInfoList.size();
        if (size <= 0) {
            return result;
        }
        for (int i = 0; i < size; i++) {
            result.add(cashServiceInfoList.valueAt(i));
        }
        return result;
    }

}
