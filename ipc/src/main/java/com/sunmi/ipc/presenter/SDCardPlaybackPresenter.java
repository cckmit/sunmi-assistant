package com.sunmi.ipc.presenter;

import com.sunmi.ipc.contract.SDCardPlaybackContract;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * Description:
 * Created by bruce on 2019/12/3.
 */
public class SDCardPlaybackPresenter extends BasePresenter<SDCardPlaybackContract.View>
        implements SDCardPlaybackContract.Presenter {

    private List<VideoTimeSlotBean> allTimeSlotBeans = new ArrayList<>();

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

    public void getPlaybackListForCalendar(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.getPlaybackList(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
            @Override
            public void onResponse(int cmd, IotcCmdResp<List<VideoTimeSlotBean>> result) {
                if (result.getData() != null && result.getData().size() > 0
                        && result.getData().get(0).getResult().size() > 0) {
                    allTimeSlotBeans.addAll(result.getData().get(0).getResult());
                    getPlaybackListForCalendar(iotcClient,
                            allTimeSlotBeans.get(allTimeSlotBeans.size() - 1).getEndTime(), end);
                } else {
                    if (isViewAttached()) {
                        mView.getAllTimeSlotSuccess(allTimeSlotBeans);
                    }
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void startPlayback(IOTCClient iotcClient, long start, long end) {
        if (iotcClient == null) {
            return;
        }
        iotcClient.startPlayback(start, end, new P2pCmdCallback<List<VideoTimeSlotBean>>() {
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

}
