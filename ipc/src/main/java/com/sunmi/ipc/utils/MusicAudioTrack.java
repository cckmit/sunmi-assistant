package com.sunmi.ipc.utils;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by bruce on 2019/4/3.
 * 播放pcm数据
 */
public class MusicAudioTrack {
    private int mFrequency;// 采样率
    private int mChannel;// 声道
    private int mSampBit;// 采样精度
    private AudioTrack mAudioTrack;

    MusicAudioTrack(int frequency, int channel, int sampbit) {
        mFrequency = frequency;
        mChannel = channel;
        mSampBit = sampbit;
    }

    /**
     * 初始化
     */
    public void init() {
        if (mAudioTrack != null) {
            release();
        }
        // 获得构建对象的最小缓冲区大小
        int minBufSize = getMinBufferSize();
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mFrequency, mChannel, mSampBit, minBufSize, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    /**
     * 将解码后的pcm数据写入audioTrack播放
     *
     * @param data   数据
     * @param offset 开始位置
     * @param length 需要播放的长度
     */
    void playAudioTrack(byte[] data, int offset, int length) {
        if (data == null || data.length == 0) {
            return;
        }
        try {
            mAudioTrack.write(data, offset, length);
        } catch (Exception e) {
            Log.e("MusicAudioTrack", "AudioTrack Exception : " + e.toString());
            e.printStackTrace();
        }
    }

    private int getMinBufferSize() {
        return AudioTrack.getMinBufferSize(mFrequency, mChannel, mSampBit);
    }

}
