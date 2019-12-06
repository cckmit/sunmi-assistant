package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.utils.IOTCClient;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/12/3.
 */
public interface SDCardPlaybackContract {
    interface View extends BaseView {

        void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots);

        void startPlaybackSuccess();

        void getAllTimeSlotSuccess(List<VideoTimeSlotBean> slots);

    }

    interface Presenter {

        void getPlaybackList(IOTCClient iotcClient, long start, long end);

        void startPlayback(IOTCClient iotcClient, long start, long end);

    }

}
