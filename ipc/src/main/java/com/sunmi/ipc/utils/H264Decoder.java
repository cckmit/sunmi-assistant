package com.sunmi.ipc.utils;

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
 * Description:视频解析
 * Created by bruce on 2019/3/25.
 */
public class H264Decoder {

    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1920;

    private MediaCodec mediaCodec;//处理音视频的编解码的类MediaCodec

    private Surface surface;//显示画面的Surface

    private int state = 0;// 0: live, 1: playback, 2: local file

    //视频数据
    private BlockingQueue<byte[]> videoDataQueue = new ArrayBlockingQueue<>(10000);

    private int fps = 30;

    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    private int frameCount = 0;
    private long deltaTime = 0;
    private long counterTime = System.currentTimeMillis();
    private boolean isRunning = false;

    private byte[] h264Header = {0x00, 0x00, 0x00, 0x01};//h264标准头，所有数据都要拼上

    public H264Decoder(Surface surface, int playerState) {
        this.surface = surface;
        this.state = playerState;
    }

    public void stopRunning() {
        videoDataQueue.clear();
    }

    /**
     * 添加视频数据
     *
     * @param data 视频数据或者头
     *             1、头数据：pps和sps，23、24位是sps长度，sps数据之后的2、3位是pps长度
     *             2、原始视频数据：前20位（11flvTag+1videoInfo+1avcInfo+3compositionTime+4video原数据长度）
     *             和后4位（该包的长度）需要截掉
     */
    public void setVideoData(byte[] data) {
        try {
            //第13位标识是视频数据还是头
            boolean isAVC = (data[12] & 0xFF) != 0;
            if (isAVC) {//视频数据
                byte[] videoData = new byte[data.length - 20];//去掉头20和尾4再加上标准头4
                System.arraycopy(h264Header, 0, videoData, 0, h264Header.length);
                System.arraycopy(data, 20, videoData, h264Header.length, data.length - 20 - 4);
                videoDataQueue.put(videoData);
            } else {//头信息
                decodeHeader(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析视频头
     * <0900002d 00000000 00000017 00000000 014d402a ffe10019 674d402a 963500f0
     * 078d3705 06054000 00fa0000 3a9826fa 80010004 68ee1f20 00000038>
     * 23、24位是sps长度，sps数据之后的2、3位是pps长度
     */
    private void decodeHeader(byte[] data) throws IOException {
        //初始化编码器
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", VIDEO_WIDTH, VIDEO_HEIGHT);

        //获取h264中的pps及sps数据
        int spsLen = byteToInt(new byte[]{data[22], data[23]});
        byte[] spsHeader = new byte[spsLen + 4];
        System.arraycopy(h264Header, 0, spsHeader, 0, h264Header.length);
        System.arraycopy(data, 24, spsHeader, h264Header.length, spsLen);
        byte[] ppsHeader = new byte[spsLen + 4];
        int ppsLen = byteToInt(new byte[]{data[spsLen + 24 + 1], data[spsLen + 24 + 2]});
        System.arraycopy(h264Header, 0, ppsHeader, 0, h264Header.length);
        System.arraycopy(data, spsLen + 24 + 3, ppsHeader, h264Header.length, ppsLen);

        format.setByteBuffer("csd-0", ByteBuffer.wrap(spsHeader));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(ppsHeader));
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps);

        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
        mediaCodec = MediaCodec.createDecoderByType("video/avc");
        if (mediaCodec == null) {
            return;
        }

        mediaCodec.configure(format, surface, null, 0);
        mediaCodec.start();
        inputBuffers = mediaCodec.getInputBuffers();
        outputBuffers = mediaCodec.getOutputBuffers();
        frameCount = 0;
        deltaTime = 0;
        isRunning = true;
        ThreadPool.getCachedThreadPool().submit(new DecodeH264Thread());//开启解码线程
    }

    /**
     * 将byte数组转换为int数据
     */
    private int byteToInt(byte[] b) {
        return (((int) b[0]) << 8) + b[1];
    }

    /**
     * 解码视频流数据thread
     */
    class DecodeH264Thread implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                int inIndex = -1;
                byte[] data;
                try {
                    inIndex = mediaCodec.dequeueInputBuffer(-1);
                } catch (Exception e) {
                    return;
                }
                try {
                    if (inIndex >= 0) {
                        ByteBuffer buffer = inputBuffers[inIndex];
                        buffer.clear();
                        if (!videoDataQueue.isEmpty()) {
                            data = videoDataQueue.take();
                            buffer.put(data);
                            mediaCodec.queueInputBuffer(inIndex, 0, data.length,
                                    state == 0 ? 66 : 33, 0);
                        } else {
                            mediaCodec.queueInputBuffer(inIndex, 0, 0,
                                    state == 0 ? 66 : 33, 0);
                        }
                    } else {
                        mediaCodec.queueInputBuffer(inIndex, 0, 0,
                                0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }

                    int outIndex = mediaCodec.dequeueOutputBuffer(info, 0);
                    switch (outIndex) {
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            outputBuffers = mediaCodec.getOutputBuffers();
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            break;
                        default:
                            mediaCodec.releaseOutputBuffer(outIndex, true);
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
                        Log.e("H264Decoder", "BUFFER_FLAG_END_OF_STREAM");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

