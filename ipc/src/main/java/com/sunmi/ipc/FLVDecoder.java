package com.sunmi.ipc;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sunmi.common.utils.ThreadPool;

/**
 * Description:
 * Created by bruce on 2019/3/25.
 */
public class FLVDecoder {
    //处理音视频的编解码的类MediaCodec
    private MediaCodec video_decoder;
    //显示画面的Surface
    private Surface surface;
    // 0: live, 1: playback, 2: local file
    private int state = 0;
    //视频数据
    private BlockingQueue<byte[]> video_data_Queue = new ArrayBlockingQueue<>(10000);
    //音频数据
    private BlockingQueue<byte[]> audio_data_Queue = new ArrayBlockingQueue<>(10000);

    private boolean isReady = false;
    private int fps = 0;

    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    private int frameCount = 0;
    private long deltaTime = 0;
    private long counterTime = System.currentTimeMillis();
    private boolean isRuning = false;

    public FLVDecoder(Surface surface, int playerState) {
        this.surface = surface;
        this.state = playerState;
//        try {
//            initial(new byte[11]);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void stopRunning() {
        video_data_Queue.clear();
        audio_data_Queue.clear();
    }

    private byte[] h264Header = {0x00, 0x00, 0x00, 0x01};

    //添加视频数据
    public void setVideoData(byte[] data) {
        try {
            //第13位标识是视频还是头
            boolean isAVC = (data[12] & 0xFF) != 0;
            if (isAVC) {//视频数据
                byte[] videoData = new byte[4 + data.length - 20];
                System.arraycopy(h264Header, 0, videoData, 0, h264Header.length);
                System.arraycopy(data, 20, videoData, h264Header.length, data.length - 20);
                video_data_Queue.put(videoData);
            } else {//头信息
                decodeHeader(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加音频数据
    public void setAudioData(byte[] data) {
        try {
            audio_data_Queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public int getFPS() {
        return fps;
    }

    private int frameRate = 30;

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    //<09000024 00000000 00000017 00000000 0142002a ffe10010 6742002a 9da81e00 89f966e0 20202040 01000468 ce3c8000 00002f>

    /**
     * 将byte数组转换为int数据
     */
    public static int byteToInt21(byte[] b) {
        return (((int) b[0]) << 8) + b[1];
    }

    public static int byteToInt2(byte[] ary) {
        int value;
        value = (int) ((ary[0] & 0xFF)
                | ((ary[1] << 8) & 0xFF00));
        return value;
    }

    public void decodeHeader(byte[] data) throws IOException {
        //初始化编码器
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 1920, 1920);

        //获取h264中的pps及sps数据
        int spsLen = byteToInt2(new byte[]{data[21], data[22]});
        byte[] spsHeader = new byte[spsLen + 4];
        System.arraycopy(h264Header, 0, spsHeader, 0, h264Header.length);
        System.arraycopy(data, 23, spsHeader, h264Header.length, spsLen);
        byte[] ppsHeader = new byte[spsLen + 4];
        int ppsLen = byteToInt2(new byte[]{data[spsLen + 23], data[spsLen + 23 + 1]});
        System.arraycopy(h264Header, 0, ppsHeader, 0, h264Header.length);
        System.arraycopy(data, spsLen + 23 + 2, ppsHeader, h264Header.length, ppsLen);

        format.setByteBuffer("csd-0", ByteBuffer.wrap(spsHeader));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(ppsHeader));
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);

        if (video_decoder != null) {
            video_decoder.stop();
            video_decoder.release();
            video_decoder = null;
        }
        video_decoder = MediaCodec.createDecoderByType("video/avc");
        if (video_decoder == null) {
            return;
        }

        video_decoder.configure(format, surface, null, 0);
        video_decoder.start();
        inputBuffers = video_decoder.getInputBuffers();
        outputBuffers = video_decoder.getOutputBuffers();
        frameCount = 0;
        deltaTime = 0;
        isRuning = true;
        runDecodeVideoThread();
    }

    private void decodeThreadStart(byte[] videoBytes) {
        ThreadPool.getSingleThreadPool().submit(new decodeH264Thread(videoBytes));
    }

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
            ByteBuffer[] inputBuffers = video_decoder.getInputBuffers();
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
            while (isRuning) {
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

                    int inIndex = video_decoder.dequeueInputBuffer(timeoutUs);
                    if (inIndex >= 0) {
                        ByteBuffer byteBuffer = inputBuffers[inIndex];
                        byteBuffer.clear();
                        byteBuffer.put(streamBuffer, startIndex, nextFrameStart - startIndex);
                        //在给指定Index的inputbuffer[]填充数据后，调用这个函数把数据传给解码器
                        video_decoder.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, 0, 0);
                        startIndex = nextFrameStart;
                    } else {
                        Log.e("flvdecoder", "aaaaa");
                        continue;
                    }

                    int outIndex = video_decoder.dequeueOutputBuffer(info, timeoutUs);
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
                        video_decoder.releaseOutputBuffer(outIndex, doRender);
                    } else {
                        Log.e("flvdecoder", "bbbb");
                    }
                }
            }
        }
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

    /**
     * 解码视频流数据
     */
    private void runDecodeVideoThread() {
        Thread t = new Thread() {
            @SuppressLint("NewApi")
            public void run() {
                while (isRuning) {
                    int inIndex = -1;
                    try {
                        inIndex = video_decoder.dequeueInputBuffer(-1);
                    } catch (Exception e) {
                        return;
                    }
                    try {
                        if (inIndex >= 0) {
                            ByteBuffer buffer = inputBuffers[inIndex];
                            buffer.clear();

                            if (!video_data_Queue.isEmpty()) {
                                byte[] data;
                                data = video_data_Queue.take();
                                buffer.put(data);
                                video_decoder.queueInputBuffer(inIndex, 0, data.length,
                                        state == 0 ? 66 : 33, 0);
                            } else {
                                video_decoder.queueInputBuffer(inIndex, 0, 0,
                                        state == 0 ? 66 : 33, 0);
                            }
                        } else {
                            video_decoder.queueInputBuffer(inIndex, 0, 0,
                                    0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        }

                        int outIndex = video_decoder.dequeueOutputBuffer(info, 0);
                        switch (outIndex) {
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                outputBuffers = video_decoder.getOutputBuffers();
                                break;
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                isReady = true;
                                break;
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                break;
                            default:
                                video_decoder.releaseOutputBuffer(outIndex, true);
                                frameCount++;
                                deltaTime = System.currentTimeMillis() - counterTime;
                                if (deltaTime > 1000) {
                                    fps = (int) (((float) frameCount / (float) deltaTime) * 1000);
                                    counterTime = System.currentTimeMillis();
                                    frameCount = 0;
                                }
                                break;
                        }

                        //所有流数据解码完成，可以进行关闭等操作
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.e("FLVDecoder", "BUFFER_FLAG_END_OF_STREAM");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void initial(byte[] sps) throws IOException {
        MediaFormat format = null;
        boolean isVGA = true;
        //使用sps数据格式判断是否是VGA
        byte[] video_sps = {0, 0, 0, 1, 103, 100, 64, 41, -84, 44, -88, 10, 2, -1, -107};
//        for (int i = 0; i < sps.length; i++) {
//            if (video_sps[i] != sps[i]) {
//                //判断是否是VGA视频传输标准
//                isVGA = false;
//                break;
//            }
//        }
//        if (!isVGA) {
//            format = MediaFormat.createVideoFormat("video/avc", 1920, 1088);
//            byte[] header_pps = {0, 0, 0, 1, 104, -18, 56, -128};
//            format.setByteBuffer("csd-0", ByteBuffer.wrap(video_sps));
//            format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 640 * 360);
//        } else {
//            format = MediaFormat.createVideoFormat("video/avc", 1920, 1088);
//            byte[] header_sps = {0, 0, 0, 1, 103, 100, 64, 41, -84, 44, -88, 5, 0, 91, -112};
//            byte[] header_pps = {0, 0, 0, 1, 104, -18, 56, -128};
//            format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//            format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//            //      format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1280 * 720);
//        }
        //初始化编码器
        format = MediaFormat.createVideoFormat("video/avc", 1920, 1080);
        //获取h264中的pps及sps数据
        byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0,
                (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
        byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6,
                (byte) 229, 1, (byte) 151, (byte) 128};
        format.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);

        if (video_decoder != null) {
            video_decoder.stop();
            video_decoder.release();
            video_decoder = null;
        }
        video_decoder = MediaCodec.createDecoderByType("video/avc");
        if (video_decoder == null) {
            return;
        }

        video_decoder.configure(format, surface, null, 0);
        video_decoder.start();
        inputBuffers = video_decoder.getInputBuffers();
        outputBuffers = video_decoder.getOutputBuffers();
        frameCount = 0;
        deltaTime = 0;
        isRuning = true;
        runDecodeVideoThread();
    }

}

