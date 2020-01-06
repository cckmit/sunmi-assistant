package com.sunmi.ipc.presenter;

import android.content.Context;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.ServiceListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public class IpcManagerPresenter extends BasePresenter<IpcManagerContract.View>
        implements IpcManagerContract.Presenter {

    private static final int STATE_CASH_VIDEO_SERVICE_ON = 1;

    @Override
    public void changeQuality(int quality, IOTCClient iotcClient) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.changeValue(quality, new P2pCmdCallback() {
            @Override
            public void onResponse(int cmd, IotcCmdResp result) {
                if (isViewAttached()) {
                    mView.changeQualitySuccess(quality);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public void resumePlay(int quality, IOTCClient iotcClient) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.changeValue(quality, new P2pCmdCallback() {
            @Override
            public void onResponse(int cmd, IotcCmdResp result) {

            }

            @Override
            public void onError() {
                if (isViewAttached()) {
                    mView.startLiveFail();
                }
            }
        });
    }

    @Override
    public void getStorageList(String deviceSn, final IpcManageBean item) {
        List<String> snList = new ArrayList<>();
        snList.add(deviceSn);
        IpcCloudApi.getInstance().getStorageList(snList, new RetrofitCallback<ServiceListResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceListResp data) {
                if (isViewAttached()) {
                    if (data.getDeviceList().size() > 0) {
                        mView.getStorageSuccess(getStorage(item, data.getDeviceList().get(0), BaseApplication.getContext()));
                    } else {
                        mView.getStorageSuccess(getStorage(item, null, BaseApplication.getContext()));
                        mView.shortTip(R.string.tip_cloud_storage_error);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceListResp data) {
                if (isViewAttached()) {
                    mView.getStorageSuccess(getStorage(item, null, BaseApplication.getContext()));
                    mView.shortTip(R.string.tip_cloud_storage_error);
                }
            }
        });
    }

    @Override
    public void getCashVideoService(final int deviceId) {
        IpcCloudApi.getInstance().getAuditVideoServiceList(null,
                new RetrofitCallback<ServiceListResp>() {

                    @Override
                    public void onSuccess(int code, String msg, ServiceListResp data) {
                        List<ServiceListResp.DeviceListBean> list = data.getDeviceList();
                        ArrayList<CashVideoServiceBean> devices = new ArrayList<>();
                        if (list == null || list.isEmpty()) {
                            if (isViewAttached()) {
                                mView.getCashVideoServiceSuccess(devices, false);
                            }
                            return;
                        }
                        boolean hasCashVideoService = false;
                        for (ServiceListResp.DeviceListBean device : list) {
                            if (device.getStatus() == STATE_CASH_VIDEO_SERVICE_ON) {
                                hasCashVideoService = true;
                            }
                            if (device.getStatus() == STATE_CASH_VIDEO_SERVICE_ON
                                    && device.getDeviceId() == deviceId) {
                                getAuditSecurityPolicyService(device.getDeviceSn());
                                return;
                            }
                        }
                        if (isViewAttached()) {
                            mView.getCashVideoServiceSuccess(devices, hasCashVideoService);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, ServiceListResp data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.toast_network_error);
                            mView.hideLoadingDialog();
                        }
                    }
                });
    }

    private void getAuditSecurityPolicyService(String deviceSn) {
        List<String> snList = new ArrayList<>();
        snList.add(deviceSn);
        IpcCloudApi.getInstance().getAuditSecurityPolicyList(snList, new RetrofitCallback<ServiceListResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceListResp data) {
                List<ServiceListResp.DeviceListBean> beans = data.getDeviceList();
                ArrayList<CashVideoServiceBean> cashVideoServiceBeans = new ArrayList<>();
                if (beans.size() > 0) {
                    for (ServiceListResp.DeviceListBean bean : beans) {
                        CashVideoServiceBean info = new CashVideoServiceBean();
                        info.setDeviceId(bean.getDeviceId());
                        info.setDeviceSn(bean.getDeviceSn());
                        info.setDeviceName(bean.getDeviceName());
                        info.setImgUrl(bean.getImgUrl());
                        info.setHasCashLossPrevent(bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED);
                        cashVideoServiceBeans.add(info);
                    }
                }
                if (isViewAttached()) {
                    mView.getCashVideoServiceSuccess(cashVideoServiceBeans, true);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceListResp data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.toast_network_error);
                    mView.hideLoadingDialog();
                }
            }
        });

    }

    private IpcManageBean getStorage(IpcManageBean item, ServiceListResp.DeviceListBean data, Context context) {
        if (data != null) {
            item.setEnabled(true);
            item.setStatus(data.getStatus());
            if (data.getActiveStatus() == CommonConstants.SERVICE_INACTIVATED && data.getStatus() != CommonConstants.SERVICE_ALREADY_OPENED) {
                item.setSummary(context.getString(R.string.str_subscribe_free));
                item.setRightText(context.getString(R.string.str_use_free));
                item.setTagImageResId(R.mipmap.ipc_cloud_free_half_year);
            } else if (data.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                item.setTitle(data.getServiceName());
                item.setSummary(context.getString(R.string.str_remaining_validity_period,
                        DateTimeUtils.secondToPeriod(data.getValidTime())));
                item.setRightText(context.getString(R.string.str_setting_detail));
                item.setTagImageResId(-1);
            } else if (data.getStatus() == CommonConstants.SERVICE_NOT_OPENED) {
                item.setSummary(context.getString(R.string.str_subscribe_free));
                item.setRightText(context.getString(R.string.str_subscribe_now));
                item.setTagImageResId(-1);
            } else if (data.getStatus() == CommonConstants.SERVICE_EXPIRED) {
                item.setSummary(context.getString(R.string.str_expired));
                item.setRightText(context.getString(R.string.str_setting_detail));
                item.setTagImageResId(-1);
            }
        } else {
            item.setRightText(context.getString(R.string.str_coming_soon));
            item.setTagImageResId(-1);
            item.setEnabled(false);
        }
        return item;
    }

    public void handleResponse(int opcode, ResponseBean res) {
        if (opcode == OpcodeConstants.getVideoParams) {
            try {
                int compensation = res.getResult().getInt("compensation");
                int saturation = res.getResult().getInt("saturation");
                int contrast = res.getResult().getInt("contrast");
                if (isViewAttached()) {
                    mView.videoParamsObtained(compensation, saturation, contrast);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
