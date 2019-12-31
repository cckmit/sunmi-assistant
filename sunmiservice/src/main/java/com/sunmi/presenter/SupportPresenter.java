package com.sunmi.presenter;

import com.sunmi.contract.SupportContract;
import com.sunmi.sunmiservice.R;
import com.xiaojinzi.component.impl.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.ServiceListResp;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-30.
 */
public class SupportPresenter extends BasePresenter<SupportContract.View> implements SupportContract.Presenter {

    private IpcCloudApiAnno ipcCloudApi;
    private List<String> snList = new ArrayList<>();

    public SupportPresenter() {
        ipcCloudApi = ServiceManager.get(IpcCloudApiAnno.class);
    }

    @Override
    public void getAuditVideoServiceList() {
        if (ipcCloudApi != null) {
            ipcCloudApi.getAuditVideoServiceList(null, new RetrofitCallback<ServiceListResp>() {
                @Override
                public void onSuccess(int code, String msg, ServiceListResp data) {
                    List<ServiceListResp.DeviceListBean> beans = data.getDeviceList();
                    ArrayList<CashVideoServiceBean> cashVideoServiceBeans = new ArrayList<>();
                    if (beans.size() > 0) {
                        for (ServiceListResp.DeviceListBean bean : beans) {
                            if (bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                                CashVideoServiceBean info = new CashVideoServiceBean();
                                info.setDeviceId(bean.getDeviceId());
                                info.setDeviceSn(bean.getDeviceSn());
                                snList.add(bean.getDeviceSn());
                                info.setDeviceName(bean.getDeviceName());
                                info.setImgUrl(bean.getImgUrl());
                                cashVideoServiceBeans.add(info);
                            }
                        }
                    }
                    if (cashVideoServiceBeans.size() > 0) {
                        getAuditSecurityPolicyService();
                    } else {
                        if (isViewAttached()) {
                            mView.getCashServiceSuccess(cashVideoServiceBeans, false);
                        }
                    }
                }

                @Override
                public void onFail(int code, String msg, ServiceListResp data) {
                    getAuditVideoServiceList();
                    if (isViewAttached()) {
                        mView.getServiceFail();
                    }
                }
            });
        }
    }

    @Override
    public void getStorageList() {
        if (ipcCloudApi != null) {
            ipcCloudApi.getStorageList(snList, new RetrofitCallback<ServiceListResp>() {
                @Override
                public void onSuccess(int code, String msg, ServiceListResp data) {
                    if (isViewAttached()) {
                        mView.getStorageSeviceSuccess(data.getDeviceList().get(0).getStatus());
                    }
                }

                @Override
                public void onFail(int code, String msg, ServiceListResp data) {
                    if (isViewAttached()) {
                        mView.shortTip(R.string.toast_network_Exception);
                    }
                }
            });
        }
    }

    private void getAuditSecurityPolicyService() {
        if (ipcCloudApi != null) {
            ipcCloudApi.getAuditSecurityPolicyList(snList, new RetrofitCallback<ServiceListResp>() {
                @Override
                public void onSuccess(int code, String msg, ServiceListResp data) {
                    List<ServiceListResp.DeviceListBean> beans = data.getDeviceList();
                    ArrayList<CashVideoServiceBean> cashVideoServiceBeans = new ArrayList<>();
                    boolean hasCashLossPrevent = false;
                    if (beans.size() > 0) {
                        for (ServiceListResp.DeviceListBean bean : beans) {
                            CashVideoServiceBean info = new CashVideoServiceBean();
                            info.setDeviceId(bean.getDeviceId());
                            info.setDeviceSn(bean.getDeviceSn());
                            info.setDeviceName(bean.getDeviceName());
                            info.setImgUrl(bean.getImgUrl());
                            info.setHasCashLossPrevent(bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED);
                            if (!hasCashLossPrevent) {
                                hasCashLossPrevent = (bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED);
                            }
                            cashVideoServiceBeans.add(info);
                        }
                    }
                    if (isViewAttached()) {
                        mView.getCashServiceSuccess(cashVideoServiceBeans, hasCashLossPrevent);
                    }
                }

                @Override
                public void onFail(int code, String msg, ServiceListResp data) {
                    getAuditSecurityPolicyService();
                    if (isViewAttached()) {
                        mView.getServiceFail();
                    }
                }
            });
        }
    }
}
