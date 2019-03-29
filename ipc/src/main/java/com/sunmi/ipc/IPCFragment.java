package com.sunmi.ipc;

import android.media.MediaCodec;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import sunmi.common.base.BaseFragment;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_ipc")
public class IPCFragment extends BaseFragment implements SurfaceHolder.Callback {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "btn_play")
    Button btnPlay;
    @ViewById(resName = "btn_pause")
    Button btnPause;
    @ViewById(resName = "btn_stop")
    Button btnStop;

    private static String UID = "C3YABT1MPRV4BM6GUHXJ";
//    private static String UID = "CRYUBT1WKFV4UM6GUH71";

    private FLVDecoder mPlayer = null;

    private boolean mStopFlag = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            shortTip("播放结束!");
        }
    };

    @AfterViews
    void init() {
        //保持屏幕常亮
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        File f = new File(filePath);
//        if (null == f || !f.exists() || f.length() == 0) {
//            shortTip("指定文件不存在!");
//            return;
//        }
//        try {
//            //获取文件输入流
//            mInputStream = new DataInputStream(new FileInputStream(new File(filePath)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        IOTCClient.setCallback(new IOTCClient.Callback() {
            @Override
            public void onVideoReceived(byte[] videoBuffer) {

                if (mPlayer != null)
                    mPlayer.setVideoData(videoBuffer);
//                startDecodingThread(videoBuffer);
            }

            @Override
            public void onAudioReceived(byte[] audioBuffer) {

            }
        });
        videoView.getHolder().addCallback(this);
//        mSurfaceHolder = videoView.getHolder();
//        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    //通过多媒体格式名创建一个可用的解码器
//                    mCodec = MediaCodec.createDecoderByType("video/avc");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                //初始化编码器
//                final MediaFormat mediaformat = MediaFormat.createVideoFormat("video/avc", Video_Width, Video_Height);
//                //获取h264中的pps及sps数据
//                if (isUsePpsAndSps) {
//                    byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0, (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
//                    byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6, (byte) 229, 1, (byte) 151, (byte) 128};
//                    mediaformat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//                    mediaformat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//                }
//                //设置帧率
//                mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, FrameRate);
//                //https://developer.android.com/reference/android/media/MediaFormat.html#KEY_MAX_INPUT_SIZE
//                //设置配置参数，参数介绍 ：
//                // format   如果为解码器，此处表示输入数据的格式；如果为编码器，此处表示输出数据的格式。
//                //surface   指定一个surface，可用作decode的输出渲染。
//                //crypto    如果需要给媒体数据加密，此处指定一个crypto类.
//                //flags     如果正在配置的对象是用作编码器，此处加上CONFIGURE_FLAG_ENCODE 标签。
//                mCodec.configure(mediaformat, holder.getSurface(), null, 0);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
    }

    @Click(resName = "btn_play")
    void playClick() {
        IOTCClient.start(UID);
//        PLMediaController mMediaController = new PLMediaController(mActivity);
//        videoView.setMediaController(mMediaController);
    }

    @Click(resName = "btn_stop")
    void stopClick() {
        IPCConfigActivity_.intent(mActivity).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer = new FLVDecoder(holder.getSurface(), 0);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭操作
        if (mPlayer != null) {
            mPlayer.stopRunning();
            mPlayer = null;
        }
    }

    private SurfaceHolder mSurfaceHolder;
    private MediaCodec mCodec;
    //    private DataInputStream mInputStream;
//    private String FileName = "test.h264";
    private int Video_Width = 1920;
    private int Video_Height = 1080;
    private int FrameRate = 15;
    private Boolean isUsePpsAndSps = false;
//    private String filePath = Environment.getExternalStorageDirectory() + "/" + FileName;

    //
    private void startDecodingThread(byte[] videoBytes) {
        mCodec.start();
        Thread mDecodeThread = new Thread(new decodeH264Thread(videoBytes));
        mDecodeThread.start();
    }

    /**
     * @author ldm
     * @description 解码线程
     * @time 2016/12/19 16:36
     */
    private class decodeH264Thread implements Runnable {
        byte[] streamBuffer;

        public decodeH264Thread(byte[] videoBytes) {
            this.streamBuffer = videoBytes;
        }

        @Override
        public void run() {
            try {
                decodeLoop();
            } catch (Exception e) {
            }
        }

        private void decodeLoop() {
            //存放目标文件的数据
            ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
            //解码后的数据，包含每一个buffer的元数据信息，例如偏差，在相关解码器中有效的数据大小
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long startMs = System.currentTimeMillis();
            long timeoutUs = 10000;
            byte[] marker0 = new byte[]{0, 0, 0, 1};
            byte[] dummyFrame = new byte[]{0x00, 0x00, 0x01, 0x20};
//            byte[] streamBuffer = null;
//            try {
//                streamBuffer = getBytes(mInputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            int bytes_cnt = 0;
            while (mStopFlag == false) {
                bytes_cnt = streamBuffer.length;
                if (bytes_cnt == 0) {
                    streamBuffer = dummyFrame;
                }

                int startIndex = 0;
                int remaining = bytes_cnt;
                while (true) {
                    if (remaining == 0 || startIndex >= remaining) {
                        break;
                    }
                    int nextFrameStart = KMPMatch(marker0, streamBuffer, startIndex + 2, remaining);
                    if (nextFrameStart == -1) {
                        nextFrameStart = remaining;
                    } else {
                    }

                    int inIndex = mCodec.dequeueInputBuffer(timeoutUs);
                    if (inIndex >= 0) {
                        ByteBuffer byteBuffer = inputBuffers[inIndex];
                        byteBuffer.clear();
                        byteBuffer.put(streamBuffer, startIndex, nextFrameStart - startIndex);
                        //在给指定Index的inputbuffer[]填充数据后，调用这个函数把数据传给解码器
                        mCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
                        startIndex = nextFrameStart;
                    } else {
                        Log.e(TAG, "aaaaa");
                        continue;
                    }

                    int outIndex = mCodec.dequeueOutputBuffer(info, timeoutUs);
                    if (outIndex >= 0) {
                        //帧控制是不在这种情况下工作，因为没有PTS H264是可用的
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        boolean doRender = (info.size != 0);
                        //对outputbuffer的处理完后，调用这个函数把buffer重新返回给codec类。
                        mCodec.releaseOutputBuffer(outIndex, doRender);
                    } else {
                        Log.e(TAG, "bbbb");
                    }
                }
                mStopFlag = true;
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;
        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
//            BufferedOutputStream bos=new BufferedOutputStream(new ByteArrayOutputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        Log.e("ipcfragment", "bbbb");
        return buf;
    }

    private int KMPMatch(byte[] pattern, byte[] bytes, int start, int remain) {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int[] lsp = computeLspTable(pattern);

        int j = 0;  // Number of chars matched in pattern
        for (int i = start; i < remain; i++) {
            while (j > 0 && bytes[i] != pattern[j]) {
                // Fall back in the pattern
                j = lsp[j - 1];  // Strictly decreasing
            }
            if (bytes[i] == pattern[j]) {
                // Next char matched, increment position
                j++;
                if (j == pattern.length)
                    return i - (j - 1);
            }
        }
        return -1;  // Not found
    }

    private int[] computeLspTable(byte[] pattern) {
        int[] lsp = new int[pattern.length];
        lsp[0] = 0;  // Base case
        for (int i = 1; i < pattern.length; i++) {
            // Start by assuming we're extending the previous LSP
            int j = lsp[i - 1];
            while (j > 0 && pattern[i] != pattern[j])
                j = lsp[j - 1];
            if (pattern[i] == pattern[j])
                j++;
            lsp[i] = j;
        }
        return lsp;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        /* p2p */
//        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(mClientListener);
//        TUTKP2P.TK_getInstance().TK_client_disConnectAll();
//
//    }
//
//    private OnP2PClientListener mClientListener = new OnP2PClientListener() {
//        @Override
//        public void receiveConnectInfo(String uid, int sid, int state) {
////			Log.i(TAG, " IOTC_Connect_ByUID_Parallel = " + i);
//        }
//
//        @Override
//        public void receiveClientStartInfo(String uid, int avIndex, int state) {
//            if (state == TUTKP2P.CONNECTION_STATE_CONNECTED) {
//                shortTip("连接成功 avNewClientStart = " + avIndex);
//            } else if (state == TUTKP2P.CONNECTION_STATE_CONNECT_FAILED) {
//                shortTip("连接失败 avNewClientStart = " + avIndex);
//            }
//        }
//
//        @Override
//        public void receiveSessionCheckInfo(String uid, St_SInfo info, int result) {
//            if (result == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE
//                    || result == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT) {
//                shortTip("对方掉线");
//                mActivity.finish();
//            }
//        }
//
//        @Override
//        public void receiveStatusCheckInfo(String uid, St_AvStatus status, int result) {
//            if (result == P2PUtils.STATUS_CHECK_BLOCK) {
//                shortTip("网络阻塞 延时严重");
//            }
//        }
//
//        @Override
//        public void receiveIOCtrlDataInfo(String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {
//            //收到设备回传的command
//            shortTip("收到command " + Integer.toHexString(avIOCtrlMsgType));
//        }
//
//        @Override
//        public void sendIOCtrlDataInfo(String uid, int avChannel, int avIOCtrlMsgType, int result, byte[] data) {
//
//        }
//
//        @Override
//        public void receiveVideoInfo(String uid, int avChannel, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {
//            shortTip("收到视频数据");
//        }
//
//        @Override
//        public void receiveAudioInfo(String uid, int avChannel, byte[] audioData, long timeStamp, int number) {
//            shortTip("收到音频数据");
//        }
//    };
//
//
//    public void connect() {
//        //连接uid
////        EditText edit_uid = findViewById(R.id.edit_uid);
////        mUID = edit_uid.getText().toString().trim();
//        TUTKP2P.TK_getInstance().TK_client_connect(mUID, "12345678", TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void disconnect(View view) {
//        //断线uid
//        TUTKP2P.TK_getInstance().TK_client_disConnect(mUID);
//    }
//
//    public void startSendVideo(View view) {
//        //开始发送视频数据
//        TUTKP2P.TK_getInstance().TK_client_startSendVideo(mUID, TUTKP2P.DEFAULT_CHANNEL);
//        //模拟发送数据
//        TUTKP2P.TK_getInstance().TK_client_onSendVideoData(mUID, TUTKP2P.DEFAULT_CHANNEL, new byte[]{0, 0, 0, 0}, true, System.currentTimeMillis());
//    }
//
//    public void stopSendVideo(View view) {
//        //停止发送视频数据
//        TUTKP2P.TK_getInstance().TK_client_stopSendVideo(mUID, TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void startReceiveVideo(View view) {
//        //开始接收视频数据
//        TUTKP2P.TK_getInstance().TK_client_startReceiveVideo(mUID, TUTKP2P.DEFAULT_CHANNEL, false);
//    }
//
//    public void stopReceiveVideo(View view) {
//        //停止接收视频数据
//        TUTKP2P.TK_getInstance().TK_client_stopReceiveVideo(mUID, TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void startSpeak(View view) {
//        //开始发送音频数据
//        TUTKP2P.TK_getInstance().TK_client_startSpeaking(mUID, TUTKP2P.DEFAULT_CHANNEL);
//        //模拟发送数据
//        TUTKP2P.TK_getInstance().TK_client_onSendAudioData(mUID, TUTKP2P.DEFAULT_CHANNEL, new byte[]{0, 0, 0, 0}, 4, System.currentTimeMillis());
//    }
//
//    public void stopSpeak(View view) {
//        //停止发送音频数据
//        TUTKP2P.TK_getInstance().TK_client_stopSpeaking(mUID, TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void startListener(View view) {
//        //开始接收音频数据
//        TUTKP2P.TK_getInstance().TK_client_startListener(mUID, TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void stopListener(View view) {
//        //停止接收音频数据
//        TUTKP2P.TK_getInstance().TK_client_stopReceiveVideo(mUID, TUTKP2P.DEFAULT_CHANNEL);
//    }
//
//    public void sendIOCtrl(View view) {
//        //开始发送自定义command
//        TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(
//                mUID,
//                TUTKP2P.DEFAULT_CHANNEL,
//                0x0900,
//                new byte[]{0, 0, 0, 0});
//    }


}
