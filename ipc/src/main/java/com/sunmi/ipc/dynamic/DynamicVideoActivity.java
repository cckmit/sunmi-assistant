package com.sunmi.ipc.dynamic;


import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.log.LogCat;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author yangShiJie
 * @date 2019-09-09
 */
@EActivity(resName = "dynamic_activity_video_play")
public class DynamicVideoActivity extends BaseActivity implements
        SunmiVideoListener, SeekBar.OnSeekBarChangeListener {

    /**
     * 同步进度
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 缓冲进度界限值
     */
    private static final int BUFFERING_PROGRESS = 95;
    /**
     * 延迟毫秒数
     */
    private static final int DELAY_MILLIS = 500;
    @ViewById(resName = "ivp_player")
    SunmiIMediaPlayer iVideoPlayer;
    @ViewById(resName = "ib_back")
    ImageButton ibBack;
    @ViewById(resName = "ib_play")
    ImageButton ibPlay;
    @ViewById(resName = "ib_volume")
    ImageButton ibVolume;
    @ViewById(resName = "tv_current_play_time")
    TextView tvCurrentPlayTime;
    @ViewById(resName = "tv_count_play_time")
    TextView tvCountPlayTime;
    @ViewById(resName = "sb_bar")
    SeekBar sbBar;
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "rl_bottom_panel")
    RelativeLayout rlBottomPanel;
    @Extra
//    String url;
//    String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
            String url = "http://test.cdn.sunmi.com/VIDEO/IPC/f4c28c287dff0e0656e00192450194e76f4863f80ca0517a135925ebc7828104";
    @Extra
    String deviceModel;

    MediaMetadataRetriever retriever;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 是否暂停，是否静音
     */
    private boolean isPaused, isOpenVolume;
    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //滑动中，同步播放进度
            if (msg.what == MESSAGE_SHOW_PROGRESS) {
                if (!isDragging) {
                    msg = obtainMessage(MESSAGE_SHOW_PROGRESS, iVideoPlayer.getCurrentPosition());
                    sendMessageDelayed(msg, DELAY_MILLIS);
                    syncProgress(msg.obj);
                }
            }
        }
    };

    @AfterViews
    void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        sbBar.setOnSeekBarChangeListener(this);
        //手机屏幕的宽高
        int screenWidth = CommonHelper.getScreenWidth(context);
        int screenHeight = CommonHelper.getScreenHeight(context);
        ViewGroup.LayoutParams lpCloud = iVideoPlayer.getLayoutParams();
        lpCloud.width = screenWidth;
        lpCloud.height = screenHeight;
        iVideoPlayer.setLayoutParams(lpCloud);
        iVideoPlayer.setVideoListener(this);
        initTakeScreenShot();
        if (!NetworkUtils.isNetworkAvailable(context)) {
            errorView();
            return;
        }
        initVideoPlay();
    }

    /**
     * 初始化播放
     */
    private void initVideoPlay() {
        try {
            iVideoPlayer.setPath(url);
            iVideoPlayer.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化设置截屏数据
     */
    private void initTakeScreenShot() {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url, new HashMap<String, String>());
    }

    /**
     * 保存截屏
     */
    private void saveVideoFrameAtTime() {
        if (isFastClick(1500)) {
            return;
        }
        if (iVideoPlayer.getCurrentPosition() > 0) {
            Bitmap bitmap = retriever.getFrameAtTime(iVideoPlayer.getCurrentPosition() * 1000, MediaMetadataRetriever.OPTION_NEXT_SYNC);
            if (ImageUtils.saveImageToGallery(context, bitmap, 100)) {
                shortTip(getString(R.string.ipc_dynamic_take_screen_shot_success));
            } else {
                shortTip(getString(R.string.ipc_dynamic_take_screen_shot_fail));
            }
        }
    }

    @Click(resName = "tv_retry")
    void retryClick() {
        isShowBottomView(true);
        showLoadingDialog();
        initVideoPlay();
    }

    @Click(resName = "ib_back")
    void onBackClick() {
        iVideoPlayer.release();
        finish();
    }

    @Click(resName = "ib_play")
    void onPlayClick() {
        ibPlay.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isPaused) {
            iVideoPlayer.pause();
        } else {
            iVideoPlayer.start();
        }
    }

    @Click(resName = "ib_save_img")
    void onSaveImgClick() {
        saveVideoFrameAtTime();
    }

    @Click(resName = "ib_volume")
    void onVolumeClick() {
        ibVolume.setBackgroundResource(isOpenVolume ? R.mipmap.ic_volume : R.mipmap.ic_muse);
        isOpenVolume = !isOpenVolume;
        if (isOpenVolume) {
            iVideoPlayer.volume();
        } else {
            iVideoPlayer.muteVolume();
        }
    }

    /**
     *
     */
    private void syncProgress(Object obj) {
        if (obj != null) {
            long generateTime;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                generateTime = (Long) obj;
                sbBar.setProgress(Math.toIntExact((Long) obj));
            } else {
                String strProgress = String.valueOf(obj);
                int progress = Integer.valueOf(strProgress);
                if ((progress == 0)) {
                    return;
                }
                if (progress + DELAY_MILLIS >= iVideoPlayer.getDuration()) {
                    sbBar.setProgress(sbBar.getMax());
                    generateTime = ((Long) obj) + 1000;//毫秒
                } else {
                    sbBar.setProgress(progress);
                    generateTime = (Long) obj;
                }
            }
            //刷新当前播放时间
            tvCurrentPlayTime.setText(iVideoPlayer.generateTime(generateTime));
        }
    }

    private void errorView() {
        rlBottomPanel.setVisibility(View.GONE);
        iVideoPlayer.setVisibility(View.GONE);
        llPlayFail.setVisibility(View.VISIBLE);
    }

    private void isShowBottomView(boolean hasShow) {
        iVideoPlayer.setVisibility(View.VISIBLE);
        llPlayFail.setVisibility(View.GONE);
        if (hasShow) {
            rlBottomPanel.setVisibility(View.VISIBLE);
        } else {
            rlBottomPanel.setVisibility(View.GONE);
        }
    }

    /**
     * 缓存状态
     **/
    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        LogCat.e(TAG, "onBufferingUpdate i=" + i);
        if (iVideoPlayer != null) {
            int onBufferingProgress;
            if (i >= BUFFERING_PROGRESS) {
                onBufferingProgress = (int) iVideoPlayer.getDuration();
            } else {
                onBufferingProgress = (int) (iVideoPlayer.getDuration() / 100 * i);
            }
            sbBar.setSecondaryProgress(onBufferingProgress);
        }
    }

    /**
     * 播放完毕
     **/
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "onCompletion");
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        }
    }

    /**
     * 播放异常
     **/
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        LogCat.e(TAG, "onError");
        hideLoadingDialog();
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        LogCat.e(TAG, "onInfo");
        return false;
    }

    /**
     * 开始播放
     **/
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "onPrepared");
        if (iVideoPlayer != null) {
            hideLoadingDialog();
            iVideoPlayer.start();
            //设置seekBar的最大限度值，当前视频的总时长（毫秒）
            long duration = iVideoPlayer.getDuration();
            //不足一秒补一秒
            if (duration % 1000 > 0) {
                duration = duration + (1000 - duration % 1000);
            }
            sbBar.setMax((int) duration);
            //视频总时长
            tvCountPlayTime.setText(Objects.requireNonNull(iVideoPlayer).generateTime(duration));
            //发送当前播放时间点通知
            Message message = Message.obtain(mHandler, MESSAGE_SHOW_PROGRESS, iVideoPlayer.getCurrentPosition());
            mHandler.sendMessageDelayed(message, DELAY_MILLIS);
        }
    }

    /**
     * Seek拖动完毕
     **/
    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "onSeekComplete");
    }

    /**
     * Video大小改变
     **/
    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        LogCat.e(TAG, "onVideoSizeChanged");
    }


    /**
     * 进度条滑动监听
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            String time = iVideoPlayer.generateTime(progress);
            tvCurrentPlayTime.setText(time);
        }
    }

    /**
     * 开始拖动
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
    }

    /**
     * 停止拖动
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        iVideoPlayer.seekTo(seekBar.getProgress());
        if (iVideoPlayer != null && !iVideoPlayer.isPlaying()) {
            iVideoPlayer.start();
        }
        mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        isDragging = false;
        //拖动停止后发送通知
        mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
