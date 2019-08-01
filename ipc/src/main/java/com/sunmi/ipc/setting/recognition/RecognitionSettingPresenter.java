package com.sunmi.ipc.setting.recognition;

import com.google.gson.Gson;
import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.CameraConfig;
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
    private int mBaseFocus;

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
        BaseNotification.newInstance().addStickObserver(this, IpcConstants.fsSetLine);
    }

    @Override
    public void updateState() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            IPCCall.getInstance().fsGetStatus(device.getIp());
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void face(int x, int y) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Move face case, auto focus: x=" + x + "; y=" + y);
            IPCCall.getInstance().fsAutoFocus(device.getIp(), x, y);
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void zoom(boolean isZoomIn) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            int gear;
            int zoom;
            if (isZoomIn) {
                gear = mConfig.getCurrentZoom() / mZoomGap + 1;
                zoom = Math.min(mZoomGap * gear, mConfig.getMaxZoom());
            } else {
                gear = (int) Math.ceil((double) mConfig.getCurrentZoom() / mZoomGap) - 1;
                zoom = Math.max(mZoomGap * gear, 0);
            }
            LogCat.d(TAG, "Zoom: " + zoom);
            IPCCall.getInstance().fsZoom(device.getIp(), zoom);
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void zoomReset() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Zoom reset.");
            IPCCall.getInstance().fsReset(device.getIp(), 1);
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
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
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void focusReset() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Focus reset: " + mBaseFocus);
            IPCCall.getInstance().fsFocus(device.getIp(), mBaseFocus);
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void line(int[] start, int[] end) {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Line set: [" + start[0] + ", " + start[1] + "] -> [" + end[0] + ", " + end[1] + "]");
            IPCCall.getInstance().fsLine(device.getIp(), start, end);
        } else if (isViewAttached()) {
            mView.showErrorDialog(R.string.ipc_setting_tip_network_dismatch);
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        LogCat.d(TAG, "Request back. id=" + id);
        if (isViewAttached()) {
            mView.hideLoadingDialog();
        }
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (res.getDataErrCode() != 1) {
            if (isViewAttached()) {
                mView.showErrorDialog(R.string.ipc_recognition_network_error);
            }
            return;
        }
        mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
        mZoomGap = mConfig.getMaxZoom() / 10;
        if (id == IpcConstants.fsGetStatus) {
            if (isViewAttached()) {
                mView.updateViewsStepTo(RecognitionSettingContract.STEP_2_RECOGNITION_ZOOM);
            }
        } else if (id == IpcConstants.fsZoom || id == IpcConstants.fsReset) {
            if (isViewAttached()) {
                mView.updateControlBtnEnable(true, mConfig.getCurrentZoom() != mConfig.getMaxZoom());
                mView.updateControlBtnEnable(false, mConfig.getCurrentZoom() != 0);
            }
        } else if (id == IpcConstants.fsFocus) {
            if (isViewAttached()) {
                int offset = mConfig.getCurrentFocus() - mBaseFocus;
                mView.updateControlBtnEnable(true, offset < 10 && mConfig.getCurrentFocus() < mConfig.getMaxFocus());
                mView.updateControlBtnEnable(false, offset > -10 && mConfig.getCurrentFocus() > 0);
            }
        } else if (id == IpcConstants.fsAutoFocus) {
            this.mBaseFocus = mConfig.getCurrentFocus();
        } else if (id == IpcConstants.fsSetLine) {
            if (isViewAttached()) {
                mView.complete();
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
        BaseNotification.newInstance().removeObserver(this, IpcConstants.fsSetLine);
    }

    private class Callback implements IpcVideoView.ResultCallback {

        @Override
        public void onResult(String result) {
            LogCat.d(TAG, result);
        }
    }


}
