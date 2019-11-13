package com.sunmi.ipc.presenter;

import android.content.Context;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.CloudTimeSlotResp;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.model.StorageListResp;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
public class IpcManagerPresenter extends BasePresenter<IpcManagerContract.View>
        implements IpcManagerContract.Presenter {

    @Override
    public void getTimeSlots(int deviceId, final long startTime, final long endTime) {
        IpcCloudApi.getInstance().getTimeSlots(deviceId, startTime, endTime, new RetrofitCallback<CloudTimeSlotResp>() {
            @Override
            public void onSuccess(int code, String msg, CloudTimeSlotResp data) {
                if (!isViewAttached()) {
                    return;
                }
                if (data.getTotalCount() == 0) {
                    mView.getCloudTimeSlotFail();
                } else {
                    mView.getCloudTimeSlotSuccess(startTime, endTime, data.getTimeslots());
                }
            }

            @Override
            public void onFail(int code, String msg, CloudTimeSlotResp data) {
                if (isViewAttached()) {
                    mView.getCloudTimeSlotFail();
                }
            }
        });
    }

    @Override
    public void getPlaybackList(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.getPlaybackList(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                List<VideoTimeSlotBean> slots = new ArrayList<>();
                if (result.getData() != null && result.getData().size() > 0) {
                    for (VideoTimeSlotBean slotBean : result.getData().get(0).getResult()) {
                        slotBean.setApPlay(true);
                        slots.add(slotBean);
                    }
                }
                if (isViewAttached()) {
                    mView.getDeviceTimeSlotSuccess(slots);
                }
            }

            @Override
            public void onError() {
                if (isViewAttached()) {
                    mView.getDeviceTimeSlotSuccess(null);
                }
            }
        });
    }

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

    @Override
    public void startPlayback(IOTCClient iotcClient, long start) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.startPlayback(start, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                if (isViewAttached()) {
                    mView.startPlaybackSuccess();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void getCloudVideoList(int deviceId, long start, long end) {
        if (deviceId <= 0) {
            mView.shortTip("设备信息不完整");
            return;
        }
        IpcCloudApi.getInstance().getVideoList(deviceId, start, end, new RetrofitCallback<VideoListResp>() {
            @Override
            public void onSuccess(int code, String msg, VideoListResp data) {
                if (isViewAttached()) {
                    mView.getCloudVideosSuccess(data.getVideo_list());
                }
            }

            @Override
            public void onFail(int code, String msg, VideoListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                }
            }
        });
    }

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
    public void getStorageList(String deviceSn) {
        List<String> snList = new ArrayList<>();
        snList.add(deviceSn);
        IpcCloudApi.getInstance().getStorageList(snList, new RetrofitCallback<StorageListResp>() {
            @Override
            public void onSuccess(int code, String msg, StorageListResp data) {
                if (data.getDeviceList().size() > 0) {
                    setStorage(data.getDeviceList().get(0), BaseApplication.getContext());
                } else {
                    setStorage(null, BaseApplication.getContext());
                    if (isViewAttached()) {
                        mView.shortTip(R.string.tip_cloud_storage_error);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, StorageListResp data) {
                setStorage(null, BaseApplication.getContext());
                if (isViewAttached()) {
                    mView.shortTip(R.string.tip_cloud_storage_error);
                }
            }
        });
    }

    private void setStorage(StorageListResp.DeviceListBean data, Context context) {
        IpcManageBean cloudStorage = new IpcManageBean(R.mipmap.ipc_cloud_storage, context.getString(R.string.str_cloud_storage),
                context.getString(R.string.str_setting_detail));
        if (data != null) {
            cloudStorage.setEnabled(true);
            cloudStorage.setStatus(data.getStatus());
            if (data.getActiveStatus() == CommonConstants.ACTIVE_CLOUD_INACTIVATED && data.getStatus() != CommonConstants.CLOUD_STORAGE_ALREADY_OPENED) {
                cloudStorage.setSummary(context.getString(R.string.str_subscribe_free));
                cloudStorage.setRightText(context.getString(R.string.str_use_free));
                cloudStorage.setTagImageResId(R.mipmap.ipc_cloud_free_half_year);
            } else if (data.getStatus() == CommonConstants.CLOUD_STORAGE_ALREADY_OPENED) {
                cloudStorage.setTitle(data.getServiceName());
                cloudStorage.setSummary(context.getString(R.string.str_remaining_validity_period,
                        DateTimeUtils.secondToPeriod(data.getValidTime())));
            } else if (data.getStatus() == CommonConstants.CLOUD_STORAGE_NOT_OPENED) {
                cloudStorage.setSummary(context.getString(R.string.str_subscribe_free));
                cloudStorage.setRightText(context.getString(R.string.str_subscribe_now));
            } else if (data.getStatus() == CommonConstants.CLOUD_STORAGE_EXPIRED) {
                cloudStorage.setSummary(context.getString(R.string.str_expired));
            }
        } else {
            cloudStorage.setRightText(context.getString(R.string.str_coming_soon));
            cloudStorage.setEnabled(false);
        }
        if (isViewAttached()) {
            mView.getStorageSuccess(cloudStorage);
        }
    }

}
