package com.sunmi.ipc.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;

import org.androidannotations.api.BackgroundExecutor;

import sunmi.common.utils.log.LogCat;

/**
 * IPC设备播放组件
 *
 * @author yinhui
 * @date 2019-07-25
 */
public class IpcVideoView extends SurfaceView
        implements SurfaceHolder.Callback, IOTCClient.Callback {

    private static final String TAG = IpcVideoView.class.getSimpleName();

    private SurfaceHolder mVideoHolder;
    private ResultCallback mCallback;

    private String mUid;
    private float mWidthHeightRatio = -1;

    private H264Decoder mVideoDecoder = null;
    private AACDecoder mAudioDecoder = null;

    public IpcVideoView(Context context) {
        this(context, null);
    }

    public IpcVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IpcVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 直播View初始化
     *
     * @param uid        IPC的UID
     * @param videoRatio 视频长宽比（width / height）
     * @param callback   IOTCResult回调
     */
    public void init(String uid, float videoRatio, ResultCallback callback) {
        this.mWidthHeightRatio = videoRatio;
        this.mUid = uid;
        mVideoHolder = getHolder();
        mVideoHolder.addCallback(this);
        mCallback = callback;
        IOTCClient.setCallback(this);
    }

    public Rect getRect() {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
    }

    void initLive() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                IOTCClient.init(mUid);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogCat.d(TAG, "surfaceCreated");
        mVideoDecoder = new H264Decoder(holder.getSurface(), 0);
        mAudioDecoder = new AACDecoder();
        initLive();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mVideoDecoder != null) {
            mVideoDecoder.stopRunning();
            mVideoDecoder = null;
        }
        if (mAudioDecoder != null) {
            mAudioDecoder.stop();
            mAudioDecoder = null;
        }
        IOTCClient.close();
    }

    @Override
    public void onVideoReceived(byte[] videoBuffer) {
        if (mVideoDecoder != null) {
            mVideoDecoder.setVideoData(videoBuffer);
        }
    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        if (mAudioDecoder != null) {
            mAudioDecoder.setAudioData(audioBuffer);
        }
    }

    @Override
    public void IOTCResult(String result) {
        if (mCallback != null) {
            mCallback.onResult(result);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mWidthHeightRatio <= 0 || width == 0 || height == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        float ratio = (float) width / (float) height;
        int newWidth = ratio > mWidthHeightRatio ? (int) (height * mWidthHeightRatio) : width;
        int newHeight = ratio > mWidthHeightRatio ? height : (int) (width / mWidthHeightRatio);
        setMeasuredDimension(newWidth, newHeight);
    }

    public interface ResultCallback {
        /**
         * IPC设备结果回调
         *
         * @param result 设备端返回的结果
         */
        void onResult(String result);
    }
}
