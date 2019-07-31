package com.sunmi.ipc.setting.recognition;

import com.google.gson.Gson;
import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.CameraConfig;
import com.sunmi.ipc.view.DoorLineView;
import com.sunmi.ipc.view.IpcVideoView;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.log.LogCat;


class RecognitionSettingPresenter extends BasePresenter<RecognitionSettingContract.View>
        implements RecognitionSettingContract.Presenter, BaseNotification.NotificationCenterDelegate {

    private static final String TAG = RecognitionSettingPresenter.class.getSimpleName();

    private Callback mCallback = new Callback();

    private SunmiDevice mDevice;
    private CameraConfig mConfig;

    private int mZoomGap;

    @Override
    public IpcVideoView.ResultCallback getCallback() {
        return mCallback;
    }

    @Override
    public void init(SunmiDevice device) {
        this.mDevice = device;
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsGetStatus);
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsAutoFocus);
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsZoom);
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsFocus);
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsReset);
    }

    @Override
    public void updateState() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            IPCCall.getInstance().fsGetStatus(device.getIp());
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void face(int x, int y) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Move face case, auto focus: x=" + x + "; y=" + y);
            IPCCall.getInstance().fsAutoFocus(device.getIp(), x, y);
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void zoomIn() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            int gear = mConfig.getCurrentZoom() / mZoomGap + 1;
            int zoom = Math.min(mZoomGap * gear, mConfig.getMaxZoom());
            LogCat.d(TAG, "Zoom in: " + zoom);
            IPCCall.getInstance().fsZoom(device.getIp(), zoom);
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void zoomOut() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            int gear = (int) Math.ceil((double) mConfig.getCurrentZoom() / mZoomGap) - 1;
            int zoom = Math.max(mZoomGap * gear, 0);
            LogCat.d(TAG, "Zoom out: " + zoom);
            IPCCall.getInstance().fsZoom(device.getIp(), zoom);
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void zoomReset() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Zoom reset.");
            IPCCall.getInstance().fsReset(device.getIp(), 1);
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void focus(boolean isPlus) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            int focus = mConfig.getCurrentFocus();
            focus = isPlus ? Math.min(focus + 2, mConfig.getMaxFocus()) : Math.max(focus - 2, 0);
            LogCat.d(TAG, "Focus: " + focus);
            IPCCall.getInstance().fsFocus(device.getIp(), focus);
        } else if (isViewAttached()) {
            mView.showErrorDialog();
        }
    }

    @Override
    public void focusReset() {
        // TODO: API
        mView.hideLoadingDialog();
    }

    @Override
    public void line(int[] start, int[] end) {
        // TODO: API
        mView.hideLoadingDialog();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (isViewAttached()) {
            mView.hideLoadingDialog();
        }
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (res.getDataErrCode() != 1) {
            if (isViewAttached()) {
                mView.shortTip(R.string.ipc_recognition_network_error);
            }
            return;
        }
        mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
        mZoomGap = mConfig.getMaxZoom() / 10;
        if (isViewAttached()) {
            if (id == IpcConstants.fsGetStatus) {
                mView.updateViewStepTo(RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM, true);
            } else if (id == IpcConstants.fsZoom) {
                mView.enableControlBtn(true, mConfig.getCurrentZoom() != mConfig.getMaxZoom());
                mView.enableControlBtn(false, mConfig.getCurrentZoom() != 0);
            } else if (id == IpcConstants.fsFocus) {
                mView.enableControlBtn(true, mConfig.getCurrentFocus() != mConfig.getMaxFocus());
                mView.enableControlBtn(false, mConfig.getCurrentFocus() != 0);
            }
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsGetStatus);
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsAutoFocus);
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsZoom);
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsFocus);
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsReset);
    }

    private class Callback implements IpcVideoView.ResultCallback {

        @Override
        public void onResult(String result) {
            LogCat.d(TAG, result);
        }
    }

    private class DoorLineStateChangeListener implements DoorLineView.OnStateChangeListener {

        @Override
        public void onStateChanged(int state) {
            switch (state) {
                case DoorLineView.STATE_INIT:
                    break;
                case DoorLineView.STATE_START:
                    break;
                case DoorLineView.STATE_END:
                    break;
                default:
            }
        }
    }


}
