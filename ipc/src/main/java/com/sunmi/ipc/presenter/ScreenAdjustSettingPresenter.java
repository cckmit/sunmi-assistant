package com.sunmi.ipc.presenter;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.ScreenAdjustSettingContract;
import com.sunmi.ipc.model.CameraConfig;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.log.LogCat;


public class ScreenAdjustSettingPresenter extends BasePresenter<ScreenAdjustSettingContract.View>
        implements ScreenAdjustSettingContract.Presenter, BaseNotification.NotificationCenterDelegate {

    private static final String TAG = ScreenAdjustSettingPresenter.class.getSimpleName();

    private static final int TIMEOUT_8S = 8_000;
    private static final int TIMEOUT_13S = 13_000;
    private static final int TIMEOUT_18S = 18_000;

    private SunmiDevice mDevice;
    private CameraConfig mConfig;

    private int mZoomGap;
    private int mBaseFocus;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeoutTask = new TimeoutTask();

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
        LogCat.d(TAG, "Get status.");
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_8S);
        IPCCall.getInstance().fsGetStatus(mDevice.getModel(), mDevice.getDeviceid());
    }

    @Override
    public void face(int x, int y) {
        LogCat.d(TAG, "Move face case, auto focus: x=" + x + "; y=" + y);
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_13S);
        IPCCall.getInstance().fsAutoFocus(mDevice.getModel(), mDevice.getDeviceid(), x, y);
    }

    @Override
    public void zoom(boolean isZoomIn) {
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
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_18S);
        IPCCall.getInstance().fsZoom(mDevice.getModel(), mDevice.getDeviceid(), zoom);
    }

    @Override
    public void zoomReset() {
        LogCat.d(TAG, "Zoom reset.");
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_18S);
        IPCCall.getInstance().fsReset(mDevice.getModel(), mDevice.getDeviceid(), 1);
    }

    @Override
    public void focus(boolean isPlus) {
        int focus = mConfig.getCurrentFocus();
        focus = isPlus ? Math.min(focus + 2, mConfig.getMaxFocus()) : Math.max(focus - 2, 0);
        LogCat.d(TAG, "Focus: " + focus + "; Base=" + mBaseFocus);
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_13S);
        IPCCall.getInstance().fsFocus(mDevice.getModel(), mDevice.getDeviceid(), focus);
    }

    @Override
    public void focusReset() {
        LogCat.d(TAG, "Focus reset: " + mBaseFocus);
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_13S);
        IPCCall.getInstance().fsFocus(mDevice.getModel(), mDevice.getDeviceid(), mBaseFocus);
    }

    @Override
    public void line(int[] start, int[] end) {
        LogCat.d(TAG, "Line set: [" + start[0] + ", " + start[1] + "] -> [" + end[0] + ", " + end[1] + "]");
        mHandler.postDelayed(mTimeoutTask, TIMEOUT_8S);
        IPCCall.getInstance().fsLine(mDevice.getModel(), mDevice.getDeviceid(), start, end);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        LogCat.d(TAG, "Request back. id=" + id);
        mHandler.removeCallbacks(mTimeoutTask);
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
        mHandler.removeCallbacks(mTimeoutTask);
    }

    private class TimeoutTask implements Runnable {

        @Override
        public void run() {
            LogCat.d(TAG, "Request timeout.");
            if (isViewAttached()) {
                mView.hideLoadingDialog();
            }
        }
    }

}
