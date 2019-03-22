package com.sunmi.ipc;

import android.util.Log;

import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AVClientStartInConfig;
import com.tutk.IOTC.St_AVClientStartOutConfig;

/**
 * IOTCClient.start(MainActivity.this.UID);
 */
public class IOTCClient {
    public static void start(String uid) {

        System.out.println("StreamClient start...");

        int ret = IOTCAPIs.IOTC_Initialize2(0);
        Log.e("IOTCClient", "IOTC_Initialize() ret = %d" + ret);
        if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
            Log.e("IOTCClient", "IOTCAPIs_Device exit...!!");
            return;
        }

        // alloc 3 sessions for video and two-way audio
        AVAPIs.avInitialize(3);

        int sid = IOTCAPIs.IOTC_Get_SessionID();
        if (sid < 0) {
            Log.e("IOTCClient", "IOTC_Get_SessionID error code [%d]" + sid);
            return;
        }
        ret = IOTCAPIs.IOTC_Connect_ByUID_Parallel(uid, sid);
        Log.e("IOTCClient", "Step 2: call IOTC_Connect_ByUID_Parallel(%s)......." + uid);

        int srvType = 0;
        int bResend = 0;
        int avIndex = 0;
        St_AVClientStartInConfig av_client_in_config = new St_AVClientStartInConfig();
        St_AVClientStartOutConfig av_client_out_config = new St_AVClientStartOutConfig();

        av_client_in_config.iotc_session_id = sid;
        av_client_in_config.iotc_channel_id = 0;
        av_client_in_config.timeout_sec = 20;
        av_client_in_config.account_or_identity = "admin";
        av_client_in_config.password_or_token = "12345678";
        av_client_in_config.resend = 0;
        av_client_in_config.security_mode = 1; //enable DTLS
        av_client_in_config.auth_type = 0;
        int[] pservType = new int[100];
        int[] bResend1 = new int[100];

//	public native static int  avClientStart2(int nSID,String viewAcc,String viewPwd, int timeout_sec,int[]pservType,int ChID, int[] bResend);
        avIndex = AVAPIs.avClientStart2(sid, "admin", "12345678", 20, pservType, 1, bResend1);
        bResend = av_client_out_config.resend;
        srvType = av_client_out_config.server_type;
        Log.e("IOTCClient", "Step 2: call avClientStartEx(%d)......." + avIndex);

        if (avIndex < 0) {
            Log.e("IOTCClient", "avClientStartEx failed[%d]" + avIndex);
            return;
        }

        if (startIpcamStream(avIndex)) {
            Thread videoThread = new Thread(new VideoThread(avIndex),
                    "Video Thread");
            Thread audioThread = new Thread(new AudioThread(avIndex),
                    "Audio Thread");
            videoThread.start();
            audioThread.start();
            try {
                videoThread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return;
            }
            try {
                audioThread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        AVAPIs.avClientStop(avIndex);
        Log.e("IOTCClient", "avClientStop OK");
        IOTCAPIs.IOTC_Session_Close(sid);
        Log.e("IOTCClient", "IOTC_Session_Close OK");
        AVAPIs.avDeInitialize();
        IOTCAPIs.IOTC_DeInitialize();
        Log.e("IOTCClient", "StreamClient exit...");
    }

    public static boolean startIpcamStream(int avIndex) {
        AVAPIs av = new AVAPIs();
        int ret = av.avSendIOCtrl(avIndex, AVAPIs.IOTYPE_INNER_SND_DATA_DELAY,
                new byte[2], 2);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        // This IOTYPE constant and its corrsponsing data structure is defined in
        // Sample/Linux/Sample_AVAPIs/AVIOCTRLDEFs.h
        //
        int IOTYPE_USER_IPCAM_START = 0x1FF;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_START,
                new byte[8], 8);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        int IOTYPE_USER_IPCAM_AUDIOSTART = 0x300;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_AUDIOSTART,
                new byte[8], 8);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        return true;
    }

    public static class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 100000;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            Log.e("IOTCClient", "[%s] Start" +
                    Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[VIDEO_BUF_SIZE];
            int[] outBufSize = new int[1];
            int[] outFrameSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            while (true) {
                int[] frameNumber = new int[1];
                int ret = av.avRecvFrameData2(avIndex, videoBuffer,
                        VIDEO_BUF_SIZE, outBufSize, outFrameSize,
                        frameInfo, FRAME_INFO_SIZE,
                        outFrmInfoBufSize, frameNumber);
                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {
                    try {
                        Thread.sleep(30);
                        continue;
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    Log.e("IOTCClient", "[%s] Lost video frame number[%d]" +
                            Thread.currentThread().getName() + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                    Log.e("IOTCClient", "[%s] Incomplete video frame number[%d]" +
                            Thread.currentThread().getName() + frameNumber[0]);
                    continue;
                } else if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    Log.e("IOTCClient", "[%s] AV_ER_SESSION_CLOSE_BY_REMOTE" +
                            Thread.currentThread().getName());
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    Log.e("IOTCClient", "[%s] AV_ER_REMOTE_TIMEOUT_DISCONNECT" +
                            Thread.currentThread().getName());
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    Log.e("IOTCClient", "[%s] Session cant be used anymore" +
                            Thread.currentThread().getName());
                    break;
                }
                Log.e("IOTCClient", "333333 received video ret = " + ret);
            }

            Log.e("IOTCClient", "[%s] Exit" +
                    Thread.currentThread().getName());
        }
    }

    public static class AudioThread implements Runnable {
        static final int AUDIO_BUF_SIZE = 1024;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public AudioThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            Log.e("IOTCClient", "[%s] Start" +
                    Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (true) {
                int ret = av.avCheckAudioBuf(avIndex);

                if (ret < 0) {
                    // Same error codes as below
                    Log.e("IOTCClient", "[%s] avCheckAudioBuf() failed: %d" +
                            Thread.currentThread().getName() + ret);
                    break;
                } else if (ret < 3) {
                    try {
                        Thread.sleep(120);
                        continue;
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }

                int[] frameNumber = new int[1];
                ret = av.avRecvAudioData(avIndex, audioBuffer,
                        AUDIO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE,
                        frameNumber);

                if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    Log.e("IOTCClient", "[%s] AV_ER_SESSION_CLOSE_BY_REMOTE" +
                            Thread.currentThread().getName());
                    break;
                } else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    Log.e("IOTCClient", "[%s] AV_ER_REMOTE_TIMEOUT_DISCONNECT" +
                            Thread.currentThread().getName());
                    break;
                } else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    Log.e("IOTCClient", "[%s] Session cant be used anymore" +
                            Thread.currentThread().getName());
                    break;
                } else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    //Log.e("IOTCClient","[%s] Audio frame losed",
                    //        Thread.currentThread().getName());
                    continue;
                }

                Log.e("IOTCClient", "333333 received video ret = " + ret);
            }

            Log.e("IOTCClient", "[%s] Exit" + Thread.currentThread().getName());
        }
    }
}
