package com.sunmi.ipc.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;

import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.log.LogCat;

/**
 * IPC设备播放组件
 *
 * @author yinhui
 * @date 2019-07-25
 */
public class IpcVideoView extends SurfaceView
        implements SurfaceHolder.Callback, IOTCClient.StatusCallback, IOTCClient.ReceiverCallback {

    private static final String TAG = IpcVideoView.class.getSimpleName();

    private SurfaceHolder mVideoHolder;

    private String mUid;
    private float mWidthHeightRatio = -1;

    private H264Decoder mVideoDecoder = null;
    private AACDecoder mAudioDecoder = null;
    IOTCClient iotcClient;

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
     */
    public void init(String uid, float videoRatio) {
        this.mWidthHeightRatio = videoRatio;
        this.mUid = uid;
        mVideoHolder = getHolder();
        mVideoHolder.addCallback(this);
        iotcClient = new IOTCClient(uid);
        iotcClient.setStatusCallback(this);
    }

    public Rect getRect() {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
    }

    void initLive() {
        ThreadPool.getCachedThreadPool().submit(() -> iotcClient.init());
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
        iotcClient.close();
    }

    @Override
    public void initFail() {

    }

    @Override
    public void onVideoReceived(byte[] frameInfo, byte[] videoBuffer) {
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

}
