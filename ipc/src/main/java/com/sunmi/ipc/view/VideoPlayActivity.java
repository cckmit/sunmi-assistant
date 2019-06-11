package com.sunmi.ipc.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datelibrary.DatePickDialog;
import com.datelibrary.OnSureLisener;
import com.datelibrary.bean.DateType;
import com.google.gson.JsonObject;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.ApCloudTimeBean;
import com.sunmi.ipc.model.TimeBean;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.sunmi.ipc.utils.TimeView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.VerticalSeekBar;

/**
 * Description:
 * Created by bruce on 2019/4/11.
 */
@EActivity(resName = "activity_video_play")
public class VideoPlayActivity extends BaseActivity
        implements SurfaceHolder.Callback, IOTCClient.Callback,
        SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    @ViewById(resName = "rl_screen")
    RelativeLayout rlScreen;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "sv_cloud")
    SurfaceView svCloud;
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
    @ViewById(resName = "tv_hd_quality")
    TextView tvHDQuality;//高清画质
    @ViewById(resName = "tv_sd_quality")
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
    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;
    @ViewById(resName = "my_view")
    TimeView timeView;//时间绘制
    @ViewById(resName = "iv_setting")
    ImageView ivSetting;//设置
    @ViewById(resName = "sb_zoom")
    SeekBar sbZoom;
    @Extra
    String UID;

    //手机屏幕的宽高
    private int screenW, screenH;

    private int currFocus;

    //获取recyclerView width
    private int rvWidth;

    private H264Decoder videoDecoder = null;
    private AACDecoder audioDecoder = null;
    private VolumeHelper volumeHelper = null;
    private LinearLayoutManager linearLayoutManager;

    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isCloudPlayBack;//是否正在云回放
    private boolean isDevPlayBack;//是否正在设备回放
    private boolean isPaused;//回放是否暂停
    private boolean isCurrentLive;//当前是否直播
    private int qualityType = 0;//0-超清，1-高清

    private SurfaceHolder surfaceHolder, shCloud;
    //adapter
    private DateAdapter adapter;
    //日历
    private Calendar calendar;
    //选择视频日期列表
    private List<TimeBean> list = new ArrayList<>();
    //绘制的小时列表
    private List<String> dateList = new ArrayList<>();
    //当前时间 ，三天前秒数 ，未来6小时后的秒数 ，区间总共秒数
    private long currentDateSeconds, threeDaysBeforeSeconds, sixHoursAfterSeconds, minutesTotal;
    //3天秒数
    private long threeDaysSeconds = 3 * 24 * 60 * 60;
    //6小时后的秒数
//    private int sixHoursSeconds = 6 * 60 * 60;
    private int sixHoursSeconds = 12 * 60 * 60;
    private int tenMinutes = 10 * 60;
    //当前分钟走的秒数
    private int currentSecond;
    //刻度尺移动定时器
    private Timer moveTimer;
    //滑动停止的时间戳
    private long scrollTime;
    //当前的itemPosition
    private int currentItemPosition;
    //是否往左滑动
    private boolean isLeftScroll;
    //是否为选择的日期
    private boolean isSelectedDate;
    //是否自动滚动
    private boolean isAutoScroll;

    //云端回放
    //用于播放视频的mediaPlayer对象
    private MediaPlayer
            firstPlayer,//负责播放进入视频播放界面后的第一段视频
            nextMediaPlayer, //负责一段视频播放结束后，播放下一段视频
            cachePlayer,     //负责setNextMediaPlayer的player缓存对象
            currentPlayer;   //负责当前播放视频段落的player对象
    //存放所有视频端的url
    private List<VideoListResp.VideoBean> videoListQueue = new ArrayList<>();
    //所有player对象的缓存
    private HashMap<String, MediaPlayer> playersCache = new HashMap<>();
    //当前播放到的视频段落数
    private int currentVideoIndex;

    //设备id
//    private int deviceId = 2237;
//    private int deviceId = 2223;
    private int deviceId = 2239;


    //屏幕控件自动隐藏计时器
    private Timer screenHideTimer = null;
    private TimerTask screenHideTimerTask = null;
    private int countdown;

    //重置倒计时
    private void resetCountdown() {
        countdown = 0;
    }

    //开启计时
    private void startScreenHideTimer() {
        stopScreenHideTimer();
        isAutoScroll = true;
        screenHideTimer = new Timer();
        screenHideTimer.schedule(screenHideTimerTask = new TimerTask() {
            @Override
            public void run() {
                countdown++;
                if (countdown == 20) {
                    hideControlBar();
                    isControlPanelShow = false;
                    stopScreenHideTimer();
                }
            }
        }, 0, 1000);
    }

    // 停止计时
    private void stopScreenHideTimer() {
        resetCountdown();
        if (screenHideTimer != null) {
            screenHideTimer.cancel();
            screenHideTimer = null;
        }
        if (screenHideTimerTask != null) {
            screenHideTimerTask.cancel();
            screenHideTimerTask = null;
        }
    }

    @AfterViews
    void init() {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        screenW = CommonHelper.getScreenWidth(context);
        screenH = CommonHelper.getScreenHeight(context);
        sbZoom.setOnSeekBarChangeListener(this);
        rlScreen.setOnTouchListener(this);
        setTextViewTimeDrawable();
        //当前天
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tvCalender.setText(day > 9 ? day + "" : "0" + day);
        //初始化recyclerView
        layoutManger();
        getRecyclerViewWidth();
        recyclerViewAddOnScrollListener();
        showTimeList(false, listAp);
        scrollCurrentTime(); //滚动到当前时间
        //refreshCanvasList();//渲染

        //设置播放器的宽高
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = isSS1() ? screenH : screenW;
        lp.height = screenH;
        videoView.setLayoutParams(lp);

        ViewGroup.LayoutParams lpCloud = svCloud.getLayoutParams();
        lpCloud.width = isSS1() ? screenH : screenW;
        lpCloud.height = screenH;
        svCloud.setLayoutParams(lpCloud);
        shCloud = svCloud.getHolder();

        //回调
        IOTCClient.setCallback(this);
        surfaceHolder = videoView.getHolder();// SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        audioDecoder = new AACDecoder();
        //初始化音量
        adjustVoice();
        initGetVolume();
    }

    private boolean isSS1() {
        return true;//todo
    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoDecoder != null)
            videoDecoder.release();
        cloudPlayDestroy();//关闭云端视频
        IOTCClient.close();
    }

    //开始直播
    @Background
    void initP2pLive() {
        IOTCClient.init(UID);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        IPCCall.getInstance().fsZoom(seekBar.getProgress(), context);
    }

    @Click(resName = "tv_add")
    void addClick() {
        if (currFocus > 775) {
            shortTip("已到最大焦距");
            return;
        }
        IPCCall.getInstance().fsFocus(currFocus += 5, context);
    }

    @Click(resName = "tv_minus")
    void minusClick() {
        if (currFocus < 5) {
            shortTip("已到最小焦距");
            return;
        }
        IPCCall.getInstance().fsFocus(currFocus -= 5, context);
    }

    @Click(resName = "tv_auto")
    void autoFocusClick() {
        IPCCall.getInstance().fsAutoFocus(context);
    }

    @Click(resName = "btn_next")
    void nextClick() {
        LogCat.e(TAG, "addClick");
        if (screenH <= 0 || screenW <= 0) {
            shortTip("已到最小焦距");
            return;
        }
        //        IPCCall.getInstance().fsReset(context);
        IPCCall.getInstance().fsIrMode(0, context);
        float currX = 540;
        shortTip("x = " + (int) currX * 100 / screenW);
        float currY = 860;
        shortTip("y = " + (int) currY * 100 / screenH);
        IPCCall.getInstance().fsSetFocusPoint((int) currX * 100 / screenW,
                (int) currY * 100 / screenH, context);
    }

    @Click(resName = "rl_video_back")
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
            if (currentVolume100 == 0) {
                ivVolume.setBackgroundResource(R.mipmap.ic_muse);
            } else {
                ivVolume.setBackgroundResource(R.mipmap.ic_volume);
            }
        }
    }

    //画质
    @Click(resName = "tv_quality")
    void qualityClick() {
        if (isDevPlayBack) return;
        llVideoQuality.setVisibility(llVideoQuality.isShown() ? View.GONE : View.VISIBLE);
        if (qualityType == 0) {
            tvHDQuality.setTextColor(getResources().getColor(R.color.colorOrange));
            tvSDQuality.setTextColor(getResources().getColor(R.color.c_white));
        } else {
            tvHDQuality.setTextColor(getResources().getColor(R.color.c_white));
            tvSDQuality.setTextColor(getResources().getColor(R.color.colorOrange));
        }
    }

    //超清画质
    @Click(resName = "tv_hd_quality")
    void hdQualityClick() {
        tvQuality.setText(R.string.str_FHD);
        changeQuality(0);
    }

    //高清画质
    @Click(resName = "tv_sd_quality")
    void sdQualityClick() {
        tvQuality.setText(R.string.str_HD);
        changeQuality(1);
    }

    //开始，暂停
    @Click(resName = "iv_play")
    void playLiveClick() {
        if (!isDevPlayBack && !isCloudPlayBack && isCurrentLive) return;
        if (isFastClick(1000)) return;
        if (isPaused) {
            ivPlay.setBackgroundResource(R.mipmap.pause_normal);
            if (isCloudPlayBack) cloudPlayIsStart();
        } else {
            ivPlay.setBackgroundResource(R.mipmap.play_normal);
            if (isCloudPlayBack) cloudPlayIsStart();
        }
        isPaused = !isPaused;
        if (isDevPlayBack) {
            IOTCClient.pausePlayback(isPaused);
        }
    }

    //cloud播放 start/pause
    private void cloudPlayIsStart() {
        try {
            if (currentPlayer != null) {
                if (currentPlayer.isPlaying())
                    currentPlayer.pause();
                else
                    currentPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (isFastClick(1000)) return;
        DatePickDialog dialog = new DatePickDialog(this);
        if (scrollTime > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String d = format.format(scrollTime);
            try {
                Date date = format.parse(d);
                dialog.setStartDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //设置上下年分限制
        dialog.setYearLimt(100);
        //设置标题
        dialog.setTitle(R.string.str_select_time);
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                onSureButton(date);
            }
        });
        dialog.show();
    }

    //点击屏幕
    @Click(resName = "rl_screen")
    void screenClick() {
        if (isControlPanelShow) {
            hideControlBar();
            isControlPanelShow = false;
        } else {
            rlTopBar.setVisibility(View.VISIBLE);
            rlBottomBar.setVisibility(View.VISIBLE);
            isControlPanelShow = true;
        }
    }

    @UiThread
    void hideControlBar() {
        rlTopBar.setVisibility(View.GONE);
        rlBottomBar.setVisibility(View.GONE);
        llChangeVolume.setVisibility(View.GONE);//音量
        llVideoQuality.setVisibility(View.GONE);//画质
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isControlPanelShow) startScreenHideTimer();
                break;
        }
        return false;
    }

    /**
     * 切回直播
     */
    private void switch2Live() {
        isCurrentLive = true; //直播
        ivPlay.setBackgroundResource(R.mipmap.play_disable);
        ivLive.setVisibility(View.GONE);
        if (!isCloudPlayBack && !isDevPlayBack) {
            LogCat.e(TAG, "6666666 switch2Live");
            scrollCurrentLive();
            return;
        }
        showLoadingDialog();
        //如果是云端回放此时需要调用停止操作然后直播
        if (isCloudPlayBack) {
            cloudPlayDestroy();
//            videoDecoder.initMediaCodec();
            svCloud.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            isCloudPlayBack = false;
        }
        IOTCClient.startPlay();
        scrollCurrentLive();
        isDevPlayBack = false;
        hideLoadingDialog();
    }

    /**
     * 切到设备回放
     */
    void switch2DevPlayback(long start) {
        showLoadingDialog();
        if (isCloudPlayBack) {
            LogCat.e(TAG, "6666666 switch2DevPlayback");
            cloudPlayDestroy();
            svCloud.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
//            videoDecoder.initMediaCodec();
            isCloudPlayBack = false;
        }
        IOTCClient.startPlayback(start);
        isCurrentLive = false;
        isDevPlayBack = true;
        ivLive.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    void switch2CloudPlayback(long start, long end) {
        showLoadingDialog();
        if (!isCloudPlayBack) {
            if (isDevPlayBack) {
                IOTCClient.stopPlayback();//先停止设备回放
                isDevPlayBack = false;
            } else {
                IOTCClient.stopLive();//先停止直播
            }
            LogCat.e(TAG, "6666666 switch2CloudPlayback");
            svCloud.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
//            if (videoDecoder != null) {
//                videoDecoder.release();//释放surfaceView
//            }
            isCloudPlayBack = true;
        }
        isCurrentLive = false;
        ivLive.setVisibility(View.VISIBLE);
        getCloudVideoUrls(start, end);
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

    //获取当前音量
    private void initGetVolume() {
        int currentVolume100 = volumeHelper.get100CurrentVolume();
        if (currentVolume100 == 0) {
            ivVolume.setBackgroundResource(R.mipmap.ic_muse);
        } else {
            ivVolume.setBackgroundResource(R.mipmap.ic_volume);
        }
    }

    private void changeQuality(int type) {
        llVideoQuality.setVisibility(View.GONE);
        if (type == qualityType) return;
        qualityType = qualityType == 0 ? 1 : 0;
        IOTCClient.changeValue(qualityType);
        if (qualityType == 0) {
            shortTip(R.string.tip_video_quality_fhd);
        } else if (qualityType == 1) {
            shortTip(R.string.tip_video_quality_hd);
        }
    }

    //*********************************************************************
    //***********************云端回放***************************************
    //*********************************************************************

    /*
     * 初始化播放首段视频的player
     */
    private void initFirstPlayer() {
        LogCat.e(TAG, "55555555 11 cloud onSuccess");
        cloudPlayDestroy();//初始化之前销毁
        LogCat.e(TAG, "55555555 22 cloud onSuccess");
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(shCloud);
        LogCat.e(TAG, "55555555 33 cloud onSuccess");
        firstPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        onVideoPlayCompleted(mp);
                    }
                });

        //设置cachePlayer为该player对象
        cachePlayer = firstPlayer;
        initNextPlayer();
    }

    private void startPlayFirstVideo() {
        LogCat.e(TAG, "55555555 44 cloud onSuccess");
        if (videoListQueue.size() <= 0) return;
        try {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
                firstPlayer.release();
                firstPlayer = new MediaPlayer();
            }
            LogCat.e(TAG, "55555555 55 cloud onSuccess");
            firstPlayer.setDataSource(videoListQueue.get(currentVideoIndex).getUrl());
            firstPlayer.prepareAsync();
            firstPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    firstPlayer.start();
                    hideLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 新开线程负责初始化负责播放剩余视频分段的player对象,避免UI线程做过多耗时操作
     */
    private void initNextPlayer() {
        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {

                //player对象初始化完成后，开启播放
                startPlayFirstVideo();
                LogCat.e(TAG, "55555555 66 cloud onSuccess");
                for (int i = 1; i < videoListQueue.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextMediaPlayer.setOnCompletionListener(
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    onVideoPlayCompleted(mp);
                                }
                            });
                    try {
                        nextMediaPlayer.setDataSource(videoListQueue.get(i).getUrl());
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //set next mediaplayer
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    //set new cachePlayer
                    cachePlayer = nextMediaPlayer;
                    //put nextMediaPlayer in cache
                    playersCache.put(String.valueOf(i), nextMediaPlayer);
                }
                LogCat.e(TAG, "55555555 77 cloud onSuccess");
            }
        });
    }

    /*
     * 负责处理一段视频播放过后，切换player播放下一段视频
     */
    private void onVideoPlayCompleted(MediaPlayer mp) {
        mp.setDisplay(null);
        //get next player
        currentPlayer = playersCache.get(String.valueOf(++currentVideoIndex));
        if (currentPlayer != null) {
            currentPlayer.setDisplay(shCloud);
        } else {
            shortTip("视频播放完毕");
        }
    }

    private void getCloudVideoUrls(long start, long end) {
        IPCCloudApi.getVideoList(deviceId, start, end, new RetrofitCallback<VideoListResp>() {
            @Override
            public void onSuccess(int code, String msg, VideoListResp data) {
                videoListQueue.clear();
                videoListQueue = data.getVideo_list();
                initFirstPlayer();
            }

            @Override
            public void onFail(int code, String msg, VideoListResp data) {
                hideLoadingDialog();
            }
        });
    }

    /*
     * 负责界面销毁时，release各个mediaPlayer
     */
    private void cloudPlayDestroy() {
        try {
            if (firstPlayer != null) {
                if (firstPlayer.isPlaying()) {
                    firstPlayer.stop();
                }
                firstPlayer.release();
            }
            if (nextMediaPlayer != null) {
                if (nextMediaPlayer.isPlaying()) {
                    nextMediaPlayer.stop();
                }
                nextMediaPlayer.release();
            }

            if (currentPlayer != null) {
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop();
                }
                currentPlayer.release();
            }
            currentPlayer = null;
            playersCache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //***********************云端回放***************************************!
    //*********************************************************************

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        videoDecoder = new H264Decoder(holder.getSurface(), 0);
        initP2pLive();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭操作
        if (videoDecoder != null) {
            videoDecoder.stopRunning();
            videoDecoder = null;
        }
    }

    @Override
    public void onVideoReceived(byte[] videoBuffer) {
        if (videoDecoder != null)
            videoDecoder.setVideoData(videoBuffer);
    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        audioDecoder.setAudioData(audioBuffer);
    }


    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.fsAutoFocus, IpcConstants.fsFocus, IpcConstants.fsGetStatus,
                IpcConstants.fsIrMode, IpcConstants.fsReset, IpcConstants.fsZoom};
    }

    @UiThread
    void setSeekBarProgress(int progress) {
        sbZoom.setProgress(progress);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (res == null) return;
        if (id == IpcConstants.fsGetStatus) {
            //{"data":[{"opcode":"0x3109","result":{"zoom":0,"max_zoom":500,"max_focus":780,
            // "irmode":0,"auto_focus_start":0,"focus":389},"errcode":1}],"msg_id":"11111","errcode":1}
            try {
                JSONObject jsonObject = res.getResult();
                if (jsonObject.has("zoom")) {
                    int currZoom = jsonObject.getInt("zoom");
                    setSeekBarProgress(currZoom);
                }
                if (jsonObject.has("focus")) {
                    currFocus = jsonObject.getInt("focus");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == IpcConstants.fsAutoFocus) {
            LogCat.e(TAG, "666666,222res = " + res.toString());
        } else if (id == IpcConstants.fsReset) {
        }
    }

    //调节音量
    private void adjustVoice() {
        volumeHelper = new VolumeHelper(this);
        //int systemCurrent = volumeHelper.getSystemCurrentVolume();
        //int systemMax = volumeHelper.getSystemMaxVolume();
        int currentVolume100 = volumeHelper.get100CurrentVolume();
        sBarVoice.setMax(100);
        sBarVoice.setProgress(currentVolume100);
        sBarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    ivVolume.setBackgroundResource(R.mipmap.ic_muse);
                } else {
                    ivVolume.setBackgroundResource(R.mipmap.ic_volume);
                }
                volumeHelper.setVoice100(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //按键控制音量，return true时不显示系统音量 return false时显示系统音量
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            llChangeVolume.setVisibility(View.GONE);
            //获取当前音量
            int currentVolume100 = volumeHelper.get100CurrentVolume();
            if (currentVolume100 == 0) {
                ivVolume.setBackgroundResource(R.mipmap.ic_muse);
            } else {
                ivVolume.setBackgroundResource(R.mipmap.ic_volume);
            }
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeMove();//关闭时间抽的timer
        stopScreenHideTimer();//关闭屏幕控件自动hide计时器
    }

    /**
     * *******************时间滑动条***************************
     */

    //初始 recyclerView
    private void layoutManger() {
        //布局管理器
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        //当前时间秒数
        currentDateSeconds = System.currentTimeMillis() / 1000;
        //三天前秒数
        threeDaysBeforeSeconds = currentDateSeconds - threeDaysSeconds;
        //6小时后的秒数
        sixHoursAfterSeconds = currentDateSeconds + sixHoursSeconds;
        //区间总共秒数 --当前时间前三天+未来6小时的秒数
        minutesTotal = threeDaysSeconds + sixHoursSeconds;
        //当前分钟走的秒数
        currentSecond = calendar.get(Calendar.SECOND);
    }

    //开始移动
    public void openMove() {
        if (moveTimer != null) {
            moveTimer.cancel();
            moveTimer = null;
        }
        moveTimer = new Timer();
        moveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogCat.e(TAG, "openMove");
                        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        if (firstVisibleItem < 0) return;
                        linearLayoutManager.scrollToPositionWithOffset(currentItemPosition++, 0);
                        canvasHours(firstVisibleItem); //绘制时间点和偏移量
                    }
                });
            }
//        }, 0, 1000);//一分钟轮询一次
        }, 0, 1000 * 60);//一分钟轮询一次
    }

    //结束移动
    public void closeMove() {
        if (moveTimer != null) {
            moveTimer.cancel();
            moveTimer = null;
        }
    }

    //日期列表，是否选择日期列表
    private void timeList(List<TimeBean> list, boolean isSelectedDate) {
        list.clear();
        TimeBean bean;
        if (isSelectedDate) {//选择日期
            for (int i = 0; i < minutesTotal; i += 60) {
                bean = new TimeBean();
                bean.setDate(threeDaysBeforeSeconds + i);
                list.add(bean);
            }
        } else {//当前日期
            for (int i = 0; i < minutesTotal; i += 60) {//10分钟一个item
                bean = new TimeBean();
                bean.setDate(threeDaysBeforeSeconds - currentSecond + i);
                list.add(bean);
            }
        }
    }

    /**
     * 时间列表
     *
     * @param isSelectedDate 是否选择日期列表
     */
    private void showTimeList(boolean isSelectedDate, List<ApCloudTimeBean> apCloudList) {
        //添加list
        timeList(list, isSelectedDate);
        //日历DateAdapter
        adapter = new DateAdapter(list, apCloudList);
        recyclerView.setAdapter(adapter);

        //开启控件隐藏倒计时
        startScreenHideTimer();
    }

    //渲染时间轴并滚动到指定时间
    private void timeCanvasList(final List<ApCloudTimeBean> apCloudList) {
        Objects.requireNonNull(this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new DateAdapter(list, apCloudList);
                recyclerView.setAdapter(adapter);

                if (!isFirstScroll && !isSelectedDate) {
                    selectedTimeIsHaveVideo(firstLeftScrollCurrentTime); //初始化左滑渲染及回放
                } else {
                    if (isSelectedDate) {
                        scrollSelectedDate0AM();  //滑动到选择日期的0.00点
                    } else {
                        scrollCurrentTime(); //滚动到当前时间
                    }
                }
            }
        });
    }

    //选择日历日期回调
    @SuppressLint("DefaultLocale")
    public void onSureButton(Date date) {
        resetCountdown();//重置隐藏控件计时
        long currentTime = System.currentTimeMillis() / 1000;//当前时间戳秒
        scrollTime = date.getTime();//选择日期的时间戳毫秒
        long time = scrollTime / 1000; //设置日期的秒数
        if (time > currentTime) {//未来时间或当前--滑动当前直播
            LogCat.e(TAG, "6666666 33 live forward " + "time=" + time + " ,currentTime=" + currentTime);
            isSelectedDate = false;
            tvCalender.setText(String.format("%td%n", date));
            switch2Live();
        } else {//回放时间
            LogCat.e(TAG, "6666666 44 back");
            isFirstScroll = false;//非首次滑动
            isSelectedDate = true;
            isCurrentLive = false; //回放
            ivPlay.setBackgroundResource(R.mipmap.pause_normal);
            ivLive.setVisibility(View.VISIBLE);

            String strDate = secondToDate(time, "yyyy-MM-dd");
            int year = Integer.valueOf(strDate.substring(0, 4));
            int month = Integer.valueOf(strDate.substring(5, 7));
            int day = Integer.valueOf(strDate.substring(8, 10));
            //显示日历天数
            tvCalender.setText(String.format("%td%n", date));

            //设置选择日期的年月日0时0分0秒
            calendar.clear();
            calendar.set(year, month - 1, day, 0, 0, 0);//设置时候月份减1即是当月
            long selectedDate = calendar.getTimeInMillis() / 1000;//设置日期的秒数
            //当前时间秒数
            currentDateSeconds = System.currentTimeMillis() / 1000;
            //选择日期三天前的秒数
            threeDaysBeforeSeconds = selectedDate - threeDaysSeconds;
            //区间总共秒数
            minutesTotal = currentDateSeconds - selectedDate + threeDaysSeconds + sixHoursSeconds;
            //加载时间轴无渲染
            showTimeList(true, null);
            //滑动到选择日期的0.00点
            scrollSelectedDate0AM();
            refreshCanvasList();//渲染
        }
    }

    //获取RecyclerView Width
    private void getRecyclerViewWidth() {
        ViewTreeObserver vto = recyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                rvWidth = recyclerView.getWidth();
            }
        });
    }

    //滑动选择日期的0点
    private void scrollSelectedDate0AM() {
        //滚动到中间
        long leftToCenterMinutes = CommonHelper.px2dp(this, rvWidth / 2);//中间距离左侧屏幕的分钟
        long threeDaysBeforeDate = 3 * 24 * 60;//3天分钟数
        currentItemPosition = (int) (threeDaysBeforeDate - leftToCenterMinutes);
        linearLayoutManager.scrollToPositionWithOffset(currentItemPosition, 0);
        openMove();
    }

    //获取视频跳转播放的currentItemPosition
    private void videoSkipScrollPosition(long currentTimeMinutes) {
        long leftToCenterMinutes = CommonHelper.px2dp(this, rvWidth / 2);//中间距离左侧屏幕的分钟
        currentItemPosition = (int) (currentTimeMinutes / 60 - threeDaysBeforeSeconds / 60 - leftToCenterMinutes);
        linearLayoutManager.scrollToPositionWithOffset(currentItemPosition, 0);
    }

    //滑动回放定位的中间 position
    private void scrollCurrentPlayBackTime(long currentTimeMinutes) {
        //滚动到中间
        long leftToCenterMinutes = CommonHelper.px2dp(this, rvWidth / 2);//中间距离左侧屏幕的分钟
        currentItemPosition = (int) (currentTimeMinutes / 60 - threeDaysBeforeSeconds / 60 - leftToCenterMinutes);
        linearLayoutManager.scrollToPositionWithOffset(currentItemPosition, 0);

        openMove();
    }

    //直播boolean状态
    private void setPlayBooleanStatus() {
        isDevPlayBack = false;//dev
        isCloudPlayBack = false;//cloud
        isCurrentLive = true; //直播
    }

    //初始化延时滑动当前时间
    private void scrollCurrentTime() {
        setPlayBooleanStatus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //中间距离左侧屏幕的分钟
                long leftToCenterMinutes = CommonHelper.px2dp(VideoPlayActivity.this, rvWidth / 2);
                LogCat.e(TAG, "leftToCenterMinutes=" + leftToCenterMinutes);
                long currentMinutes = (minutesTotal - sixHoursSeconds) / 60 - leftToCenterMinutes;//初始化无偏移量
                currentItemPosition = (int) currentMinutes;//当前的item
                linearLayoutManager.scrollToPositionWithOffset((int) (currentMinutes + 1), 0);

                openMove();
            }
        }, 500);
    }

    /**
     * 点击直播按钮滑动到当前时间
     */
    private void scrollCurrentLive() {
        setPlayBooleanStatus();
        //当前时间秒数
        long nowMinute = System.currentTimeMillis() / 1000;
        //初始化当前的秒数和现在的秒数时间戳对比相差的偏移量--比对分钟数
        long offsetMinutes = nowMinute / 60 - currentDateSeconds / 60;

        //中间距离左侧屏幕的分钟
        long leftToCenterMinutes = CommonHelper.px2dp(VideoPlayActivity.this, rvWidth / 2);
        long currentMinutes = (minutesTotal - sixHoursSeconds) / 60 - leftToCenterMinutes + offsetMinutes;//点击直播+偏移量offsetMinutes
        currentItemPosition = (int) currentMinutes;//当前的item
        linearLayoutManager.scrollToPositionWithOffset((int) (currentMinutes), 0);

        openMove();
    }

    private void rightNowScrollCurrentPosition(long currentTimeSecond) {
        //当前时间秒数
        //long currentTimeSecond = System.currentTimeMillis() / 1000;
        //初始化当前的秒数和现在的秒数时间戳对比相差的偏移量--比对分钟数
        long offsetMinutes = currentTimeSecond / 60 - currentDateSeconds / 60;
        //中间距离左侧屏幕的分钟
        long leftToCenterMinutes = CommonHelper.px2dp(VideoPlayActivity.this, rvWidth / 2);
        long currentMinutes = (minutesTotal - sixHoursSeconds) / 60 - leftToCenterMinutes + offsetMinutes;//点击直播+偏移量offsetMinutes
        currentItemPosition = (int) currentMinutes;//当前的item
        linearLayoutManager.scrollToPositionWithOffset((int) (currentMinutes), 0);
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
                    LogCat.e(TAG, "55555555 00");
                    switch2Live();
                    return;
                }
                boolean isCloud = !listAp.get(i).isApPlay();
                if (isCloud) {
                    LogCat.e(TAG, "55555555 11");
                    switch2CloudPlayback(endOpposite, endOpposite + tenMinutes);
                } else {
                    LogCat.e(TAG, "55555555 22");
                    switch2DevPlayback(endOpposite);
                }
                scrollCurrentPlayBackTime(endOpposite);//回放到拖动的时间点

            } else if (currTime >= start && currTime < end) {//视频区域
                boolean isCloud = !listAp.get(i).isApPlay();
                if (isCloud) {
                    LogCat.e(TAG, "55555555 44");
                    switch2CloudPlayback(currTime, currTime + tenMinutes);
                } else {
                    LogCat.e(TAG, "55555555 55");
                    switch2DevPlayback(currTime);
                }
                scrollCurrentPlayBackTime(currTime);//回放到拖动的时间点
            }
        }
    }

    //非第一次滑动停止
    private boolean isFirstScroll = true;
    //第一次向左滑动的中心点时间
    private long firstLeftScrollCurrentTime;

    //可视时间轴中间对应的时间
    private long centerCurrentTime(int firstVisibleItem) {
        //int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        int center = (lastVisibleItem - firstVisibleItem) / 2 + firstVisibleItem + 1;
        TimeBean bs = list.get(center);
        return bs.getDate();
    }

    //recyclerView 滑动监听
    private void recyclerViewAddOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//停止滑动
                    LogCat.e("TAG", "onScrolled00 " + ", isCloudPlayBack=" + isCloudPlayBack + " ,isDevPlayBack=" + isDevPlayBack);
                    resetCountdown();//重置隐藏控件计时
                    //center date
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    long currTime = centerCurrentTime(firstVisibleItem);//当前中间轴时间
                    //首次向左滑动请求+渲染
                    if (isFirstScroll && isLeftScroll) {
                        firstLeftScrollCurrentTime = currTime;
                        refreshCanvasList();//渲染
                        isFirstScroll = false;
                        isCurrentLive = false; //回放
                        return;
                    }
                    String strDate = secondToDate(currTime, "yyyy-MM-dd HH:mm:ss");
                    String day = strDate.substring(8, 11);
                    tvCalender.setText(String.format(" %s", day));  //滑动停止显示日期
                    scrollTime = currTime * 1000;//滑动日历的时间戳毫秒
                    String hourMinuteSecond = strDate.substring(11, strDate.length());
                    toastForShort(VideoPlayActivity.this, hourMinuteSecond, isLeftScroll);//toast显示时间
                    canvasHours(firstVisibleItem);//绘制时间轴
                    long currentSeconds = System.currentTimeMillis() / 1000;//当前时间戳秒
                    //停止到未来时间
                    if (currTime > currentSeconds && currTime - currentSeconds > 1) {
                        switch2Live();  //滚动到当前直播
                        return;
                    }
                    //无回放视频跳转当前
                    if (listAp == null || listAp.size() == 0) {
                        LogCat.e(TAG, "11111 no video");
                        rightNowScrollCurrentPosition(currentSeconds);
                        return;
                    }
                    //拖动或选择的时间是否有video（ap或cloud）
                    selectedTimeIsHaveVideo(currTime);
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING ||
                        newState == RecyclerView.SCROLL_STATE_SETTLING) {//拖动和自动滑动
                    isSelectedDate = false;//手动拖动或自动滑动
                    isAutoScroll = false;//非自动滑动
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    long currTime = centerCurrentTime(firstVisibleItem);//当前中间轴时间
                    long currentSeconds = System.currentTimeMillis() / 1000;//当前时间戳秒
                    LogCat.e(TAG, "date=" + currTime + ", currentSeconds" + currentSeconds);
                    if (currTime < currentSeconds && currentSeconds - currTime > 1) {
                        //回放时间
                        ivPlay.setBackgroundResource(R.mipmap.pause_normal);
                        ivLive.setVisibility(View.VISIBLE);
                        isCurrentLive = false;
                    } else {
                        //当前时间、未来时间
                        ivPlay.setBackgroundResource(R.mipmap.play_disable);
                        ivLive.setVisibility(View.GONE);
                        isCurrentLive = true;
                    }
                    isPaused = false;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogCat.e("TAG", "onScrolled33=" + dx + " dy=" + dy);
                isLeftScroll = dx <= 0; //dx < 0左边滑动  dx > 0右边滑动
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                long currTime = centerCurrentTime(firstVisibleItem);//当前中间轴时间
                String strDate = secondToDate(currTime, "yyyy-MM-dd HH:mm:ss");
                String day = strDate.substring(8, 11);
                tvCalender.setText(String.format(" %s", day));
                scrollTime = currTime * 1000;//滑动日历的时间戳毫秒

                canvasHours(firstVisibleItem);//绘制时间
                if (isAutoScroll) switch2Playback(currTime);//自动滑动时下一个视频ap还是cloud播放
                if (isSelectedDate) {
                    if (listAp == null || listAp.size() == 0) return;
                    selectedTimeIsHaveVideo(currTime);
                    LogCat.e(TAG, "1111122 have video");
                }
            }
        });
    }

    private void switch2Playback(long currTime) {
        if (!isCloudPlayBack && !isDevPlayBack && isCurrentLive) return;
        int availableVideoSize = listAp.size();
        for (int i = 0; i < availableVideoSize; i++) {
            ApCloudTimeBean bean = listAp.get(i);
            long start = bean.getStartTime();
            long end = bean.getEndTime();
            //当滑动到最后前后一分钟时，判断下一个视频片段ap还是cloud
            if (currTime >= start && currTime < end && end - currTime < 60) {
                if (i == availableVideoSize - 1) {//todo 最后一个，需要渲染后面的数据
//                    refreshCanvasList();//i是最后一个，基于i的end作为start再拉7天的数据。
                    LogCat.e(TAG, "22222222222222 11");
                } else {
                    boolean isCloud = !listAp.get(i + 1).isApPlay();
                    int delayMillis = (int) end - currTime < 0 ? 1 : (int) (end - currTime);
                    LogCat.e(TAG, "22222222222222 22" + " end - currTime=" + (end - currTime));
                    final int finalI = i;
                    if (isCloud) {
                        LogCat.e(TAG, "22222222222222 33");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch2CloudPlayback(listAp.get(finalI + 1).getStartTime(),
                                        listAp.get(finalI + 1).getStartTime() + tenMinutes);
                                videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime()); //偏移跳转
                            }
                        }, delayMillis * 1000);
                    } else {
                        LogCat.e(TAG, "22222222222222 44");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch2DevPlayback(listAp.get(finalI + 1).getStartTime());
                                videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime());//偏移跳转
                            }
                        }, delayMillis * 1000);
                    }
                }
            }
        }
    }

    //绘制时间
    private void canvasHours(int firstVisibleItem) {
        if (firstVisibleItem < 0) return;
        TimeBean bs = list.get(firstVisibleItem);
        String str = secondToDate(bs.getDate(), "HH:mm:ss");
        int hour = Integer.valueOf(str.substring(0, 2));
        int minute = Integer.valueOf(str.substring(3, 5));
        //可见第一个item距离第一个可见长条小时的偏移量
        int offsetPx;
        if (minute == 0) {
            offsetPx = 0;
        } else {
            offsetPx = (60 - minute) * (int) getResources().getDimension(R.dimen.dp_1);
            hour++;
        }
        //绘制下方时间
        dateList.clear();
        for (int i = 0; i < 10; i++) {
            if (hour <= 23) {
                dateList.add((hour++) + ":00");
            } else {
                hour = 0;
            }
            timeView.refresh(dateList, offsetPx);
        }
    }

    //date adapter
    public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
        private List<TimeBean> list;
        private List<ApCloudTimeBean> apCloudList;//组合时间轴

        DateAdapter(List<TimeBean> list, List<ApCloudTimeBean> apCloudList) {
            this.list = list;
            this.apCloudList = apCloudList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLine;
            RelativeLayout rlItem;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvLine = itemView.findViewById(R.id.tv_line);
                rlItem = itemView.findViewById(R.id.rl_item);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_time, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            TimeBean bean = list.get(i);
            long date = bean.getDate();
            String str = secondToDate(date, "HH:mm:ss");
            String minuteSecond = str.substring(3, 8);
            String hour = str.substring(3, 5);

            //当前时间线的高度
            ViewGroup.LayoutParams lp = viewHolder.tvLine.getLayoutParams();
            lp.width = CommonHelper.dp2px(context, (float) 0.8);
            if (hour.contains("00")) {
                lp.height = CommonHelper.dp2px(context, 8);
                viewHolder.tvLine.setVisibility(View.VISIBLE);

            } else {
                lp.height = CommonHelper.dp2px(context, 4);
                viewHolder.tvLine.setVisibility(showShortTimeLine(minuteSecond)
                        ? View.VISIBLE : View.INVISIBLE);
            }
            viewHolder.tvLine.setLayoutParams(lp);
            //渲染
            viewHolder.rlItem.setBackgroundResource(R.color.transparent);
            if (apCloudList != null) {
                for (int j = 0; j < apCloudList.size(); j++) {
                    if (date > apCloudList.get(j).getStartTime() && date < apCloudList.get(j).getEndTime()) {
                        viewHolder.rlItem.setBackgroundResource(R.color.colorOrangeLight);
//                        if (apCloudList.get(j).isApPlay())
//                            viewHolder.rlItem.setBackgroundResource(R.color.colorOrangeLight);
//                        else
//                            viewHolder.rlItem.setBackgroundResource(R.color.c_green);
                    }
                }
            }
        }

        private boolean showShortTimeLine(String minuteSecond) {
            return minuteSecond.contains("10:00") ||
                    minuteSecond.contains("20:00") ||
                    minuteSecond.contains("30:00") ||
                    minuteSecond.contains("40:00") ||
                    minuteSecond.contains("50:00");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    /**
     * 秒转换为指定格式的日期
     *
     * @param second
     * @param patten
     * @return
     */
    private String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten, Locale.getDefault());
        return format.format(date);
    }

    //自定义toast
    private Toast mToast;
    private Drawable drawableLeft, drawableRight;

    private void setTextViewTimeDrawable() {
        drawableLeft = getResources().getDrawable(R.mipmap.ic_fast_forward);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        drawableRight = getResources().getDrawable(R.mipmap.ic_forward);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
    }

    public void toastForShort(final Context context, final String msg, final boolean isLeft) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null || TextUtils.isEmpty(msg)) {
                    return;
                }
                View layout = LayoutInflater.from(context).inflate(R.layout.toast_item, null);
                TextView tvDate = layout.findViewById(R.id.tv_date);
                tvDate.setText(msg);
                if (isLeft)
                    tvDate.setCompoundDrawables(drawableLeft, null, null, null);
                else
                    tvDate.setCompoundDrawables(null, null, drawableRight, null);
                if (mToast == null) {
                    mToast = new Toast(context);
                    mToast.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL, 0, 0);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(layout);
                    mToast.show();
                    mToast = null;
                }
            }
        });
    }

    /*
     ****************************绘制时间轴*******************************************
     */
    private List<ApCloudTimeBean> listAp = new ArrayList<>();
    private List<ApCloudTimeBean> listCloud = new ArrayList<>();

    //发送请求获取组合时间轴
    private void refreshCanvasList() {
        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IOTCClient.getPlaybackList(threeDaysBeforeSeconds, currentDateSeconds); //获取AP回放时间列表
            }
        }, 2500);
    }

    @Override
    public void IOTCResult(String result) {
        LogCat.e(TAG, "111111 ap get result = " + result);
        try {
            JSONObject object = new JSONObject(result);
            int errcode = object.getInt("errcode");
            if (errcode == 0) {
                JSONArray array = object.getJSONArray("data");
                JSONObject object1 = (JSONObject) array.opt(0);
                int cmd = object1.getInt("cmd");
                if (cmd == 32) {//ap回放时间轴
                    if (!object1.has("result")) return;
                    JSONArray array1 = object1.getJSONArray("result");
                    ApCloudTimeBean ap;
                    listAp.clear();
                    for (int i = 0; i < array1.length(); i++) {
                        JSONObject object2 = (JSONObject) array1.opt(i);
                        ap = new ApCloudTimeBean();
                        ap.setStartTime(object2.getLong("start_time"));
                        ap.setEndTime(object2.getLong("end_time"));
                        ap.setApPlay(true);
                        listAp.add(ap);
                    }
                    ///获取cloud回放时间轴
                    getTimeList(deviceId, threeDaysBeforeSeconds, currentDateSeconds);
                }
            } else hideLoadingDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取cloud回放时间轴
    public void getTimeList(int deviceId, final long startTime, final long endTime) {
        IPCCloudApi.getTimeSlots(deviceId, startTime, endTime, new RetrofitCallback<JsonObject>() {
            @Override
            public void onSuccess(int code, String msg, JsonObject data) {
                LogCat.e(TAG, "111111 cloud getTimeSlots==" + data.toString());
                if (code == 1) {
                    try {
                        JSONObject object = new JSONObject(data.toString());
                        int total_count = object.getInt("total_count");
                        if (total_count == 0) {
                            hideLoadingDialog();
                            timeCanvasList(listAp);
                            return;
                        }
                        JSONArray jsonArray = object.getJSONArray("timeslots");
                        ApCloudTimeBean cloud;
                        listCloud.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object2 = (JSONObject) jsonArray.opt(i);
                            cloud = new ApCloudTimeBean();
                            cloud.setStartTime(object2.getLong("start_time"));
                            cloud.setEndTime(object2.getLong("end_time"));
                            listCloud.add(cloud);
                        }
                        getCanvasList(startTime, endTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, JsonObject data) {
                LogCat.e(TAG, "111111 cloud getTimeSlots onFail");
                hideLoadingDialog();
            }
        });
    }

    //时间轴组合
    private void getCanvasList(long mStartTime, long mEndTime) {
        int apSize = listAp.size();
        int cloudSize = listCloud.size();
        if (apSize == 0 && cloudSize > 0) {
            listAp = listCloud;
            timeCanvasList(listAp);//组合时间轴
            hideLoadingDialog();
            return;
        }
        ApCloudTimeBean bean;
        //AP时间
        for (int i = 0; i < apSize + 1; i++) {
            bean = new ApCloudTimeBean();
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
        if (listAp.size() > 1) {
            listAp = duplicateRemoval(listAp);//去重
            Collections.sort(listAp);//正序比较
        }
        timeCanvasList(listAp);//组合时间轴
        hideLoadingDialog();
//        for (int i = 0; i < listAp.size(); i++) {
//            LogCat.e(TAG, "3333=  " + listAp.get(i).getStartTime() + ", " + listAp.get(i).getEndTime() + ", " + listAp.get(i).isApPlay());
//        }
    }

    //去重
    private List<ApCloudTimeBean> duplicateRemoval(List<ApCloudTimeBean> list) {
        LinkedHashSet<ApCloudTimeBean> tmpSet = new LinkedHashSet<>(list.size());
        tmpSet.addAll(list);
        list.clear();
        list.addAll(tmpSet);
        return list;
    }
}
