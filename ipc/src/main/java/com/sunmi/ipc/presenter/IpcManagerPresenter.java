package com.sunmi.ipc.presenter;

import android.content.Context;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.ServiceListResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public class IpcManagerPresenter extends BasePresenter<IpcManagerContract.View>
        implements IpcManagerContract.Presenter {

    private static final int STATE_CASH_VIDEO_SERVICE_ON = 1;
    private static final int STATE_CASH_VIDEO_SERVICE_OFF = 2;
    private static final int STATE_CASH_VIDEO_SERVICE_EXPIRED = 3;

//    @Override
//    public void getPlaybackList(IOTCClient iotcClient, long start, long end) {
//        if (iotcClient == null) {
//            return;
//        }
//        iotcClient.getPlaybackList(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
//            @Override
//            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
//                List<VideoTimeSlotBean> slots = new ArrayList<>();
//                if (result.getData() != null && result.getData().size() > 0) {
//                    for (VideoTimeSlotBean slotBean : result.getData().get(0).getResult()) {
//                        slotBean.setApPlay(true);
//                        slots.add(slotBean);
//                    }
//                }
//                if (isViewAttached()) {
//                    mView.getDeviceTimeSlotSuccess(slots);
//                }
//            }
//
//            @Override
//            public void onError() {
//                if (isViewAttached()) {
//                    mView.getDeviceTimeSlotSuccess(null);
//                }
//            }
//        });
//    }

    @Override
    public void startLive(IOTCClient iotcClient) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.startPlay(new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                if (isViewAttached()) {
                    mView.startLiveSuccess();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

//    @Override
//    public void startPlayback(IOTCClient iotcClient, long start) {
//        if (iotcClient == null) {
//            return;
//        }
//        iotcClient.startPlayback(start, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
//            @Override
//            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
//                if (isViewAttached()) {
//                    mView.startPlaybackSuccess();
//                }
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
//    }

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
                                CashVideoServiceBean info = new CashVideoServiceBean();
                                info.setDeviceId(deviceId);
                                info.setDeviceSn(device.getDeviceSn());
                                info.setDeviceName(device.getDeviceName());
                                devices.add(info);
                                break;
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

    private IpcManageBean getStorage(IpcManageBean item, ServiceListResp.DeviceListBean data, Context context) {
        if (data != null) {
            item.setEnabled(true);
            item.setStatus(data.getStatus());
            if (data.getActiveStatus() == CommonConstants.SERVICE_INACTIVATED && data.getStatus() != CommonConstants.SERVICE_ALREADY_OPENED) {
                item.setSummary(context.getString(R.string.str_subscribe_free));
                item.setRightText(context.getString(R.string.str_use_free));
                item.setTagImageResId(R.mipmap.ipc_cloud_free_half_year);
            } else if (data.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                BaseNotification.newInstance().postNotificationName(CommonNotifications.cloudStorageOpened);
                item.setTitle(data.getServiceName());
                item.setSummary(context.getString(R.string.str_remaining_validity_period,
                        DateTimeUtils.secondToPeriod(data.getValidTime())));
            } else if (data.getStatus() == CommonConstants.SERVICE_NOT_OPENED) {
                item.setSummary(context.getString(R.string.str_subscribe_free));
                item.setRightText(context.getString(R.string.str_subscribe_now));
            } else if (data.getStatus() == CommonConstants.SERVICE_EXPIRED) {
                item.setSummary(context.getString(R.string.str_expired));
            }
        } else {
            item.setRightText(context.getString(R.string.str_coming_soon));
            item.setEnabled(false);
        }
        return item;
    }

}
