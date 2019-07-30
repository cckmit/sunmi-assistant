package com.sunmi.ipc.setting.recognition;

import com.sunmi.ipc.view.IpcVideoView;

import sunmi.common.base.BasePresenter;
import sunmi.common.utils.log.LogCat;


class RecognitionSettingPresenter extends BasePresenter<RecognitionSettingContract.View>
        implements RecognitionSettingContract.Presenter {

    private static final String TAG = RecognitionSettingPresenter.class.getSimpleName();

    private Callback mCallback = new Callback();

    private int mZoom;
    private int mFocus;

    @Override
    public IpcVideoView.ResultCallback getCallback() {
        return mCallback;
    }

    @Override
    public void init() {
    }

    @Override
    public void face(int[] coordinate) {

    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void zoomReset() {

    }

    @Override
    public void focus(boolean isPlus) {

    }

    @Override
    public void focusReset() {

    }

    @Override
    public void line(int[] start, int[] end) {

    }

    class Callback implements IpcVideoView.ResultCallback {

        @Override
        public void onResult(String result) {
            LogCat.d(TAG, result);
        }
    }

}
