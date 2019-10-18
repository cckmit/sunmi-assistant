package com.sunmi.ipc.view.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.datelibrary.DatePickDialog;
import com.datelibrary.OnSureListener;
import com.datelibrary.bean.DateType;
import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.VideoPlayContract;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.presenter.VideoPlayPresenter;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.sunmi.ipc.view.ZFTimeLine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.VerticalSeekBar;
import sunmi.common.view.dialog.LoadingDialog;

/**
 * Description:
 * Created by bruce on 2019/4/11.
 */
@EActivity(resName = "activity_video_play")
public class VideoPlayActivity extends BaseMvpActivity<VideoPlayPresenter>
        implements VideoPlayContract.View, SurfaceHolder.Callback, IOTCClient.Callback,
        View.OnTouchListener, IVideoPlayer.VideoPlayListener, ZFTimeLine.OnZFTimeLineListener {
    @ViewById(resName = "rl_screen")
    RelativeLayout rlScreen;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "ivp_cloud")
    IVideoPlayer ivpCloud;
    @ViewById(resName = "rl_control_panel")
    RelativeLayout rlController;
    @ViewById(resName = "rl_top")
    RelativeLayout rlTopBar;
    @ViewById(resName = "rl_bottom")
    RelativeLayout rlBottomBar;
    @ViewById(resName = "sBar_voice")
    VerticalSeekBar sBarVoice;//音量控制
    @ViewById(resName = "ll_change_volume")
    LinearLayout llChangeVolume;//音量控制
    @ViewById(resName = "iv_record")
    ImageView ivRecord;//录制
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;//音量
    @ViewById(resName = "tv_quality")
    TextView tvQuality;//画质
    @ViewById(resName = "ll_video_quality")
    LinearLayout llVideoQuality;//是否显示画质
    @ViewById(resName = "tv_fhd_quality")
    TextView tvHDQuality;//高清画质
    @ViewById(resName = "tv_hd_quality")
    TextView tvSDQuality;//标清画质
    @ViewById(resName = "cm_timer")
    Chronometer cmTimer;//录制时间
    @ViewById(resName = "rl_record")
    RelativeLayout rlRecord;
    @ViewById(resName = "tv_calender")
    TextView tvCalender;//日历
    @ViewById(resName = "iv_screenshot")
    ImageView ivScreenshot;//截图
    @ViewById(resName = "iv_live")
    ImageView ivLive;//直播
    @ViewById(resName = "iv_play")
    ImageView ivPlay;//开始播放
    //    @ViewById(resName = "iv_setting")
//    ImageView ivSetting;//设置
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "scale_panel")
    ZFTimeLine scalePanel;
    @ViewById(resName = "tv_time_scroll")
    TextView tvTimeScroll;

    @Extra
    String UID;//用来打通P2P
    @Extra
    String ipcType;//ss or fs
    @Extra
    int deviceId;//设备sn

    private int screenW, screenH; //手机屏幕的宽高
    private float aspectRatio;//宽高比

    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isCloudPlayBack;//是否正在云回放
    private boolean isDevPlayBack;//是否正在设备回放
    private boolean isPaused;//回放是否暂停
    private boolean isCurrentLive;//当前是否直播
    private int qualityType = 0;//0-超清，1-高清

    private boolean isVideoLess1Minute;//视频片段是否小于一分钟
    private boolean isFirstScroll = true;//是否第一次滑动

    private H264Decoder videoDecoder = null;
    private AACDecoder audioDecoder = null;
    private VolumeHelper volumeHelper = null;

    private IOTCClient iotcClient;
    //日历
    private Calendar calendar;
    //当前时间 ，三天前秒数 ，区间总共秒数
    private long currentDateSeconds, threeDaysBeforeSeconds;
    //3天秒数
    private long threeDaysSeconds = 3 * 24 * 60 * 60;
    //10分钟
    private int tenMinutes = 10 * 60;
    //刻度尺移动定时器
    private ScheduledExecutorService executorService;
    //滑动停止的时间戳
    private long scrollTime;
    //选择日历当前的时间的0点
    private long selectedDate;
    //是否为选择的日期
    private boolean isSelectedDate;

    private Handler handler = new Handler();

    //屏幕控件自动隐藏计时器
    CountDownTimer hideControllerPanelTimer;
    private Drawable drawableLeft, drawableRight;
    /*
     *绘制时间轴
     */
    private List<VideoTimeSlotBean> listAp = new ArrayList<>();
    private List<VideoTimeSlotBean> listCloud = new ArrayList<>();
    private LoadingDialog timeSlotsDialog;

    @AfterViews
    void init() {
        mPresenter = new VideoPlayPresenter();
        mPresenter.attachView(this);
        LogCat.e(TAG, "8888888 uid = " + UID);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        initData();
        showLoadingDialog();
        initSurfaceView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initControllerPanel();
            }
        }, 200);
    }

    void initControllerPanel() {
        rlScreen.setOnTouchListener(this);
        setPlayBooleanStatus();
        openMove();
        initVolume();
        rlController.setVisibility(View.VISIBLE);
        scalePanel.setListener(this);
    }

    void initData() {
        screenW = CommonHelper.getScreenWidth(context);
        screenH = CommonHelper.getScreenHeight(context);
        aspectRatio = screenW / screenH;
        //当前天
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvCalender.setText(day > 9 ? day + "" : "0" + day);
        //当前时间秒数
        currentDateSeconds = System.currentTimeMillis() / 1000;
        //三天前秒数
        threeDaysBeforeSeconds = currentDateSeconds - threeDaysSeconds;

        iotcClient = new IOTCClient(UID);
        //直播回调
        iotcClient.setCallback(this);
        audioDecoder = new AACDecoder();
    }

    private void initSurfaceView() {
        int width = screenW, height = screenH;
        if (isSS1()) {
            width = screenH;
        } else {
            if (aspectRatio > 16 / 9) {
                width = screenH * 16 / 9;
            } else {
                height = 1080 * screenW / 1920;
            }
        }
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        videoView.setLayoutParams(lp);
        SurfaceHolder surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);

        ViewGroup.LayoutParams lpCloud = ivpCloud.getLayoutParams();
        lpCloud.width = width;
        lpCloud.height = height;
        ivpCloud.setLayoutParams(lpCloud);
        ivpCloud.setVideoPlayListener(this);
    }

    private boolean isSS1() {
        return DeviceTypeUtils.getInstance().isSS1(ipcType);
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    private void stopPlay() {
        cloudPlayDestroy();//关闭云端视频
        if (iotcClient != null) {
            iotcClient.close();
            iotcClient = null;
        }
        if (videoDecoder != null) {
            videoDecoder.release();
            videoDecoder = null;
        }
        if (audioDecoder != null) {
            audioDecoder.stop();
            audioDecoder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        removeCallbacks();
        closeMove();//关闭时间抽的timer
        cancelTimer();//关闭屏幕控件自动hide计时器
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isControlPanelShow) {
                    startTimer();
                }
                break;
        }
        return false;
    }

    /**
     * 按键控制音量，return true时不显示系统音量 return false时显示系统音量
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            llChangeVolume.setVisibility(View.GONE);
            ivVolume.setBackgroundResource(volumeHelper.get100CurrentVolume() == 0 ?//获取当前音量
                    R.mipmap.ic_muse : R.mipmap.ic_volume);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (videoDecoder == null) {
            videoDecoder = new H264Decoder(holder.getSurface(), 0);
            initP2pLive();
        } else {
            if (llPlayFail != null && llPlayFail.isShown()) {
                return;
            }
            setPanelVisible(View.VISIBLE);
            if (isCurrentLive && iotcClient != null) {
                showLoadingDialog();
                iotcClient.startPlay();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //放后台
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if ((isDevPlayBack || isCloudPlayBack) && !isPaused) {
            pausePlayClick();
        } else if (isCurrentLive && iotcClient != null) {
            iotcClient.stopLive();
            if (audioDecoder != null) {
                audioDecoder.stopRunning();
            }
        }
    }

    @Override
    public void initSuccess() {
        hideLoadingDialog();
    }

    @Override
    public void initFail() {
        hideLoadingDialog();
        setPlayFailVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoReceived(byte[] videoBuffer) {
        setPlayFailVisibility(View.GONE);
        if (videoDecoder != null) {
            videoDecoder.setVideoData(videoBuffer);
        }
    }

    @UiThread
    public void setPlayFailVisibility(int visibility) {
        llPlayFail.setVisibility(visibility);

    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        audioDecoder.setAudioData(audioBuffer);
    }

    @Override
    public void onStartPlay() {
        hideLoadingDialog();
    }

    @Override
    public void onPlayComplete() {
        //获取当前播放完毕时间判断是否cloud or ap
        selectedTimeIsHaveVideo(scalePanel.getCurrentInterval());
    }

    @Override
    public void onBackPressed() {
        stopPlay();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 400);
    }

    @Click(resName = "rl_top")
    void backClick() {
        onBackPressed();
    }

    //视频录制
    @Click(resName = "iv_record")
    void recordClick() {
        if (isStartRecord) {
            ivRecord.setBackgroundResource(R.mipmap.ic_recording_normal);
            rlRecord.setVisibility(View.GONE);
            isStartRecord = false;
            cmTimer.stop();//关闭录制
        } else {
            ivRecord.setBackgroundResource(R.mipmap.ic_recording);
            rlRecord.setVisibility(View.VISIBLE);
            isStartRecord = true;
            startRecord();//开始录制
        }
    }

    //音量
    @Click(resName = "iv_volume")
    void volumeClick() {
        if (llChangeVolume.isShown()) {
            llChangeVolume.setVisibility(View.GONE);
        } else {
            llChangeVolume.setVisibility(View.VISIBLE);
            int currentVolume100 = volumeHelper.get100CurrentVolume();//获取当前音量
            sBarVoice.setProgress(currentVolume100);
            setVolumeViewBackgroud(currentVolume100);
        }
    }

    //画质
    @Click(resName = "tv_quality")
    void qualityClick() {
        if (!isCurrentLive) {
            return;
        }
        llVideoQuality.setVisibility(llVideoQuality.isShown() ? View.GONE : View.VISIBLE);
        if (qualityType == 0) {
            tvHDQuality.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
            tvSDQuality.setTextColor(ContextCompat.getColor(this, R.color.c_white));
        } else {
            tvHDQuality.setTextColor(ContextCompat.getColor(this, R.color.c_white));
            tvSDQuality.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
        }
    }

    //超清画质
    @Click(resName = "tv_fhd_quality")
    void hdQualityClick() {
        tvQuality.setText(R.string.str_FHD);
        changeQuality(0);
    }

    //高清画质
    @Click(resName = "tv_hd_quality")
    void sdQualityClick() {
        tvQuality.setText(R.string.str_HD);
        changeQuality(1);
    }

    //开始，暂停
    @Click(resName = "iv_play")
    void pausePlayClick() {
        if (isFastClick(1000)) {
            return;
        }
        if (!isDevPlayBack && !isCloudPlayBack && isCurrentLive) {
            return;
        }
        ivPlay.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isDevPlayBack) {
            if (iotcClient != null) {
                iotcClient.pausePlayback(isPaused);
            }
        } else if (isCloudPlayBack) {
            if (isPaused) {
                ivpCloud.pause();
            } else {
                ivpCloud.play();
            }
        }
    }

    //直播
    @Click(resName = "iv_live")
    void playApBackClick() {
        switch2Live();
    }

    //显示日历
    @Click(resName = "tv_calender")
    void calenderClick() {
        if (isFastClick(1000)) {
            return;
        }
        showDatePicker();
    }

    //点击屏幕
    @Click(resName = "rl_screen")
    void screenClick() {
        if (isControlPanelShow) {
            hideControllerPanel();
            isControlPanelShow = false;
        } else {
            setPanelVisible(View.VISIBLE);
            isControlPanelShow = true;
        }
    }

    @Click(resName = "tv_retry")
    void retryClick() {
        setPlayFailVisibility(View.GONE);
        showLoadingDialog();
        initP2pLive();
    }

    @Override
    public void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots) {
        listCloud.clear();
        listCloud.addAll(slots);
        getCanvasList(startTime, endTime);
    }

    @Override
    public void getCloudTimeSlotFail() {
        if (listAp == null || listAp.size() == 0) {
            timeSlotsHideProgress();
            switch2Live();//无ap且无cloud的时间列表
        } else {
            timeCanvasList(listAp); //ap时间列表>0且cloud列表=0
        }
    }

    @Override
    public void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots) {
        if (slots != null && slots.size() > 0) {
            listAp.addAll(slots);
            getDeviceTimeSlots(slots.get(slots.size() - 1).getEndTime(), currentDateSeconds);
        } else {
            getCloudTimeSlots(deviceId, threeDaysBeforeSeconds, currentDateSeconds);
        }
    }

    @UiThread
    @Override
    public void startLiveSuccess() {
        isCurrentLive = true;
        isCloudPlayBack = false;
        isDevPlayBack = false;
        ivPlay.setBackgroundResource(R.mipmap.play_disable);
        ivLive.setVisibility(View.GONE);
        ivpCloud.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        scrollCurrentLive();
        hideLoadingDialog();
    }

    @UiThread
    @Override
    public void startPlaybackSuccess() {
        isCloudPlayBack = false;
        isCurrentLive = false;
        isDevPlayBack = true;
        ivpCloud.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        ivLive.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    @UiThread
    @Override
    public void getCloudVideosSuccess(List<VideoListResp.VideoBean> videoBeans) {
        isCloudPlayBack = true;
        isCurrentLive = false;
        isDevPlayBack = false;
        ivpCloud.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        ivLive.setVisibility(View.VISIBLE);
        List<String> urlList = new ArrayList<>();
        for (VideoListResp.VideoBean bean : videoBeans) {
            urlList.add(bean.getUrl());
        }
        cloudPlay(urlList);
    }

    //开始直播
    @Background
    void initP2pLive() {
        iotcClient.init();
    }

    @UiThread
    void hideControllerPanel() {
        setPanelVisible(View.GONE);
        llChangeVolume.setVisibility(View.GONE);//音量
        llVideoQuality.setVisibility(View.GONE);//画质
    }

    private void setPanelVisible(int visible) {
        if (rlTopBar != null && rlBottomBar != null) {
            rlTopBar.setVisibility(visible);
            rlBottomBar.setVisibility(visible);
        }
    }

    private void showDatePicker() {
        DatePickDialog datePickDialog = new DatePickDialog(context);
        datePickDialog.setType(DateType.TYPE_YMD);
        //设置点击确定按钮回调
        datePickDialog.setOnSureListener(new OnSureListener() {
            @Override
            public void onSure(Date date) {
                onSureButton(date);
            }
        });

        datePickDialog.setStartDate(scrollTime > 0 ? new Date(scrollTime)
                : new Date(System.currentTimeMillis()));
        datePickDialog.show();
    }

    /**
     * 切回直播
     */
    private void switch2Live() {
        isFirstScroll = true;
        showLoadingDialog();
        //如果是云端回放此时需要调用停止操作然后直播
        if (isCloudPlayBack) {
            cloudPlayDestroy();
        }
        //当前时间秒数 TODO 需优化播放中渲染的时间
        currentDateSeconds = System.currentTimeMillis() / 1000;
        selectedDate = currentDateSeconds;
        if (listAp.size() > 0) {
            refreshTimeSlotVideoList();
        }
        mPresenter.startLive(iotcClient);
    }

    /**
     * 切到设备回放
     */
    void switch2DevPlayback(long start) {
        showLoadingDialog();
        if (isCloudPlayBack) {
            cloudPlayDestroy();
        }
        mPresenter.startPlayback(iotcClient, start);
    }

    //********************************* 云端回放 ***********************************

    /**
     * 切到云端回放
     */
    void switch2CloudPlayback(long start, long end) {
        showLoadingDialog();
        if (!isCloudPlayBack) {
            if (isDevPlayBack) {
                iotcClient.stopPlayback();//先停止设备回放
            } else {
                iotcClient.stopLive();//先停止直播
            }
        }
        mPresenter.getCloudVideoList(deviceId, start, end);
    }

    //开始计时录制
    private void startRecord() {
        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = System.currentTimeMillis() - cArg.getBase();
                Date d = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                cmTimer.setText(sdf.format(d));
            }
        });
        cmTimer.setBase(System.currentTimeMillis());
        cmTimer.start();
    }
    //***********************云端回放***************************************

    private void changeQuality(int type) {
        llVideoQuality.setVisibility(View.GONE);
        if (type == qualityType) {
            return;
        }
        qualityType = qualityType == 0 ? 1 : 0;
        iotcClient.changeValue(qualityType);
        if (qualityType == 0) {
            shortTip(R.string.tip_video_quality_fhd);
        } else if (qualityType == 1) {
            shortTip(R.string.tip_video_quality_hd);
        }
    }

    /**
     * 播放云端回放
     *
     * @param urlList
     */
    private void cloudPlay(List<String> urlList) {
        ivpCloud.setUrlQueue(urlList);
        try {
            ivpCloud.startPlay();
        } catch (Exception e) {
            shortTip(R.string.tip_play_fail);
            e.printStackTrace();
        }
    }

    //*********************时间滑动条***************************

    /*
     * 云端回放销毁
     */
    private void cloudPlayDestroy() {
        try {
            if (ivpCloud != null) {
                ivpCloud.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调节音量
     */
    private void initVolume() {
        volumeHelper = new VolumeHelper(this);
        int currentVolume100 = volumeHelper.get100CurrentVolume();
        sBarVoice.setMax(100);
        sBarVoice.setProgress(currentVolume100);
        sBarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolumeViewBackgroud(progress);
                volumeHelper.setVoice100(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        setVolumeViewBackgroud(currentVolume100);
    }

    private void setVolumeViewBackgroud(int currentVolume100) {
        if (currentVolume100 == 0) {
            ivVolume.setBackgroundResource(R.mipmap.ic_muse);
        } else {
            ivVolume.setBackgroundResource(R.mipmap.ic_volume);
        }
    }

    /**
     * 一分钟轮询一次
     * 开始移动
     */
    public void openMove() {
        closeMove();
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        scrollTime = scalePanel.getCurrentInterval() * 1000;//获取实时滚动的时间
        tvCalender.setText(scalePanel.currentTimeStr().substring(6, 8));
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                moveTo();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @UiThread
    void moveTo() {
        LogCat.e(TAG, "11111 moveTo currentInteval = " + scalePanel.getCurrentInterval());
        scalePanel.autoMove();
        //自动滑动时下一个视频ap还是cloud播放
        if (!isCurrentLive && isVideoLess1Minute) {
            isVideoLess1Minute = false;
            switch2Playback(scalePanel.getCurrentInterval());
        }
    }

    //结束移动
    public void closeMove() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    //渲染时间轴并滚动到指定时间
    @UiThread
    void timeCanvasList(final List<VideoTimeSlotBean> apCloudList) {
        scalePanel.setVideoData(listAp);
        scalePanel.refresh();
        if (isFirstScroll && !isSelectedDate) {
            LogCat.e(TAG, "11111 timeCanvasList isFirstScroll ok= " + scalePanel.getCurrentInterval());
            isFirstScroll = false;
            selectedTimeIsHaveVideo(selectedDate); //初始化左滑渲染及回放
        } else {
            LogCat.e(TAG, "11111 timeCanvasList isFirstScroll no");
            if (isSelectedDate) {
                selectedTimeIsHaveVideo(selectedDate);//滑动到选择日期
            } else {
                scalePanel.refreshNow(); //滚动到当前时间
            }
        }
        //渲染完成
        timeSlotsHideProgress();
    }

    //选择日历日期回调
    @SuppressLint("DefaultLocale")
    public void onSureButton(Date date) {
        resetCountdown();//重置隐藏控件计时
        long currentTime = System.currentTimeMillis() / 1000;//当前时间戳秒
        scrollTime = date.getTime();//选择日期的时间戳毫秒
        long time = scrollTime / 1000; //设置日期的秒数
        if (time > currentTime) {//未来时间或当前--滑动当前直播
            isSelectedDate = false;
            scrollTime = System.currentTimeMillis();
            tvCalender.setText(String.format("%td", new Date()));
            if (isCurrentLive) {
                return;
            }
            switch2Live();
        } else {//回放时间
            isFirstScroll = false;//非首次滑动
            isSelectedDate = true;
            isCurrentLive = false; //回放
            ivPlay.setBackgroundResource(R.mipmap.pause_normal);
            ivLive.setVisibility(View.VISIBLE);

            String strDate = DateTimeUtils.secondToDate(time, "yyyy-MM-dd");
            int year = Integer.valueOf(strDate.substring(0, 4));
            int month = Integer.valueOf(strDate.substring(5, 7));
            int day = Integer.valueOf(strDate.substring(8, 10));
            //显示日历天数
            tvCalender.setText(String.format("%td", date));
            //设置选择日期的年月日0时0分0秒
            calendar.clear();
            calendar.set(year, month - 1, day, 0, 0, 0);//设置时候月份减1即是当月
            selectedDate = calendar.getTimeInMillis() / 1000;//设置日期的秒数
            //当前时间秒数
            currentDateSeconds = System.currentTimeMillis() / 1000;
            //选择日期三天前的秒数
            threeDaysBeforeSeconds = selectedDate - threeDaysSeconds;
            //加载时间轴
            refreshTimeSlotVideoList();
        }
    }

    //获取视频跳转播放的currentItemPosition
    private void videoSkipScrollPosition(long currentTimeMinutes) {
        scalePanel.moveToTime(currentTimeMinutes);
    }

    //滑动回放定位的中间 position
    private void scrollCurrentPlayBackTime(long currentTimeMinutes) {
        ivPlay.setBackgroundResource(R.mipmap.pause_normal);
        isPaused = false;
        scalePanel.moveToTime(currentTimeMinutes);
        openMove();
    }

    //直播boolean状态
    private void setPlayBooleanStatus() {
        isDevPlayBack = false;//dev
        isCloudPlayBack = false;//cloud
        isCurrentLive = true; //直播
    }

    /**
     * 滑动到当前时间
     * <p>
     * 1 回放视频为空
     * 2 点击直播按钮
     */
    private void scrollCurrentLive() {
        setPlayBooleanStatus();
        scalePanel.refreshNow();
        openMove();
    }

    //拖动或选择的时间是否有video（ap或cloud）
    private void selectedTimeIsHaveVideo(long currTime) {
        int apSize = listAp.size();
        if (apSize == 0) {
            switch2Live();//跳转直播
            return;
        }
        long mStartTime = threeDaysBeforeSeconds, mEndTime = currentDateSeconds;
        for (int i = 0; i < apSize + 1; i++) {
            long startOpposite = 0, endOpposite = 0, start = 0, end = 0;
            //不包含ap时间轴内的时间
            if (i == 0) {
                startOpposite = mStartTime;
                endOpposite = listAp.get(i).getStartTime();
            } else if (i < apSize) {
                startOpposite = listAp.get(i - 1).getEndTime();
                endOpposite = listAp.get(i).getStartTime();
            } else if (i == apSize) {
                startOpposite = listAp.get(i - 1).getEndTime();
                endOpposite = mEndTime;
            }
            //包含ap时间内
            if (i < apSize) {
                start = listAp.get(i).getStartTime();
                end = listAp.get(i).getEndTime();
            }
            if (currTime >= startOpposite && currTime < endOpposite) {//空白区域
                if (i == apSize) {//最后一个无视频区域跳转直播
                    switch2Live();
                    return;
                }
                boolean isCloud = !listAp.get(i).isApPlay();
                //当前的视频片段是否小于一分钟
                isVideoLess1Minute = listAp.get(i).getEndTime() - listAp.get(i).getStartTime() <= 60;
                if (isCloud) {
                    switch2CloudPlayback(endOpposite, endOpposite + tenMinutes);
                } else {
                    switch2DevPlayback(endOpposite);
                }
                scrollCurrentPlayBackTime(endOpposite);//回放到拖动的时间点
                break;
            } else if (currTime >= start && currTime < end) {//视频区域
                boolean isCloud = !listAp.get(i).isApPlay();
                //当前的视频片段是否小于一分钟
                isVideoLess1Minute = listAp.get(i).getEndTime() - currTime <= 60;
                if (isCloud) {
                    switch2CloudPlayback(currTime, currTime + tenMinutes);
                } else {
                    switch2DevPlayback(currTime);
                }
                scrollCurrentPlayBackTime(currTime);//回放到拖动的时间点
                break;
            }
        }
    }

    private void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private void switch2Playback(long currTime) {
        if (!isCloudPlayBack && !isDevPlayBack && isCurrentLive) {
            return;
        }
        int availableVideoSize = listAp.size();
        for (int i = 0; i < availableVideoSize; i++) {
            VideoTimeSlotBean bean = listAp.get(i);
            long start = bean.getStartTime();
            long end = bean.getEndTime();
            //当滑动到最后前后一分钟时，判断下一个视频片段ap还是cloud
            if (end - currTime < 60 && currTime >= start && currTime < end) {
                if (i == availableVideoSize - 1) {//todo 最后一个，需要渲染后面的数据
//                    refreshTimeSlotVideoList();//i是最后一个，基于i的end作为start再拉7天的数据。
                } else {
                    boolean isCloud = !listAp.get(i + 1).isApPlay();
                    final int delayMillis = (int) end - currTime < 0 ? 1 : (int) (end - currTime);
                    final int finalI = i;
                    if (isCloud) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch2CloudPlayback(listAp.get(finalI + 1).getStartTime(),
                                        listAp.get(finalI + 1).getStartTime() + tenMinutes);
                                videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime()); //偏移跳转
                            }
                        }, delayMillis * 1000);
                        break;
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch2DevPlayback(listAp.get(finalI + 1).getStartTime());
                                videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime());//偏移跳转
                            }
                        }, delayMillis * 1000);
                        break;
                    }
                }
            }
        }
    }

    private void timeSlotsShowProgress() {
        if (timeSlotsDialog == null) {
            timeSlotsDialog = new LoadingDialog(this);
            timeSlotsDialog.setLoadingContent(null);
        }
        timeSlotsDialog.show();
    }

    private void timeSlotsHideProgress() {
        if (timeSlotsDialog != null) {
            timeSlotsDialog.dismiss();
        }
        //开启控件隐藏倒计时
        startTimer();
    }

    //发送请求获取组合时间轴
    private void refreshTimeSlotVideoList() {
        timeSlotsShowProgress();
        listAp.clear();
        getDeviceTimeSlots(threeDaysBeforeSeconds, currentDateSeconds);
    }

    //获取cloud回放时间轴
    public void getCloudTimeSlots(int deviceId, long startTime, long endTime) {
        mPresenter.getTimeSlots(deviceId, startTime, endTime);
    }

    //获取设备sd卡回放时间轴
    public void getDeviceTimeSlots(long startTime, long endTime) {
        mPresenter.getPlaybackList(iotcClient, startTime, endTime);
    }

    //时间轴组合
    private void getCanvasList(long mStartTime, long mEndTime) {
        int apSize = listAp.size();
        int cloudSize = listCloud.size();
        if (apSize == 0 && cloudSize > 0) {
            listAp = listCloud;
            timeCanvasList(listAp);//组合时间轴渲染
            return;
        }
        VideoTimeSlotBean bean;
        //AP时间
        for (int i = 0; i < apSize + 1; i++) {
            long startAp = 0, endAp = 0;
            //不包含ap时间轴内的时间
            if (i == 0) {
                startAp = mStartTime;
                endAp = listAp.get(i).getStartTime();
            } else if (i < apSize) {
                startAp = listAp.get(i - 1).getEndTime();
                endAp = listAp.get(i).getStartTime();
            } else if (i == apSize) {
                startAp = listAp.get(i - 1).getEndTime();
                endAp = mEndTime;
            }
            //cloud时间
            for (int j = 0; j < cloudSize; j++) {
                bean = new VideoTimeSlotBean();
                long startCloud = listCloud.get(j).getStartTime();
                long endCloud = listCloud.get(j).getEndTime();

                if (startCloud >= startAp && endAp > startCloud && endCloud >= endAp) {
                    bean.setStartTime(startCloud);
                    bean.setEndTime(endAp);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startAp >= startCloud && endCloud > startAp && endAp >= endCloud) {
                    bean.setStartTime(startAp);
                    bean.setEndTime(endCloud);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startAp != endAp && startAp >= startCloud && endAp <= endCloud) {
                    bean.setStartTime(startAp);
                    bean.setEndTime(endAp);
                    bean.setApPlay(false);
                    listAp.add(bean);
                } else if (startCloud != endCloud && startCloud >= startAp && endCloud <= endAp) {
                    bean.setStartTime(startCloud);
                    bean.setEndTime(endCloud);
                    bean.setApPlay(false);
                    listAp.add(bean);
                }
            }
        }
        LogCat.e(TAG, "11111  apSize>0  cloudSize>0");
        if (apSize > 0 && cloudSize > 0) {
            listAp = duplicateRemoval(listAp);//去重
            Collections.sort(listAp);//正序比较
        }
        timeCanvasList(listAp);//组合时间轴渲染
    }

    //去重
    private List<VideoTimeSlotBean> duplicateRemoval(List<VideoTimeSlotBean> list) {
        LinkedHashSet<VideoTimeSlotBean> tmpSet = new LinkedHashSet<>(list.size());
        tmpSet.addAll(list);
        list.clear();
        list.addAll(tmpSet);
        return list;
    }

    private void startTimer() {
        if (hideControllerPanelTimer == null) {
            hideControllerPanelTimer = new CountDownTimer(8000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    hideControllerPanel();
                    isControlPanelShow = false;
                }
            };
        }
        hideControllerPanelTimer.start();
    }

    private void cancelTimer() {
        if (hideControllerPanelTimer != null) {
            hideControllerPanelTimer.cancel();
        }
    }

    //重置倒计时
    private void resetCountdown() {
        cancelTimer();
        startTimer();
    }

    @Override
    public void didMoveToDate(String date, long timeStamp) {
        LogCat.e(TAG, "11111 didMoveToDate, " + date + ",  " + timeStamp);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTimeScroll.setVisibility(View.GONE);
            }
        }, 500);
        if (timeStamp > System.currentTimeMillis() / 1000) {
            shortTip(getString(R.string.ipc_time_over_current_time));
            if (isCurrentLive) {//超过当前时间--当前处于直播
                scrollCurrentLive();
                return;
            }
            switch2Live(); //超过当前时间--当前处于回放
            return;
        }
        if (timeStamp < threeDaysBeforeSeconds) {
            shortTip(getString(R.string.ipc_time_over_back_time));
            selectedTimeIsHaveVideo(threeDaysBeforeSeconds);
            return;
        }
        if (isFirstScroll && listAp.size() == 0) {
            selectedDate = timeStamp;
            scalePanel.clearData();//clear渲染时间轴
            getDeviceTimeSlots(threeDaysBeforeSeconds, currentDateSeconds);
            return;
        }
        selectedTimeIsHaveVideo(timeStamp);
    }

    @Override
    public void moveTo(String data, boolean isLeftScroll, long timeStamp) {
        showTimeScroll(data.substring(11), isLeftScroll);//toast显示时间
    }

    @UiThread
    void showTimeScroll(final String time, final boolean isLeft) {
        if (TextUtils.isEmpty(time)) {
            return;
        }
        tvTimeScroll.setVisibility(View.VISIBLE);
        tvTimeScroll.setText(time);
        if (isLeft) {
            if (drawableLeft == null) {
                drawableLeft = ContextCompat.getDrawable(this, R.mipmap.ic_fast_forward);
            }
            tvTimeScroll.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        } else {
            if (drawableRight == null) {
                drawableRight = ContextCompat.getDrawable(this, R.mipmap.ic_forward);
            }
            tvTimeScroll.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        }
    }

}
