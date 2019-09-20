package com.sunmi.ipc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.ipc.model.IotcCmdReq;
import com.sunmi.ipc.model.IotcCmdResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.P2pCmdCallback;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.utils.ByteUtils;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/8/1.
 */
public class IOTCClient {
    private String TAG = "IOTCClient";

    private static int IOTC_CONNECT_TIMEOUT = 4000;
    private String uid;
    private int SID = -1;
    private int avIndex = -1;
    private int IOTC_CONNECT_RESULT = -1000;
    private int CMD_LIVE_START = 0x10;
    private int CMD_LIVE_STOP = 0x11;
    private int CMD_LIVE_START_AUDIO = 0x12;
    private int CMD_LIVE_STOP_AUDIO = 0x13;
    private int CMD_SD_CARD_STATUS = 0x1E;
    private int CMD_PLAYBACK_LIST = 0x20;
    private int CMD_PLAYBACK_START = 0x21;
    private int CMD_PLAYBACK_STOP = 0x22;
    private int CMD_PLAYBACK_PAUSE = 0x23;

    private Callback callback;
    private boolean alreadyQuit;
    private boolean isRunning = true;
    private Timer timer;

    public IOTCClient(String uid) {
        this.uid = uid;
    }

    public void init() {
        LogCat.e(TAG, "StreamClient init...");
        int ret = IOTCAPIs.IOTC_Initialize2(0);//step 1
        LogCat.e(TAG, "IOTC_Initialize() ret = " + ret);
        if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
            LogCat.e(TAG, "IOTCAPIs_Device exit...!!");
            if (IOTCAPIs.IOTC_ER_ALREADY_INITIALIZED == ret) {
                IOTCAPIs.IOTC_DeInitialize();
            }//todo
            if (callback != null) {
                callback.initFail();
            }
            return;
        }

        // alloc 3 sessions for video and two-way audio
        AVAPIs.avInitialize(3);//step 2

        SID = IOTCAPIs.IOTC_Get_SessionID();//step 3
        if (SID < 0) {
            LogCat.e(TAG, "IOTC_Get_SessionID error code, sid = " + SID);
            IOTCAPIs.IOTC_DeInitialize();
            if (callback != null) {
                callback.initFail();
            }
            return;
        }
        LogCat.e(TAG, "Step 1: call IOTC_Get_SessionID, uid = " + uid);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogCat.e(TAG, "Step 2: call IOTC_Connect_ByUID_Parallel timeout,ret = " + IOTC_CONNECT_RESULT);
                timer.cancel();
                if (alreadyQuit) {
                    return;
                }
                if (IOTC_CONNECT_RESULT < 0) {
                    LogCat.e(TAG, "Step 2: call IOTC_Connect_ByUID_Parallel timeout, quit");
                    IOTCAPIs.IOTC_Connect_Stop_BySID(SID);
                    AVAPIs.avDeInitialize();
                    if (callback != null) {
                        callback.initFail();
                    }
                }
            }
        }, IOTC_CONNECT_TIMEOUT, 1000);

        IOTC_CONNECT_RESULT = IOTCAPIs.IOTC_Connect_ByUID_Parallel(uid, SID);//step 4
        if (IOTC_CONNECT_RESULT < 0 || alreadyQuit) {
            LogCat.e(TAG, "IOTC_Connect_ByUID_Parallel failed ret = " + IOTC_CONNECT_RESULT);
            return;
        }
        LogCat.e(TAG, "Step 2: call IOTC_Connect_ByUID_Parallel, uid = " + uid);

        String account = "admin";
        String password = "12345678";//8Qi0ZLkwv3VP0W
        int timeoutSec = 20;
        int channelId = 1;//chid用来传输音视频
        int[] pservType = new int[100];
        int[] bResend1 = new int[100];

        avIndex = AVAPIs.avClientStart2(SID, account, password,
                timeoutSec, pservType, channelId, bResend1);//step 5
        if (avIndex < 0) {
            LogCat.e(TAG, "avClientStartEx failed avIndex = " + avIndex);
            AVAPIs.avDeInitialize();
            IOTCAPIs.IOTC_Session_Close(SID);
            IOTCAPIs.IOTC_DeInitialize();
            if (callback != null) {
                callback.initFail();
            }
            IOTC_CONNECT_RESULT = -1000;
            return;
        }
        LogCat.e(TAG, "Step 3: call avClientStartEx, avIndex = " + avIndex);
        startPlay();
        Thread videoThread = new Thread(new VideoThread(avIndex), "Video Thread");
        Thread audioThread = new Thread(new AudioThread(avIndex), "Audio Thread");
        videoThread.start();
        audioThread.start();
        try {
            videoThread.join();
        } catch (InterruptedException e) {
            LogCat.e(TAG, "videoThread:" + e.getMessage());
            return;
        }
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            LogCat.e(TAG, "audioThread:" + e.getMessage());
        }
    }

    public void close() {
        if (IOTC_CONNECT_RESULT == -1000) {
            alreadyQuit = true;
            IOTCAPIs.IOTC_Connect_Stop_BySID(SID);
            return;
        }
        if (avIndex < 0) return;
        AVAPIs.avClientStop(avIndex);
        LogCat.e(TAG, "avClientStop OK");
        AVAPIs.avClientExit(SID, 1);
        AVAPIs.avDeInitialize();
        IOTCAPIs.IOTC_Session_Close(SID);
        LogCat.e(TAG, "IOTC_Session_Close OK");
        IOTCAPIs.IOTC_DeInitialize();
        isRunning = false;
        LogCat.e(TAG, "StreamClient exit...");
    }

    /**
     * 开始直播
     */
    public void startPlay() {
        startPlay(new P2pCmdCallback() {
            @Override
            public void onResponse(int cmd, IotcCmdResp result) {
                if (callback != null) {
                    callback.initSuccess();
                }
            }

            @Override
            public void onError() {
                if (callback != null) {
                    callback.initFail();
                }
            }
        });
    }

    /**
     * 开始直播
     */
    public void startPlay(P2pCmdCallback callback) {
        changeValue(0, callback);
    }

    /**
     * 切换分辨率
     *
     * @param type 分辨率，0：超清，1：高清，2：标清
     */
    public void changeValue(int type, P2pCmdCallback callback) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_LIVE_START)
                .setChannel(1)
                .setParam("resolution", type).builder();
        cmdCall(CMD_LIVE_START, cmd, callback);
    }

    /**
     * 切换分辨率
     *
     * @param type 分辨率，0：超清，1：高清，2：标清
     */
    public void changeValue(int type) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_LIVE_START)
                .setChannel(1)
                .setParam("resolution", type).builder();
        cmdCall(cmd);
    }

    /**
     * 停止直播参数
     */
    public void stopLive() {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_LIVE_STOP)
                .setChannel(1).builder();
        cmdCall(cmd);
    }

    public void getPlaybackList(long start, long end, P2pCmdCallback callback) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_LIST)
                .setChannel(1)
                .setParam("start_time", start)
                .setParam("end_time", end).builder();
        cmdCall(CMD_PLAYBACK_LIST, cmd, callback);
    }

    public void startPlayback(long startTime, P2pCmdCallback callback) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_START)
                .setChannel(1)
                .setParam("start_time", startTime).builder();
        cmdCall(CMD_PLAYBACK_START, cmd, callback);
    }

    public void stopPlayback(P2pCmdCallback callback) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_STOP)
                .setChannel(1).builder();
        cmdCall(CMD_PLAYBACK_STOP, cmd, callback);
    }

    public void pausePlayback(boolean isPause, P2pCmdCallback callback) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_PAUSE)
                .setChannel(1)
                .setParam("pause", isPause ? 1 : 0).builder();
        cmdCall(CMD_PLAYBACK_PAUSE, cmd, callback);
    }

    public void stopPlayback() {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_STOP)
                .setChannel(1).builder();
        cmdCall(cmd);
    }

    public void pausePlayback(boolean isPause) {
        IotcCmdReq cmd = new IotcCmdReq.Builder()
                .setMsg_id(Utils.getMsgId())
                .setCmd(CMD_PLAYBACK_PAUSE)
                .setChannel(1)
                .setParam("pause", isPause ? 1 : 0).builder();
        cmdCall(cmd);
    }

    private void cmdCall(final IotcCmdReq cmd) {
        ThreadPool.getSingleThreadPool().execute(() -> {
            String json = new Gson().toJson(cmd);
            byte[] req = json.getBytes();
            IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
            getCmdResponse();
        });
    }

    private void cmdCall(final int cmd, final IotcCmdReq cmdReq, final P2pCmdCallback callback) {
        ThreadPool.getSingleThreadPool().execute(() -> {
            String json = new Gson().toJson(cmdReq);
            byte[] req = json.getBytes();
            LogCat.e(TAG, "99999999 cmdReq = " + cmdReq.toString());
            IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
            getCmdResponse(cmd, callback);
        });
    }

    private void getCmdResponse(int cmd, P2pCmdCallback callback) {
        byte[] buf = new byte[1024];
        int actualLen = IOTCAPIs.IOTC_Session_Read(SID, buf, 1024, 10000, 0);
        if (actualLen > 0) {
            byte[] data = new byte[actualLen];
            System.arraycopy(buf, 0, data, 0, actualLen);
            String result = ByteUtils.byte2String(data);
            LogCat.e(TAG, "99999999 result = " + result);
            try {
                IotcCmdResp cmdBean;
                if (CMD_PLAYBACK_LIST == cmd) {
                    cmdBean = new Gson().fromJson(result,
                            new TypeToken<IotcCmdResp<List<VideoTimeSlotBean>>>() {
                            }.getType());
                } else {
                    cmdBean = new Gson().fromJson(result, IotcCmdResp.class);
                }
                if (callback != null) {
                    callback.onResponse(cmd, cmdBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (callback != null) {
                callback.onError();
            }
        }
    }

    private void getCmdResponse() {
        byte[] buf = new byte[1024];
        int actualLen = IOTCAPIs.IOTC_Session_Read(SID, buf, 1024, 10000, 0);
        if (actualLen > 0) {
            byte[] data = new byte[actualLen];
            System.arraycopy(buf, 0, data, 0, actualLen);
            String result = ByteUtils.byte2String(data);
            IotcCmdResp cmdBean = new Gson().fromJson(result, IotcCmdResp.class);
        }
    }

    public class VideoThread implements Runnable {
        final int VIDEO_BUF_SIZE = 2_000_000;
        final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[VIDEO_BUF_SIZE];
            int[] outBufSize = new int[1];
            int[] outFrameSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            while (isRunning) {
                int[] frameNumber = new int[1];
                int ret = AVAPIs.avRecvFrameData2(avIndex, videoBuffer, VIDEO_BUF_SIZE, outBufSize,
                        outFrameSize, frameInfo, FRAME_INFO_SIZE, outFrmInfoBufSize, frameNumber);
                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {//缓存没数据等待10ms再读
                    try {
                        Thread.sleep(20);
                        continue;
                    } catch (InterruptedException e) {
                        LogCat.e(TAG, e.getMessage());
                        break;
                    }
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    LogCat.e(TAG, "Lost video frame number[%d]" + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                    LogCat.e(TAG, "Incomplete video frame number = " + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    LogCat.e(TAG, "AV_ER_SESSION_CLOSE_BY_REMOTE");
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    LogCat.e(TAG, "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    LogCat.e(TAG, "Session cant be used anymore");
                    break;
                }
//                LogCat.e(TAG, "888888vvv T-" + Process.myTid() + ", VIDEO received = " + ret);
                if (ret > 0) {
                    byte[] data = new byte[ret];
                    System.arraycopy(videoBuffer, 0, data, 0, ret);
                    if (callback != null) callback.onVideoReceived(data);
                }
            }
        }
    }

    public class AudioThread implements Runnable {
        final int AUDIO_BUF_SIZE = 1024 * 4;
        final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        AudioThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (isRunning) {
                int[] frameNumber = new int[1];
                int ret = AVAPIs.avRecvAudioData(avIndex, audioBuffer,
                        AUDIO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE, frameNumber);

                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {//缓存没数据等待10ms再读
                    try {
                        Thread.sleep(10);
                        continue;
                    } catch (InterruptedException e) {
                        LogCat.e(TAG, e.getMessage());
                        break;
                    }
                } else if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    LogCat.e(TAG, "AudioThread - AV_ER_SESSION_CLOSE_BY_REMOTE");
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    LogCat.e(TAG, "AudioThread - AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    LogCat.e(TAG, "AudioThread - AV_ER_INVALID_SID");
                    break;
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    LogCat.e(TAG, "AudioThread - AV_ER_LOSED_THIS_FRAME");
                    continue;
                }
//                LogCat.e(TAG, "888888aaa received ret = " + ret);
                if (ret < 0) return;
                byte[] data = new byte[ret];
                System.arraycopy(audioBuffer, 0, data, 0, ret);
                if (callback != null) callback.onAudioReceived(data);
            }
            LogCat.e(TAG, "AudioThread - Exit");
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void initSuccess();

        void initFail();

        void onVideoReceived(byte[] videoBuffer);

        void onAudioReceived(byte[] audioBuffer);

    }

}
