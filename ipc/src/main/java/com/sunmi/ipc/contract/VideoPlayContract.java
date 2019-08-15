package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.utils.IOTCClient;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public interface VideoPlayContract {

    interface View extends BaseView {
        void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots);

        void getCloudTimeSlotFail();

        void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots);
    }

    interface Presenter {
        void getTimeSlots(int deviceId, long startTime, long endTime);

        void getPlaybackList(IOTCClient iotcClient, long start, long end);
    }
}
