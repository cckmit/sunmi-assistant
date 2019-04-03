package com.sunmi.ipc;

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

    public FLVDecoder(Surface surface, int playerState) {
        this.surface = surface;
        this.state = playerState;
    }

    public void stopRunning() {
        videoDataQueue.clear();
    }

    private byte[] h264Header = {0x00, 0x00, 0x00, 0x01};//h264标准头，所有数据都要拼上

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
                byte[] videoData = new byte[data.length - 20];
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

    public int getFPS() {
        return fps;
    }

    /**
     * 解析视频头
     * <0900002d 00000000 00000017 00000000 014d402a ffe10019 674d402a 963500f0 078d3705 06054000 00fa0000 3a9826fa 80010004 68ee1f20 00000038>
     * 23、24位是sps长度，sps数据之后的2、3位是pps长度
     */
    private void decodeHeader(byte[] data) throws IOException {
        //初始化编码器
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 1920, 1920);
//        LogCat.e("fld", "555555 data = " + Arrays.toString(data));

        //获取h264中的pps及sps数据
        int spsLen = byteToInt2(new byte[]{data[22], data[23]});
        byte[] spsHeader = new byte[spsLen + 4];
        System.arraycopy(h264Header, 0, spsHeader, 0, h264Header.length);
        System.arraycopy(data, 24, spsHeader, h264Header.length, spsLen);
        byte[] ppsHeader = new byte[spsLen + 4];
        int ppsLen = byteToInt2(new byte[]{data[spsLen + 24 + 1], data[spsLen + 24 + 2]});
        System.arraycopy(h264Header, 0, ppsHeader, 0, h264Header.length);
        System.arraycopy(data, spsLen + 24 + 3, ppsHeader, h264Header.length, ppsLen);

//        LogCat.e("fld", "555555 spsHeader = " + Arrays.toString(spsHeader));
//        LogCat.e("fld", "555555 ppsHeader = " + Arrays.toString(ppsHeader));
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
    private int byteToInt2(byte[] b) {
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
                        Log.e("FLVDecoder", "BUFFER_FLAG_END_OF_STREAM");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    /**
//     * 解码{@link #srcPath}音频文件 得到PCM数据块
//     * @return 是否解码完所有数据
//     */
//    private void srcAudioFormatToPCM() {
//        for (int i = 0; i < decodeInputBuffers.length-1; i++) {
//            int inputIndex = mediaDecode.dequeueInputBuffer(-1);//获取可用的inputBuffer -1代表一直等待，0表示不等待 建议-1,避免丢帧
//            if (inputIndex < 0) {
//                codeOver =true;
//                return;
//            }
//
//            ByteBuffer inputBuffer = decodeInputBuffers[inputIndex];//拿到inputBuffer
//            inputBuffer.clear();//清空之前传入inputBuffer内的数据
//            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);//MediaExtractor读取数据到inputBuffer中
//            if (sampleSize <0) {//小于0 代表所有数据已读取完成
//                codeOver=true;
//            }else {
//                mediaDecode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);//通知MediaDecode解码刚刚传入的数据
//                mediaExtractor.advance();//MediaExtractor移动到下一取样处
//                decodeSize+=sampleSize;
//            }
//        }
//
//        //获取解码得到的byte[]数据 参数BufferInfo上面已介绍 10000同样为等待时间 同上-1代表一直等待，0代表不等待。此处单位为微秒
//        //此处建议不要填-1 有些时候并没有数据输出，那么他就会一直卡在这 等待
//        int outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);
//
////        showLog("decodeOutIndex:" + outputIndex);
//        ByteBuffer outputBuffer;
//        byte[] chunkPCM;
//        while (outputIndex >= 0) {//每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
//            outputBuffer = decodeOutputBuffers[outputIndex];//拿到用于存放PCM数据的Buffer
//            chunkPCM = new byte[decodeBufferInfo.size];//BufferInfo内定义了此数据块的大小
//            outputBuffer.get(chunkPCM);//将Buffer内的数据取出到字节数组中
//            outputBuffer.clear();//数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
//            putPCMData(chunkPCM);//自己定义的方法，供编码器所在的线程获取数据,下面会贴出代码
//            mediaDecode.releaseOutputBuffer(outputIndex, false);//此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据
//            outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);//再次获取数据，如果没有数据输出则outputIndex=-1 循环结束
//        }
//
//    }
}

