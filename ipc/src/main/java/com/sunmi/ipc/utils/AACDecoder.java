package com.sunmi.ipc.utils;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sunmi.common.utils.ThreadPool;

/**
 * Description:解析aac音频
 * Created by bruce on 2019/3/25.
 */
public class AACDecoder {

    private static final String TAG = "AACDecoderUtil";
    //声道数
    private static final int KEY_CHANNEL_COUNT = 2;
    //采样率
    private static final int KEY_SAMPLE_RATE = 8000;
    private static final int KEY_BIT_RATE = 128000;
    //用于播放解码后的pcm
    private MusicAudioTrack mPlayer;

    private MediaCodec mDecoder; //解码器

    private int count = 0;//用来记录解码失败的帧数

    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();//编解码器缓冲区

    private boolean isRunning;

    public AACDecoder() {
        isRunning = true;
        start();
        ThreadPool.getCachedThreadPool().submit(new DecodeAACWorker());//开启解码线程
    }

    //音频数据
    private BlockingQueue<byte[]> audioDataQueue = new ArrayBlockingQueue<>(10000);

    //添加音频数据
    public void setAudioData(byte[] data) {
        try {
            audioDataQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        audioDataQueue.clear();
    }

    /**
     * 判断aac帧头
     */
    private boolean isHeader(byte[] data) {
        return (data[12] & 0xFF) == 0;
    }

    /**
     * 初始化所有变量
     */
    public void start() {
        mPlayer = new MusicAudioTrack(KEY_SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        mPlayer.init();
        decodeInit();
    }

    /**
     * 初始化解码器
     *
     * @return 初始化失败返回false，成功返回true
     */
    private void decodeInit() {
        // 初始化AudioTrack
        mPlayer = new MusicAudioTrack(KEY_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        mPlayer.init();
        try {
            //需要解码数据的类型
            String mine = "audio/mp4a-latm";
            //初始化解码器
            mDecoder = MediaCodec.createDecoderByType(mine);
            //MediaFormat用于描述音视频数据的相关参数
            MediaFormat mediaFormat = new MediaFormat();
            //数据类型
            mediaFormat.setString(MediaFormat.KEY_MIME, mine);
            //声道个数
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, KEY_CHANNEL_COUNT);
            //采样率
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, KEY_SAMPLE_RATE);
            //比特率
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
            //用来标记AAC是否有adts头，1->有
            mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 0);
            //用来标记aac的类型
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                    MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            //adts头
            int profile = MediaCodecInfo.CodecProfileLevel.AACObjectLC;  //AAC LC
            int freqIdx = 11;  //sample rate
            int chanCfg = 2;  //CPE
            ByteBuffer csd = ByteBuffer.allocate(2);
            csd.put(0, (byte) (profile << 3 | freqIdx >> 1));
            csd.put(1, (byte) ((freqIdx & 0x01) << 7 | chanCfg << 3));
            mediaFormat.setByteBuffer("csd-0", csd);
            //配置解码器
            mDecoder.configure(mediaFormat, null, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mDecoder != null) {
            mDecoder.start();
        }
    }

    /**
     * aac解码+播放
     */
    public void decode(byte[] buf, int offset, int length) {
        //输入ByteBuffer
        ByteBuffer[] codecInputBuffers = mDecoder.getInputBuffers();
        //输出ByteBuffer
        ByteBuffer[] codecOutputBuffers = mDecoder.getOutputBuffers();
        //等待时间，0->不等待，-1->一直等待
        try {
            //返回一个包含有效数据的input buffer的index,-1->不存在
            long kTimeOutUs = 0;
            int inputBufIndex = mDecoder.dequeueInputBuffer(kTimeOutUs);
            if (inputBufIndex >= 0) {
                //获取当前的ByteBuffer
                ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                //清空ByteBuffer
                dstBuf.clear();
                //填充数据
                dstBuf.put(buf, offset, length);
                //将指定index的input buffer提交给解码器
                mDecoder.queueInputBuffer(inputBufIndex, 0, length, 0, 0);
            }
            //返回一个output buffer的index，-1->不存在
            int outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs);

            if (outputBufferIndex < 0) {
                count++; //记录解码失败的次数
            }
            ByteBuffer outputBuffer;
            while (outputBufferIndex >= 0) {
                //获取解码后的ByteBuffer
                outputBuffer = codecOutputBuffers[outputBufferIndex];
                //用来保存解码后的数据
                byte[] outData = new byte[info.size];
                outputBuffer.get(outData);
                //清空缓存
                outputBuffer.clear();
                //播放
                mPlayer.playAudioTrack(outData, 0, info.size);
                //释放已经解码的buffer
                mDecoder.releaseOutputBuffer(outputBufferIndex, false);
                //解码未解完的数据
                outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void stop() {
        try {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            if (mDecoder != null) {
                mDecoder.stop();
                mDecoder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DecodeAACWorker implements Runnable {
        @Override
        public void run() {
            try {
                //每次从文件读取的数据
                byte[] readData;
                //循环读取数据
                while (isRunning) {
                    if (!audioDataQueue.isEmpty()) {
                        readData = audioDataQueue.take();
                        int readLen = readData.length;
                        if (!isHeader(readData)) {
                            byte[] data = new byte[readLen - 13 - 4];
                            System.arraycopy(readData, 13, data, 0, data.length);
                            decode(data, 0, data.length);
                        } else {
                            Log.e("AACDecoder", "555555 is header---");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
