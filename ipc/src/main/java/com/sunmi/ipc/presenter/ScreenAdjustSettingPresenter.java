package com.sunmi.ipc.presenter;

import com.google.gson.Gson;
import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.ScreenAdjustSettingContract;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.model.CameraConfig;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.log.LogCat;


public class ScreenAdjustSettingPresenter extends BasePresenter<ScreenAdjustSettingContract.View>
        implements ScreenAdjustSettingContract.Presenter, BaseNotification.NotificationCenterDelegate {

    private static final String TAG = ScreenAdjustSettingPresenter.class.getSimpleName();

    private static final int SD_STATUS_NONE = 0;
    private static final int SD_STATUS_UNINITIALIZED = 1;
    private static final int SD_STATUS_FINE = 2;
    private static final int SD_STATUS_UNKNOWN = 3;

    private SunmiDevice mDevice;
    private CameraConfig mConfig;

    private int mZoomGap;
    private int mBaseFocus;

    @Override
    public void init(SunmiDevice device) {
        this.mDevice = device;
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsGetStatus);
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsAutoFocus);
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsZoom);
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsFocus);
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsReset);
        BaseNotification.newInstance().addStickObserver(this, OpcodeConstants.fsSetLine);
    }

    @Override
    public void updateControlBtnEnable(boolean isZoom) {
        if (isZoom) {
            updateZoomBtnEnable();
        } else {
            updateFocusBtnEnable();
        }
    }

    @Override
    public void updateState() {
        SunmiDevice device = CommonConstants.SUNMI_DEVICE_MAP.get(mDevice.getDeviceid());
        if (device != null) {
            LogCat.d(TAG, "Get status: " + device.getIp());
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
            LogCat.d(TAG, "Focus: " + focus + "; Base=" + mBaseFocus);
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
            LogCat.e(TAG, res.getReturnData());
            if (isViewAttached()) {
                mView.showErrorDialog(R.string.ipc_recognition_network_error);
            }
            return;
        }
        switch (id) {
            case OpcodeConstants.fsGetStatus:
                this.mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
                this.mBaseFocus = mConfig.getCurrentFocus();
                this.mZoomGap = mConfig.getMaxZoom() / 10;
                if (isViewAttached()) {
                    mView.updateViewsStepTo(ScreenAdjustSettingContract.STEP_2_RECOGNITION_ZOOM);
                }
                break;
            case OpcodeConstants.fsAutoFocus:
                this.mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
                this.mBaseFocus = mConfig.getCurrentFocus();
                break;
            case OpcodeConstants.fsZoom:
            case OpcodeConstants.fsReset:
                this.mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
                this.mBaseFocus = mConfig.getCurrentFocus();
                updateZoomBtnEnable();
                break;
            case OpcodeConstants.fsFocus:
                this.mConfig = new Gson().fromJson(res.getResult().toString(), CameraConfig.class);
                updateFocusBtnEnable();
                break;
            case OpcodeConstants.fsSetLine:
                if (isViewAttached()) {
                    mView.complete();
                }
            default:
        }
    }

    private void updateZoomBtnEnable() {
        if (isViewAttached()) {
            mView.updateControlBtnEnable(true, mConfig.getCurrentZoom() < mConfig.getMaxZoom());
            mView.updateControlBtnEnable(false, mConfig.getCurrentZoom() > 0);
        }
    }

    private void updateFocusBtnEnable() {
        if (isViewAttached()) {
            int offset = mConfig.getCurrentFocus() - mBaseFocus;
            mView.updateControlBtnEnable(true, offset < 10 && mConfig.getCurrentFocus() < mConfig.getMaxFocus());
            mView.updateControlBtnEnable(false, offset > -10 && mConfig.getCurrentFocus() > 0);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsGetStatus);
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsAutoFocus);
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsZoom);
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsFocus);
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsReset);
        BaseNotification.newInstance().removeObserver(this, OpcodeConstants.fsSetLine);
    }

}
