package com.sunmi.ipc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.Surface;

import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.tutk.IOTC.P2pCmdCallback;

import sunmi.common.utils.ByteUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/12/4.
 */
public class P2pService extends Service
        implements IOTCClient.ReceiverCallback, IOTCClient.StatusCallback {

    private H264Decoder videoDecoder;
    private AACDecoder audioDecoder;
    private IOTCClient iotcClient;
    private OnPlayStatusChangedListener statusChangedListener;
    private OnPlayingListener onPlayingListener;

    private boolean isPlaying;//是否正在播放
    private long endTime;

    private String uid;

    private long currentVideoTime;//回放的当前时间

    @Override
    public void initFail() {
        if (statusChangedListener != null) {
            statusChangedListener.onPlayFail();
        }
    }

    public class MyBinder extends Binder {
        public P2pService getService() {
            return P2pService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private MyBinder binder = new MyBinder();

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioDecoder = new AACDecoder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogCat.e("p2pService", "onStartCommand - startId = "
                + startId + ", Thread = " + Thread.currentThread().getName());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogCat.e("p2pService", "onBind - Thread = " + Thread.currentThread().getName());
        uid = intent.getStringExtra("uid");
        if (iotcClient == null) {
            iotcClient = new IOTCClient(uid);
            iotcClient.setReceiverCallback(this);
            iotcClient.setStatusCallback(this);
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogCat.e("p2pService", "onUnbind - from = " + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onDestroy() {
        LogCat.e("p2pService", "onDestroy - Thread = " + Thread.currentThread().getName());
        super.onDestroy();
        release();
    }

    @Override
    public void onVideoReceived(byte[] frameInfo, byte[] videoBuffer) {
        if (videoDecoder == null) {
            return;
        }
        try {
            videoDecoder.setVideoData(videoBuffer);
            if (!isPlaying || !videoDecoder.isPlaying()) {
                isPlaying = videoDecoder.isPlaying();
                if (isPlaying && statusChangedListener != null) {
                    statusChangedListener.onPlayStarted();
                }
            } else {
                long currentTime = getFrameInfoTime(frameInfo);
                if (endTime > 0 && currentTime >= endTime - 2) {
                    if (statusChangedListener != null) {
                        statusChangedListener.onPlayFinished();
                    }
                } else {
                    if (onPlayingListener != null) {
                        onPlayingListener.onPlaying(currentTime, frameInfo[2]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频帧的时间（如果是直播，获取的是自设备开机的相对时间，单位-ms；如果是回放，获取的视频的绝对时间-单位s）
     *
     * @param frameInfo 视频帧信息
     */
    private long getFrameInfoTime(byte[] frameInfo) {
        currentVideoTime = ByteUtils.byte4ToIntL(ByteUtils.subBytes(frameInfo, 12, 4));
        return currentVideoTime;
    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        if (audioDecoder != null) {
            audioDecoder.setAudioData(audioBuffer);
        }
    }

    public void init(Surface surface, OnPlayStatusChangedListener statusCallback) {
        isPlaying = false;
        this.statusChangedListener = statusCallback;
        if (videoDecoder == null) {
            videoDecoder = new H264Decoder(surface, 0);
        } else {
            videoDecoder.changeSurface(surface);
        }
    }

    public void init(Surface surface, OnPlayStatusChangedListener statusCallback,
                     OnPlayingListener onPlayingListener) {
        init(surface, statusCallback);
        this.onPlayingListener = onPlayingListener;
    }

    public long getCurrentVideoTime() {
        return currentVideoTime;
    }

    public void initP2pLive() {
        if (iotcClient != null) {
            iotcClient.init();
        }
    }

    public void startPlay() {
        if (iotcClient != null) {
            iotcClient.startPlay(new P2pCmdCallback() {
                @Override
                public void onResponse(int cmd, IotcCmdResp result) {
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    public void pausePlayback(boolean isPaused) {
        iotcClient.pausePlayback(isPaused, new P2pCmdCallback() {
            @Override
            public void onResponse(int cmd, IotcCmdResp result) {

            }

            @Override
            public void onError() {

            }
        });
    }

    public void startDecode() {
        if (videoDecoder != null) {
            videoDecoder.startDecode();
        }
    }

    public void release() {
        if (iotcClient != null) {
            iotcClient.close();
            iotcClient = null;
        }
        if (videoDecoder != null) {
            videoDecoder.release();
            videoDecoder = null;
        }
        if (audioDecoder != null) {
            audioDecoder.stop();
            audioDecoder = null;
        }
    }

    public void stopRunning() {
        if (iotcClient != null) {
            iotcClient.stopLive();
        }
        if (audioDecoder != null) {
            audioDecoder.stopRunning();
        }
    }

    public void setCallback(IOTCClient.StatusCallback statusCallback) {
        if (iotcClient != null) {
            iotcClient.setStatusCallback(statusCallback);
        }
    }

    public IOTCClient getIOTCClient() {
        return iotcClient;
    }

    public interface OnPlayStatusChangedListener {

        void onPlayStarted();

        void onPlayFinished();

        void onPlayFail();

    }

    public interface OnPlayingListener {

        void onPlaying(long time, int flag);

    }

}
