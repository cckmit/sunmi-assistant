package com.sunmi.ipc;

import android.util.Log;

import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_AVClientStartInConfig;
import com.tutk.IOTC.St_AVClientStartOutConfig;

import java.io.DataInputStream;

import sunmi.common.utils.log.LogCat;

/**
 *
 */
public class IOTCClient {
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

        int sid = IOTCAPIs.IOTC_Get_SessionID();
        if (sid < 0) {
            Log.e("IOTCClient", "IOTC_Get_SessionID error code, sid = " + sid);
            return;
        }
        ret = IOTCAPIs.IOTC_Connect_ByUID_Parallel(uid, sid);
        Log.e("IOTCClient", "Step 2: call IOTC_Connect_ByUID_Parallel, uid = " + uid);

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

        avIndex = AVAPIs.avClientStart2(sid, "admin", "12345678",
                20, pservType, 1, bResend1);
        bResend = av_client_out_config.resend;
        srvType = av_client_out_config.server_type;
        Log.e("IOTCClient", "Step 2: call avClientStartEx, avIndex = " + avIndex);

        if (avIndex < 0) {
            Log.e("IOTCClient", "avClientStartEx failed avIndex = " + avIndex);
            return;
        }

        if (startIpcamStream(avIndex)) {
            Thread videoThread = new Thread(new VideoThread(avIndex), "Video Thread");
            Thread audioThread = new Thread(new AudioThread(avIndex), "Audio Thread");
            videoThread.start();
            audioThread.start();
            try {
                videoThread.join();
            } catch (InterruptedException e) {
                LogCat.e("IOTCClient", e.getMessage());
                return;
            }
            try {
                audioThread.join();
            } catch (InterruptedException e) {
                LogCat.e("IOTCClient", e.getMessage());
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
        int ret = av.avSendIOCtrl(avIndex, AVAPIs.IOTYPE_INNER_SND_DATA_DELAY, new byte[2], 2);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        // This IOTYPE constant and its corrsponsing data structure is defined in
        // Sample/Linux/Sample_AVAPIs/AVIOCTRLDEFs.h
        //
        int IOTYPE_USER_IPCAM_START = 0x1FF;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_START, new byte[8], 8);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }

        int IOTYPE_USER_IPCAM_AUDIOSTART = 0x300;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_AUDIOSTART, new byte[8], 8);
        if (ret < 0) {
            Log.e("IOTCClient", "start_ipcam_stream failed[%d]" + ret);
            return false;
        }
        return true;
    }

    public static class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 200000;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public VideoThread(int avIndex) {
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
                int ret = av.avRecvFrameData2(avIndex, videoBuffer,
                        VIDEO_BUF_SIZE, outBufSize, outFrameSize,
                        frameInfo, FRAME_INFO_SIZE,
                        outFrmInfoBufSize, frameNumber);
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
                Log.e("IOTCClient", "333333 received video ret = " + ret);
            }
            Log.e("IOTCClient", Thread.currentThread().getName() + " VideoThread Start");
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
                    //Log.e("IOTCClient","[%s] Audio frame losed",
                    //        Thread.currentThread().getName());
                    continue;
                }
                if (callback != null) callback.onAudioReceived(audioBuffer);
                Log.e("IOTCClient", "333333 received video ret = " + ret);
            }

            Log.e("IOTCClient", Thread.currentThread().getName() + "  Exit");
        }
    }

    public static void setCallback(Callback callback) {
        IOTCClient.callback = callback;
    }

    private static Callback callback;

    public interface Callback {
        void onVideoReceived(byte[] videoBuffer);

        void onAudioReceived(byte[] audioBuffer);
    }

    public void initCodec(DataInputStream mInputStream) {
//        MediaCodec mCodec;
//        try {
//            //通过多媒体格式名创建一个可用的解码器
//            mCodec = MediaCodec.createDecoderByType("video/avc");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //初始化编码器
//        final MediaFormat mediaformat = MediaFormat.createVideoFormat("video/avc", 164, 164);
//        //获取h264中的pps及sps数据
//        if (isUsePpsAndSps) {
//            byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0, (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
//            byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6, (byte) 229, 1, (byte) 151, (byte) 128};
//            mediaformat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//            mediaformat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//        }
//        //设置帧率
//        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
//        mCodec.configure(mediaformat, mSurface, null, 0);
    }
//    public class decodeH264Thread implements Runnable {
//        @Override
//        public void run() {
//            try {
//                decodeLoop();
//            } catch (Exception e) {
//            }
//        }
//
//        private void decodeLoop() {
//            //存放目标文件的数据
//            ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
//            //解码后的数据，包含每一个buffer的元数据信息，例如偏差，在相关解码器中有效的数据大小
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//            long startMs = System.currentTimeMillis();
//            long timeoutUs = 10000;
//            byte[] marker0 = new byte[]{0, 0, 0, 1};
//            byte[] dummyFrame = new byte[]{0x00, 0x00, 0x01, 0x20};
//            byte[] streamBuffer = null;
//            try {
//                streamBuffer = getBytes(mInputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int bytes_cnt = 0;
//            while (mStartFlag == true) {
//                bytes_cnt = streamBuffer.length;
//                if (bytes_cnt == 0) {
//                    streamBuffer = dummyFrame;
//                }
//
//                int startIndex = 0;
//                int remaining = bytes_cnt;
//                while (true) {
//                    if (remaining == 0 || startIndex >= remaining) {
//                        break;
//                    }
//                    int nextFrameStart = KMPMatch(marker0, streamBuffer, startIndex + 2, remaining);
//                    if (nextFrameStart == -1) {
//                        nextFrameStart = remaining;
//                    } else {
//                    }
//
//                    int inIndex = mCodec.dequeueInputBuffer(timeoutUs);
//                    if (inIndex >= 0) {
//                        ByteBuffer byteBuffer = inputBuffers[inIndex];
//                        byteBuffer.clear();
//                        byteBuffer.put(streamBuffer, startIndex, nextFrameStart - startIndex);
//                        //在给指定Index的inputbuffer[]填充数据后，调用这个函数把数据传给解码器
//                        mCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
//                        startIndex = nextFrameStart;
//                    } else {
//                        continue;
//                    }
//
//                    int outIndex = mCodec.dequeueOutputBuffer(info, timeoutUs);
//                    if (outIndex >= 0) {
//                        //帧控制是不在这种情况下工作，因为没有PTS H264是可用的
//                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        boolean doRender = (info.size != 0);
//                        //对outputbuffer的处理完后，调用这个函数把buffer重新返回给codec类。
//                        mCodec.releaseOutputBuffer(outIndex, doRender);
//                    }
//                }
//                mStartFlag = false;
//                mHandler.sendEmptyMessage(0);
//            }
//        }
//    }

}
