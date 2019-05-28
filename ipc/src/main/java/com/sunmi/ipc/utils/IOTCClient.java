package com.sunmi.ipc.utils;

import com.google.gson.Gson;
import com.sunmi.ipc.model.IotcCmdBean;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.utils.ByteUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * iotc
 */
public class IOTCClient {
    private static String TAG = "IOTCClient";

    private static Callback callback;
    private static int SID = -1;
    private static int avIndex = -1;
    private static int CMD_LIVE_START = 0x10;
    private static int CMD_LIVE_STOP = 0x11;
    private static int CMD_LIVE_START_AUDIO = 0x12;
    private static int CMD_LIVE_STOP_AUDIO = 0x13;
    private static int CMD_PLAYBACK_LIST = 0x20;
    private static int CMD_PLAYBACK_START = 0x21;
    private static int CMD_PLAYBACK_STOP = 0x22;
    private static int CMD_PLAYBACK_PAUSE = 0x23;


    public static void init(String uid) {
        LogCat.e(TAG, "StreamClient init...");
        int ret = IOTCAPIs.IOTC_Initialize2(0);
        LogCat.e(TAG, "IOTC_Initialize() ret = " + ret);
        if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
            LogCat.e(TAG, "IOTCAPIs_Device exit...!!");
            return;
        }

        // alloc 3 sessions for video and two-way audio
        AVAPIs.avInitialize(3);

        SID = IOTCAPIs.IOTC_Get_SessionID();
        if (SID < 0) {
            LogCat.e(TAG, "IOTC_Get_SessionID error code, sid = " + SID);
            return;
        }
        LogCat.e(TAG, "Step 1: call IOTC_Get_SessionID, uid = " + uid);
        ret = IOTCAPIs.IOTC_Connect_ByUID_Parallel(uid, SID);
        if (ret < 0) {
            LogCat.e(TAG, "IOTC_Connect_ByUID_Parallel failed ret = " + ret);
            return;
        }
        LogCat.e(TAG, "Step 2: call IOTC_Connect_ByUID_Parallel, uid = " + uid);

        String account = "admin";
        String password = "12345678";
        int timeoutSec = 20;
        int channelId = 1;
        int[] pservType = new int[100];
        int[] bResend1 = new int[100];

        avIndex = AVAPIs.avClientStart2(SID, account, password,
                timeoutSec, pservType, channelId, bResend1);//chid用来传输音视频
        if (avIndex < 0) {
            LogCat.e(TAG, "avClientStartEx failed avIndex = " + avIndex);
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
            LogCat.e("IOTCClient - videoThread:", e.getMessage());
            return;
        }
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            LogCat.e("IOTCClient - audioThread:", e.getMessage());
            return;
        }
        close();
    }

    public static void close() {
        if (avIndex < 0) return;
        AVAPIs.avClientStop(avIndex);
        LogCat.e(TAG, "avClientStop OK");
        IOTCAPIs.IOTC_Session_Close(SID);
        LogCat.e(TAG, "IOTC_Session_Close OK");
        AVAPIs.avDeInitialize();
        IOTCAPIs.IOTC_DeInitialize();
        LogCat.e(TAG, "StreamClient exit...");
    }

    /**
     * 开始直播
     */
    public static void startPlay() {
        changeValue(0);
    }

    /**
     * 切换分辨率
     *
     * @param type 分辨率，0：超清，1：高清，2：标清
     */
    public static void changeValue(int type) {
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_LIVE_START, 1,
                new HashMap<>().put("resolution", type));
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 changeValue json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    /**
     * 停止直播参数
     */
    public static void stopLive() {
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_LIVE_STOP, 1, new Object());
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 StopLive json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    public static void getPlaybackList(long start, long end) {
        Map<String, Object> param = new HashMap<>();
        param.put("start_time", start);
        param.put("end_time", end);
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_PLAYBACK_LIST, 1, param);
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 getPlaybackList json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    public static void startPlayback(long startTime) {
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_PLAYBACK_START, 1,
                new HashMap<>().put("start_time", startTime));
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 startPlayback json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    public static void stopPlayback() {
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_PLAYBACK_STOP, 1, new Object());
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 stopPlayback json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    public static void pausePlayback(boolean isPause) {
        IotcCmdBean cmd = new IotcCmdBean(Utils.getMsgId(), CMD_PLAYBACK_PAUSE, 1,
                new HashMap<>().put("pause", isPause ? 1 : 0));
        String json = new Gson().toJson(cmd);
        LogCat.e(TAG, "111111 pausePlayback json = " + json);
        byte[] req = json.getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
        getCmdResponse();
    }

    private static void getCmdResponse() {
        byte[] buf = new byte[1024];
        int actualLen = IOTCAPIs.IOTC_Session_Read(SID, buf, 1024, 10000, 0);
        if (actualLen > 0) {
            byte[] data = new byte[actualLen];
            System.arraycopy(buf, 0, data, 0, actualLen);
            String result = ByteUtils.byte2String(data);
            if (callback != null) callback.IOTCResult(result);
            LogCat.e(TAG, "111111 getCmdResponse data = " + ByteUtils.byte2String(data));
        }
    }

    public static class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 2000000;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            LogCat.e(TAG, Thread.currentThread().getName() + " VideoThread Start");
            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[VIDEO_BUF_SIZE];
            int[] outBufSize = new int[1];
            int[] outFrameSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            while (true) {
                int[] frameNumber = new int[1];
                int ret = av.avRecvFrameData2(avIndex, videoBuffer, VIDEO_BUF_SIZE, outBufSize,
                        outFrameSize, frameInfo, FRAME_INFO_SIZE, outFrmInfoBufSize, frameNumber);
                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {
                    try {
                        Thread.sleep(30);
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
//                LogCat.e(TAG, "555555vvv VIDEO received ret = " + ret);
                if (ret < 0) return;
                byte[] data = new byte[ret];
                System.arraycopy(videoBuffer, 0, data, 0, ret);
                if (callback != null) callback.onVideoReceived(data);
            }
        }
    }

    public static class AudioThread implements Runnable {
        static final int AUDIO_BUF_SIZE = 1024 * 4;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        AudioThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            LogCat.e(TAG, Thread.currentThread().getName() + " AudioThread Start");

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (true) {
                int ret = av.avCheckAudioBuf(avIndex);

                if (ret < 0) {// Same error codes as below
                    LogCat.e(TAG, "AudioThread - avCheckAudioBuf(),failed: = " + ret);
                    break;
                } else if (ret < 3) {
                    try {
                        Thread.sleep(120);
                        continue;
                    } catch (InterruptedException e) {
                        LogCat.e(TAG, e.getMessage());
                        break;
                    }
                }

                int[] frameNumber = new int[1];
                ret = av.avRecvAudioData(avIndex, audioBuffer,
                        AUDIO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE, frameNumber);
                if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
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
//                LogCat.e(TAG, "555555aaa AUDIO received ret = " + ret);
                if (ret < 0) return;
                byte[] data = new byte[ret];
                System.arraycopy(audioBuffer, 0, data, 0, ret);
                if (callback != null) callback.onAudioReceived(data);
            }
            LogCat.e(TAG, "AudioThread - Exit");
        }
    }

    public static void setCallback(Callback callback) {
        IOTCClient.callback = callback;
    }

    public interface Callback {
        void onVideoReceived(byte[] videoBuffer);

        void onAudioReceived(byte[] audioBuffer);

        void IOTCResult(String result);
    }

}
