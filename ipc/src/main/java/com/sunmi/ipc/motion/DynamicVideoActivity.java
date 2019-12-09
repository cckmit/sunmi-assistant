package com.sunmi.ipc.motion;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.utils.TimeoutTimer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Objects;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * @author yangShiJie
 * @date 2019-09-09
 */
@EActivity(resName = "dynamic_activity_video_play")
public class DynamicVideoActivity extends BaseActivity implements
        SeekBar.OnSeekBarChangeListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener,
        VolumeHelper.VolumeChangeListener {
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
    private static final int DELAY_MILLIS = 10;
    /**
     * 读写权限
     */
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    /**
     * 请求状态码
     */
    private static int REQUEST_PERMISSION_CODE = 1;
    @ViewById(resName = "ivp_player")
    IVideoPlayer iVideoPlayer;
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
    @ViewById(resName = "tv_tip")
    TextView tvTip;
    @ViewById(resName = "tv_fail_tip")
    TextView tvFailTip;
    @ViewById(resName = "tv_retry")
    TextView tvRetry;
    @ViewById(resName = "ll_play_loading")
    LinearLayout llPlayLoading;

    @Extra
    String url;
    //            string url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//            String url = "https://wifi.cdn.sunmi.com/VIDEO/IPC/b6760075fd4326fc6bb1ef67f00b9e059a01b1159e8788649ffa8bb19a7b0661";
    @Extra
    String deviceModel;

    private FFmpegMediaMetadataRetriever retriever;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 是否暂停，是否静音，是否初始化了截屏
     */
    private boolean isPaused, isOpenVolume, isInitTakeScreenShot;
    /**
     * 音量
     */
    private VolumeHelper volumeHelper = null;
    private int bufferingUpdate;
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
        volumeHelper = new VolumeHelper(this);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
        initScreenWidthHeight();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            errorView();
            return;
        }
        TimeoutTimer.getInstance().start();
        loadingView();
        requestPermissions();
    }

    private void initScreenWidthHeight() {
        //手机屏幕的宽高
        int screenWidth = CommonHelper.getScreenWidth(context);
        int screenHeight = CommonHelper.getScreenHeight(context);
        float aspectRatio = screenWidth / screenHeight;
        if (DeviceTypeUtils.getInstance().isSS1(deviceModel)) {
            screenWidth = CommonHelper.getScreenHeight(context);
        } else {
            if (aspectRatio > 16 / 9) {
                screenWidth = screenHeight * 16 / 9;
            } else {
                screenHeight = 1080 * screenWidth / 1920;
            }
        }
        ViewGroup.LayoutParams params = iVideoPlayer.getLayoutParams();
        params.width = screenWidth;
        params.height = screenHeight;
        iVideoPlayer.setLayoutParams(params);
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    private void setVideoListener() {
        sbBar.setOnSeekBarChangeListener(this);
        iVideoPlayer.setOnPreparedListener(this);
        iVideoPlayer.setOnBufferingUpdateListener(this);
        iVideoPlayer.setOnCompletionListener(this);
        iVideoPlayer.setOnErrorListener(this);
        iVideoPlayer.setOnSeekCompleteListener(this);
    }

    /**
     * 读写权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else {
                initVideoPlay();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadingView();
                initVideoPlay();
            } else {
                //未授权
                requestPermissionsFailView();
                timeoutStop();
            }
        }
    }

    /**
     * 初始化播放
     */
    private void initVideoPlay() {
        new Handler().postDelayed(() -> {
            iVideoPlayer.load(url);
            setVideoListener();
            //初始化参数
            isPaused = false;
            ibPlay.setBackgroundResource(R.mipmap.pause_normal);
            sbBar.setProgress(0);
            tvCurrentPlayTime.setText(iVideoPlayer.generateTime(0));
        }, 300);
    }

    /**
     * 初始化设置截屏数据
     */
    private void initTakeScreenShot() {
        retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(url);
            isInitTakeScreenShot = true;
        } catch (Exception e) {
            isInitTakeScreenShot = false;
            errorView();
            e.printStackTrace();
        }
    }

    /**
     * 保存截屏
     */
    private void saveVideoFrameAtTime() {
        if (isFastClick(1200)) {
            return;
        }
        if (iVideoPlayer.getCurrentPosition() > 0 && retriever != null) {
            tvTip.setVisibility(View.VISIBLE);
            final Bitmap bitmap = retriever.getFrameAtTime(iVideoPlayer.getCurrentPosition() * 1000,
                    FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ImageUtils.saveImageToGallery(context, bitmap, 100)) {
                        shortTip(getString(R.string.ipc_dynamic_take_screen_shot_success));
                    } else {
                        shortTip(getString(R.string.ipc_dynamic_take_screen_shot_fail));
                    }
                    tvTip.setVisibility(View.GONE);
                }
            }, 1200);
        }
    }

    @Click(resName = "tv_retry")
    void retryClick() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.tip_network_fail_retry);
            return;
        }
        if (isFastClick(1500)) {
            return;
        }
        loadingView();
        TimeoutTimer.getInstance().start();
        requestPermissions();
    }

    @Click(resName = "ib_back")
    void onBackClick() {
        iVideoPlayer.release();
        finish();
    }

    @Click(resName = "ib_play")
    void onPlayClick() {
        if (iVideoPlayer == null || sbBar.getProgress() >= iVideoPlayer.getDuration()) {
            return;
        }
        ibPlay.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isPaused) {
            iVideoPlayer.pauseVideo();
        } else {
            iVideoPlayer.startVideo();
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
            volumeHelper.mute();
        } else {
            volumeHelper.unMute();
        }
    }

    private void setVolumeViewImage(int currentVolume100) {
        if (currentVolume100 == 0) {
            isOpenVolume = true;
            volumeHelper.mute();
            ibVolume.setBackgroundResource(R.mipmap.ic_muse);
        } else {
            isOpenVolume = false;
            volumeHelper.unMute();
            ibVolume.setBackgroundResource(R.mipmap.ic_volume);
        }
    }

    /**
     * 更新进度
     */
    private void syncProgress(Object obj) {
        if (obj != null) {
            String strProgress = String.valueOf(obj);
            int progress = Integer.valueOf(strProgress);
            if ((progress == 0)) {
                return;
            }
            long generateTime;
            if (progress + DELAY_MILLIS >= iVideoPlayer.getDuration()) {
                progress = sbBar.getMax();
                generateTime = sbBar.getMax();//毫秒
            } else {
                generateTime = (Long) obj;
            }
            sbBar.setProgress(progress);
            tvCurrentPlayTime.setText(iVideoPlayer.generateTime(generateTime));
        }
    }

    @UiThread
    void errorView() {
        rlBottomPanel.setVisibility(View.GONE);
        iVideoPlayer.setVisibility(View.GONE);
        llPlayFail.setVisibility(View.VISIBLE);
        llPlayLoading.setVisibility(View.GONE);
    }

    private void isShowBottomView() {
        iVideoPlayer.setVisibility(View.VISIBLE);
        llPlayFail.setVisibility(View.GONE);
        rlBottomPanel.setVisibility(View.VISIBLE);
        llPlayLoading.setVisibility(View.GONE);
    }

    private void requestPermissionsFailView() {
        rlBottomPanel.setVisibility(View.GONE);
        iVideoPlayer.setVisibility(View.GONE);
        llPlayFail.setVisibility(View.VISIBLE);
        tvRetry.setVisibility(View.GONE);
        llPlayLoading.setVisibility(View.GONE);
        tvFailTip.setText(R.string.ipc_dynamic_request_external_storage);
    }

    private void loadingView() {
        rlBottomPanel.setVisibility(View.GONE);
        iVideoPlayer.setVisibility(View.GONE);
        llPlayFail.setVisibility(View.GONE);
        llPlayLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 缓存状态
     **/
    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        LogCat.e(TAG, "onBufferingUpdate i=" + i);
        if (iVideoPlayer != null) {
            bufferingUpdate = i;
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
        if (iVideoPlayer != null) {
            isPaused = true;
            ibPlay.setBackgroundResource(R.mipmap.play_normal);
        }
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        }
        if (bufferingUpdate == 0) {
            errorView();
        } else {
            sbBar.setProgress(sbBar.getMax());
            tvCurrentPlayTime.setText(iVideoPlayer.generateTime(sbBar.getMax()));
        }
    }

    /**
     * 播放异常
     **/
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        LogCat.e(TAG, "onError");
        timeoutStop();
        shortTip(R.string.str_server_exception);
        errorView();
        return false;
    }


    /**
     * 开始播放
     **/
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "onPrepared");
        if (iVideoPlayer != null) {
            isShowBottomView();
            timeoutStop();
            iVideoPlayer.startVideo();
            if (!isInitTakeScreenShot) {
                initTakeScreenShot();
            }
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
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    /**
     * Seek拖动完毕
     **/
    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
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
        if (iVideoPlayer != null) {
            iVideoPlayer.seekTo(seekBar.getProgress());
            if (!iVideoPlayer.isPlaying()) {
                iVideoPlayer.startVideo();
                isPaused = false;
                ibPlay.setBackgroundResource(R.mipmap.pause_normal);
            }
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    private void timeoutStop() {
        TimeoutTimer.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        volumeHelper.unregisterVolumeReceiver();
        timeoutStop();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (retriever != null) {
            retriever.release();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.mqttResponseTimeout};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (id == CommonNotifications.mqttResponseTimeout) { //连接超时
            shortTip(R.string.str_server_exception);
            errorView();
        }
    }

    @Override
    public void onVolumeChanged(int volume) {
        setVolumeViewImage(volume);
    }
}
