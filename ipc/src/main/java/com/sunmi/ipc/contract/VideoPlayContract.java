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

        void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots);

        void startLiveSuccess();

        void startPlaybackSuccess();

    }

    interface Presenter {

        void getPlaybackList(IOTCClient iotcClient, long start, long end);

        void startLive(IOTCClient iotcClient);

        void startPlayback(IOTCClient iotcClient, long start);

    }

}
