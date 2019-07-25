package com.sunmi.ipc.view;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;

import sunmi.common.utils.log.LogCat;

/**
 * IPC直播组件
 *
 * @author yinhui
 * @date 2019-07-25
 */
public class IpcVideoView extends ConstraintLayout
        implements SurfaceHolder.Callback, IOTCClient.Callback {

    private static final String TAG = IpcVideoView.class.getSimpleName();
    private static HandlerThread sThread = new HandlerThread("LiveVideo");
    private static Handler sHandler;

    static {
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
    }

    private SurfaceView mVideoView;
    private SurfaceHolder mVideoHolder;
    private String mUid;

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
        setBackgroundColor(ContextCompat.getColor(context, R.color.c_black));
        mVideoView = new SurfaceView(context);
        mVideoView.setId(View.generateViewId());
        addView(mVideoView, 0);
        ConstraintSet con = new ConstraintSet();
        con.clone(this);
        con.connect(mVideoView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        con.connect(mVideoView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        con.connect(mVideoView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        con.connect(mVideoView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        con.constrainHeight(mVideoView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        con.constrainWidth(mVideoView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        con.applyTo(this);
    }

    public void init(String uid) {
        this.mUid = uid;
        mVideoHolder = mVideoView.getHolder();
        mVideoHolder.addCallback(this);
        IOTCClient.setCallback(this);
    }

    public void setVideoRatio(int widthRatio, int heightRatio) {
        if (widthRatio <= 0 || heightRatio <= 0) {
            LogCat.e(TAG, "Width : height ratio must be above zero.");
        }
        ConstraintSet con = new ConstraintSet();
        con.clone(this);
        con.setDimensionRatio(mVideoView.getId(), widthRatio + ":" + heightRatio);
        con.applyTo(this);
    }

    void initLive() {
        sHandler.post(new Runnable() {
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

    }
}
