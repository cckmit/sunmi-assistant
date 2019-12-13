package com.sunmi.ipc.contract;

import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/11/15.
 */
public interface CloudPlaybackContract {

    interface View extends BaseView {
        void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots);

        void showNoVideoTip();

        void getCloudTimeSlotFail();

        void getCloudVideosSuccess(List<VideoListResp.VideoBean> videoBeans);

        void getCloudVideosFail();

    }

    interface Presenter {

        void getTimeSlots(int deviceId, long startTime, long endTime);

        void getCloudVideoList(int deviceId, long start, long end);

    }

}
