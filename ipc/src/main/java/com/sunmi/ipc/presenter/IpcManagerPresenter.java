package com.sunmi.ipc.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.ServiceResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.ConfigManager;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public class IpcManagerPresenter extends BasePresenter<IpcManagerContract.View>
        implements IpcManagerContract.Presenter {

    private static final int STATE_CASH_VIDEO_SERVICE_ON = 1;
    private int status;
    private int validTime;

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
        IpcCloudApi.getInstance().getStorageList(SpUtils.getCompanyId(), SpUtils.getShopId(), snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    if (data.getList().size() > 0) {
                        mView.getStorageSuccess(getStorage(item, data.getList().get(0), BaseApplication.getContext()));
                    } else {
                        mView.getStorageSuccess(getStorage(item, null, BaseApplication.getContext()));
                        mView.shortTip(R.string.tip_cloud_storage_error);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.getStorageSuccess(getStorage(item, null, BaseApplication.getContext()));
                    mView.shortTip(R.string.tip_cloud_storage_error);
                }
            }
        });
    }

    @Override
    public void getCashVideoService(final int deviceId) {
        IpcCloudApi.getInstance().getAuditVideoServiceList(SpUtils.getCompanyId(), SpUtils.getShopId(), null,
                new RetrofitCallback<ServiceResp>() {

                    @Override
                    public void onSuccess(int code, String msg, ServiceResp data) {
                        List<ServiceResp.Info> list = data.getList();
                        ArrayList<CashServiceInfo> devices = new ArrayList<>();
                        if (list == null || list.isEmpty()) {
                            if (isViewAttached()) {
                                mView.getCashVideoServiceSuccess(devices, false,
                                        CommonConstants.SERVICE_NOT_OPENED, 0);
                            }
                            return;
                        }
                        boolean hasCashVideoService = false;
                        for (ServiceResp.Info device : list) {
                            if (device.getStatus() == STATE_CASH_VIDEO_SERVICE_ON) {
                                hasCashVideoService = true;
                            }
                            if (device.getStatus() == STATE_CASH_VIDEO_SERVICE_ON
                                    && device.getDeviceId() == deviceId) {
                                getCashPreventService(device.getDeviceSn());
                                status = device.getStatus();
                                validTime = device.getValidTime();
                                return;
                            } else if (device.getStatus() == CommonConstants.SERVICE_EXPIRED
                                    && device.getDeviceId() == deviceId) {
                                if (isViewAttached()) {
                                    mView.getCashVideoServiceSuccess(devices, hasCashVideoService, device.getStatus(), 0);
                                }
                                return;
                            }
                        }
                        if (isViewAttached()) {
                            mView.getCashVideoServiceSuccess(devices, hasCashVideoService,
                                    CommonConstants.SERVICE_NOT_OPENED, 0);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, ServiceResp data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.toast_network_error);
                            mView.hideLoadingDialog();
                        }
                    }
                });
    }

    public void getCashPreventService(String deviceSn) {
        List<String> snList = new ArrayList<>();
        snList.add(deviceSn);
        IpcCloudApi.getInstance().getAuditSecurityPolicyList(SpUtils.getCompanyId(), SpUtils.getShopId(), snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                List<ServiceResp.Info> beans = data.getList();
                ArrayList<CashServiceInfo> cashServiceInfos = new ArrayList<>();
                if (beans.size() > 0) {
                    ServiceResp.Info bean = beans.get(0);
                    CashServiceInfo info = new CashServiceInfo();
                    info.setDeviceId(bean.getDeviceId());
                    info.setDeviceSn(bean.getDeviceSn());
                    info.setDeviceName(bean.getDeviceName());
                    info.setImgUrl(bean.getImgUrl());
                    info.setHasCashLossPrevention(bean.getStatus() != CommonConstants.SERVICE_NOT_OPENED
                            && ConfigManager.get().getCashSecurityEnable());
                    cashServiceInfos.add(info);
                    if (info.isHasCashLossPrevention()) {
                        status = bean.getStatus();
                        validTime = bean.getValidTime();
                    }
                }
                if (isViewAttached()) {
                    mView.getCashVideoServiceSuccess(cashServiceInfos, true, status, validTime);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.toast_network_error);
                    mView.hideLoadingDialog();
                }
            }
        });
    }

    @Override
    public void onServiceSubscribeResult(Intent intent, String deviceSn) {
        String args = intent.getStringExtra("args");
        if (!TextUtils.isEmpty(args)) {
            try {
                JSONObject jsonObject = new JSONObject(args);
                int code = jsonObject.getInt("code");
                JSONObject data = jsonObject.getJSONObject("data");
                if (code == 100) {
                    JSONArray list = data.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject serviceObject = list.optJSONObject(i);
                        int service = serviceObject.getInt("service");
                        int status = serviceObject.getInt("status");
                        if (service == IpcConstants.SERVICE_TYPE_CASH_PREVENT
                                && status == CommonConstants.RESULT_OK) {
                            getCashPreventService(deviceSn);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private IpcManageBean getStorage(IpcManageBean item, ServiceResp.Info data, Context context) {
        if (data != null) {
            item.setEnabled(true);
            item.setStatus(data.getStatus());
            if (data.getActiveStatus() == CommonConstants.SERVICE_INACTIVATED && data.getStatus() != CommonConstants.SERVICE_ALREADY_OPENED) {
                item.setSummary(context.getString(R.string.str_subscribe_free));
                item.setRightText(context.getString(R.string.str_use_free));
                if (!CommonHelper.isGooglePlay()) {
                    item.setTagImageResId(R.mipmap.ipc_cloud_free_half_year);
                } else {
                    item.setTagImageResId(-1);
                }
            } else if (data.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                if (data.getServiceTag() == 1) {
                    item.setTitle(context.getString(R.string.service_cloud_7));
                } else {
                    item.setTitle(context.getString(R.string.service_cloud_30));
                }
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
