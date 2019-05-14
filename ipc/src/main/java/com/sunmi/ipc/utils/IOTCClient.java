package com.sunmi.ipc.utils;

import android.util.Log;

import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.utils.log.LogCat;

/**
 * iotc
 */
public class IOTCClient {

    private static Callback callback;
    private static int SID = -1;

    public static void start(String uid) {
        LogCat.e("IOTCClient", "StreamClient start...");

        int ret = IOTCAPIs.IOTC_Initialize2(0);
        Log.e("IOTCClient", "IOTC_Initialize() ret = " + ret);
        if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
            Log.e("IOTCClient", "IOTCAPIs_Device exit...!!");
            return;
        }

        // alloc 3 sessions for video and two-way audio
        AVAPIs.avInitialize(3);

        SID = IOTCAPIs.IOTC_Get_SessionID();
        if (SID < 0) {
            Log.e("IOTCClient", "IOTC_Get_SessionID error code, sid = " + SID);
            return;
        }
        Log.e("IOTCClient", "Step 1: call IOTC_Get_SessionID, uid = " + uid);
        ret = IOTCAPIs.IOTC_Connect_ByUID_Parallel(uid, SID);
        if (ret < 0) {
            Log.e("IOTCClient", "IOTC_Connect_ByUID_Parallel failed ret = " + ret);
            return;
        }
        Log.e("IOTCClient", "Step 2: call IOTC_Connect_ByUID_Parallel, uid = " + uid);

        String account = "admin";
        String password = "12345678";
        int timeoutSec = 20;
        int channelId = 1;
        int[] pservType = new int[100];
        int[] bResend1 = new int[100];

        int avIndex = AVAPIs.avClientStart2(SID, account, password,
                timeoutSec, pservType, channelId, bResend1);//chid用来传输音视频
        if (avIndex < 0) {
            Log.e("IOTCClient", "avClientStartEx failed avIndex = " + avIndex);
            return;
        }
        Log.e("IOTCClient", "Step 3: call avClientStartEx, avIndex = " + avIndex);
        startPlay();
//        if (startIpcamStream(avIndex)) {
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
//        }

        AVAPIs.avClientStop(avIndex);
        Log.e("IOTCClient", "avClientStop OK");
        IOTCAPIs.IOTC_Session_Close(SID);
        Log.e("IOTCClient", "IOTC_Session_Close OK");
        AVAPIs.avDeInitialize();
        IOTCAPIs.IOTC_DeInitialize();
        Log.e("IOTCClient", "StreamClient exit...");
    }

    private static boolean startIpcamStream(int avIndex) {
        AVAPIs av = new AVAPIs();
        int ret = 0;
        //avClient(AP) change time interval of sending packets by avSendFrameData(avServer)
//        int IOTYPE_INNER_SND_DATA_DELAY = 0xFF;
//         ret = av.avSendIOCtrl(avIndex, IOTYPE_INNER_SND_DATA_DELAY, new byte[2], 2);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        // This IOTYPE constant and its corrsponsing data structure is defined in
        // Sample/Linux/Sample_AVAPIs/AVIOCTRLDEFs.h

        Log.e("IOTCClient", "666666 start_ipcam_stream  start, " + ret);
//        int IOTYPE_USER_IPCAM_START = 0x1FF;
//        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_START, new byte[8], 8);
        Log.e("IOTCClient", "666666 start_ipcam_stream  end, " + ret);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

//        int IOTYPE_USER_IPCAM_AUDIOSTART = 0x300;
//        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_AUDIOSTART, new byte[8], 8);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }
        return true;
    }

    public static void startPlay() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg_id", "1000");
            JSONArray array = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("cmd", 1);
            item.put("channel", 1);
            JSONObject param = new JSONObject();
            param.put("resolution", 0);
            item.put("param", param);
            array.put(item);
            jsonObject.put("params", array);
            String json = jsonObject.toString();
            byte[] req = json.getBytes();
            IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        byte[] req = "{\"msg_id\":\"1000\",\"params\":[{\"cmd\":1,\"channel\":1\"param\":{\"resolution\":0}}]}".getBytes();
    }

    public static void stopVideo() {
        byte[] req = "{\"id\":1000,\"cmd\":2,\"param\":{\"channel\":1}}".getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 1);
    }

    public static void stopAudio() {
        byte[] req = "{\"id\":1000,\"cmd\":4,\"param\":{\"channel\":1}}".getBytes();
        IOTCAPIs.IOTC_Session_Write(SID, req, req.length, 0);
    }

    public static class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 1024 * 1024;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            Log.e("IOTCClient", Thread.currentThread().getName() + " VideoThread Start");
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
                        LogCat.e("IOTCClient", e.getMessage());
                        break;
                    }
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    Log.e("IOTCClient", "Lost video frame number[%d]" + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                    Log.e("IOTCClient", "Incomplete video frame number = " + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    Log.e("IOTCClient", "AV_ER_SESSION_CLOSE_BY_REMOTE");
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    Log.e("IOTCClient", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    Log.e("IOTCClient", "Session cant be used anymore");
                    break;
                }
                if (callback != null) callback.onVideoReceived(videoBuffer);
//                Log.e("IOTCClient", "555555vvv VIDEO received ret = " + ret);
            }
            Log.e("IOTCClient", Thread.currentThread().getName() + " VideoThread Start");
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
            Log.e("IOTCClient", Thread.currentThread().getName() + " AudioThread Start");

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (true) {
                int ret = av.avCheckAudioBuf(avIndex);

                if (ret < 0) {// Same error codes as below
                    Log.e("IOTCClient", Thread.currentThread().getName()
                            + " avCheckAudioBuf(),failed: = " + ret);
                    break;
                } else if (ret < 3) {
                    try {
                        Thread.sleep(120);
                        continue;
                    } catch (InterruptedException e) {
                        LogCat.e("IOTCClient", e.getMessage());
                        break;
                    }
                }

                int[] frameNumber = new int[1];
                ret = av.avRecvAudioData(avIndex, audioBuffer,
                        AUDIO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE, frameNumber);
                LogCat.e("IOTCClient", "666666 ret = " + ret);

                if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    Log.e("IOTCClient", Thread.currentThread().getName() + " AV_ER_SESSION_CLOSE_BY_REMOTE");
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    Log.e("IOTCClient", Thread.currentThread().getName() + " AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    Log.e("IOTCClient", Thread.currentThread().getName() + " AV_ER_INVALID_SID");
                    break;
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    Log.e("IOTCClient", Thread.currentThread().getName() + " AV_ER_LOSED_THIS_FRAME");
                    continue;
                }
                if (ret < 0) return;
                byte[] data = new byte[ret];
                System.arraycopy(audioBuffer, 0, data, 0, ret);
                if (callback != null) callback.onAudioReceived(data);
//                Log.e("IOTCClient", "555555aaa AUDIO received ret = " + ret);
            }
            Log.e("IOTCClient", Thread.currentThread().getName() + "  Exit");
        }
    }

    public static void setCallback(Callback callback) {
        IOTCClient.callback = callback;
    }

    public interface Callback {
        void onVideoReceived(byte[] videoBuffer);

        void onAudioReceived(byte[] audioBuffer);
    }

}
