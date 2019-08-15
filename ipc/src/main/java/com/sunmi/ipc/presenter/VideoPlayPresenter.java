package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.VideoPlayContract;
import com.sunmi.ipc.model.CloudTimeSlotResp;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public class VideoPlayPresenter extends BasePresenter<VideoPlayContract.View>
        implements VideoPlayContract.Presenter {
    @Override
    public void getTimeSlots(int deviceId, final long startTime, final long endTime) {
        IPCCloudApi.getTimeSlots(deviceId, startTime, endTime, new RetrofitCallback<CloudTimeSlotResp>() {
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
        });
    }

}
