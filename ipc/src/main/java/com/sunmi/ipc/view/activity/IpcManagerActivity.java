package com.sunmi.ipc.view.activity;

import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.presenter.IpcManagerPresenter;

import org.androidannotations.annotations.EActivity;

import java.util.List;

import sunmi.common.base.BaseMvpActivity;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
@EActivity(resName = "activity_ipc_manager")
public class IpcManagerActivity extends BaseMvpActivity<IpcManagerPresenter>
        implements IpcManagerContract.View {

    @Override
    public void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots) {

    }

    @Override
    public void getCloudTimeSlotFail() {

    }

    @Override
    public void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots) {

    }

    @Override
    public void startLiveSuccess() {

    }

    @Override
    public void startPlaybackSuccess() {

    }

    @Override
    public void getCloudVideosSuccess(List<VideoListResp.VideoBean> videoBeans) {

    }
}
