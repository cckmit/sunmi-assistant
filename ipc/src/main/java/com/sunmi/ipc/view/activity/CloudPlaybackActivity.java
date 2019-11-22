package com.sunmi.ipc.view.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datelibrary.bean.DateType;
import com.datelibrary.view.DatePickDialog;
import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CloudPlaybackContract;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.presenter.CloudPlaybackPresenter;
import com.sunmi.ipc.router.SunmiServiceApi;
import com.sunmi.ipc.view.ZFTimeLine;
import com.xiaojinzi.component.impl.Router;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.view.TitleBarView;

/**
 * Description:
 * Created by bruce on 2019/11/15.
 */
@EActivity(resName = "activity_cloud_playback")
public class CloudPlaybackActivity extends BaseMvpActivity<CloudPlaybackPresenter>
        implements CloudPlaybackContract.View, IVideoPlayer.VideoPlayListener,
        ZFTimeLine.OnZFTimeLineListener, View.OnClickListener {

    private final static long SECONDS_IN_ONE_DAY = 24 * 60 * 60;//3天秒数
    private final static int tenMinutes = 10 * 60;//10分钟

    @ViewById(resName = "rl_screen")
    LinearLayout rlScreen;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "ivp_cloud")
    IVideoPlayer ivpCloud;
    @ViewById(resName = "rl_control_panel")
    RelativeLayout rlController;
    @ViewById(resName = "rl_top")
    RelativeLayout rlTopBar;
    @ViewById(resName = "rl_bottom_playback")
    RelativeLayout rlBottomBar;
    @ViewById(resName = "iv_record")
    ImageView ivRecord;//录制
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;//音量
    @ViewById(resName = "cm_timer")
    Chronometer cmTimer;//录制时间
    @ViewById(resName = "rl_record")
    RelativeLayout rlRecord;
    @ViewById(resName = "iv_screenshot")
    ImageView ivScreenshot;//截图
    @ViewById(resName = "iv_live")
    ImageView ivLive;//直播
    @ViewById(resName = "iv_play")
    ImageView ivPlay;//开始播放
    @ViewById(resName = "iv_full_screen")
    ImageView ivFullScreen;
    @ViewById(resName = "tv_time_scroll")
    TextView tvTimeScroll;
    @ViewById(resName = "rl_video")
    RelativeLayout rlVideo;
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "ll_no_service")
    LinearLayout llNoService;
    @ViewById(resName = "ll_portrait_controller_bar")
    LinearLayout llPortraitBar;

    @ViewById(resName = "time_line")
    ZFTimeLine timeLine;

    @Extra
    SunmiDevice device;
    @Extra
    int cloudStorageServiceStatus;

    CountDownTimer hideControllerPanelTimer;//屏幕控件自动隐藏计时器
    private CountDownTimer timeLineScrollTimer; //时间轴滑动延时
    private int screenW; //手机屏幕的宽
    private boolean isPaused;//回放是否暂停
    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isVideoLess1Minute;//视频片段是否小于一分钟

    //选择日历当前的时间的0点
    private long currentTime;
    //当前时间，已选日期的开始和结束时间  in seconds
    private long presentTime, startTimeCurrentDate, endTimeCurrentDate;
    //刻度尺移动定时器
    private ScheduledExecutorService executorService;
    //滑动停止的时间戳
    private long scrollTime;

    private Handler handler = new Handler();
    private VolumeHelper volumeHelper = null;

    private List<VideoTimeSlotBean> timeSlotsInDay = new ArrayList<>();

    private List<VideoTimeSlotBean> timeSlotsInMonth;

    @AfterViews
    void init() {
        mPresenter = new CloudPlaybackPresenter();
        mPresenter.attachView(this);
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        titleBar.setAppTitle(device.getName());
        titleBar.getLeftLayout().setOnClickListener(this);
        if (cloudStorageServiceStatus == CommonConstants.CLOUD_STORAGE_NOT_OPENED) {
            llNoService.setVisibility(View.VISIBLE);
        } else {
            rlBottomBar.setVisibility(View.VISIBLE);
        }
        initData();
        switchOrientation(Configuration.ORIENTATION_PORTRAIT);
        llNoService.setOnTouchListener((v, event) -> true);
        llPlayFail.setOnTouchListener((v, event) -> true);
        handler.postDelayed(this::initControllerPanel, 200);
    }

    @SuppressLint("ClickableViewAccessibility")
    void initControllerPanel() {
        rlVideo.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                if (!isControlPanelShow) {
                    startTimer();
                }
            }
            return false;
        });
        openMove();
        initVolume();
        rlController.setVisibility(View.GONE);
        timeLine.setListener(this);
    }

    void initData() {
        screenW = CommonHelper.getScreenWidth(context);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setDay(day > 9 ? day + "" : "0" + day);
        presentTime = System.currentTimeMillis() / 1000;
        startTimeCurrentDate = DateTimeUtils.getDayStart(new Date()).getTime() / 1000;
        endTimeCurrentDate = presentTime;
        initTimeSlotData();
    }

    private void setDay(String day) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        removeCallbacks();
        closeMove();//关闭时间抽的timer
        cancelTimer();//关闭屏幕控件自动hide计时器
    }

    @Override
    public void onBackPressed() {
        if (isPortrait()) {
            if (llNoService != null && llNoService.isShown()) {
                llNoService.setVisibility(View.GONE);
                return;
            }
            stopPlay();
            handler.postDelayed(this::finish, 200);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switchOrientation(newConfig.orientation);
    }

    /**
     * 按键控制音量，return true时不显示系统音量 return false时显示系统音量
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setVolumeViewImage(volumeHelper.get100CurrentVolume());
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_back_layout) {
            onBackPressed();
        }
    }

    @Click(resName = "btn_open_service")
    void openServiceClick() {
        ArrayList<String> snList = new ArrayList<>();
        snList.add(device.getDeviceid());
        Router.withApi(SunmiServiceApi.class).goToWebViewCloud(CommonConfig.CLOUD_STORAGE_URL, snList);
    }

    @Click(resName = "rl_top")
    void backClick() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Click(resName = "iv_full_screen")
    void fullScreenClick() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
    @Click(resName = "iv_mute")
    void volumeClick() {
        if (volumeHelper.isMute()) {
            setVolumeViewImage(1);
            volumeHelper.unMute();
        } else {
            volumeHelper.mute();
            setVolumeViewImage(0);
        }
    }

    //开始，暂停
    @Click(resName = "iv_play")
    void pausePlayClick() {
        if (isFastClick(1000)) {
            return;
        }
        ivPlay.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isPaused) {
            ivpCloud.pause();
        } else {
            ivpCloud.play();
        }
    }

    //点击屏幕
    @Click(resName = "rl_video")
    void screenClick() {
        if (llPlayFail != null && llPlayFail.isShown()) {
            return;
        }
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
        isControlPanelShow = false;
        setPlayFailVisibility(View.GONE);
        showVideoLoading();
    }

    @Override
    public void onStartPlay() {
        hideVideoLoading();
    }

    @Override
    public void onPlayComplete() {
        selectedTimeIsHaveVideo(timeLine.getCurrentInterval());
    }

    @Override
    public void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots) {
        timeSlotsInDay.clear();
        timeSlotsInDay.addAll(slots);
        if (timeSlotsInDay.size() > 0) {
            refreshScaleTimePanel();
            selectedTimeIsHaveVideo(currentTime);
        } else {
            shortTip("no data");
        }
        hideVideoLoading();
    }

    @Override
    public void getCloudTimeSlotFail() {
    }

    @UiThread
    @Override
    public void getCloudVideosSuccess(List<VideoListResp.VideoBean> videoBeans) {
        List<String> urlList = new ArrayList<>();
        for (VideoListResp.VideoBean bean : videoBeans) {
            urlList.add(bean.getUrl());
        }
        cloudPlay(urlList);
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.ipcNameChanged, CommonNotifications.cloudStorageOpened};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (id == IpcConstants.ipcNameChanged) {
            if (args != null) {
                SunmiDevice sd = (SunmiDevice) args[0];
                if (TextUtils.equals(sd.getDeviceid(), device.getDeviceid())) {
                    device.setName(sd.getName());
                    titleBar.setAppTitle(device.getName());
                }
            }
        } else if (id == CommonNotifications.cloudStorageOpened) {
            cloudStorageServiceOpened();
        }
    }

    @UiThread
    void cloudStorageServiceOpened() {
        cloudStorageServiceStatus = CommonConstants.CLOUD_STORAGE_ALREADY_OPENED;
        llNoService.setVisibility(View.GONE);
    }

    @UiThread
    public void setPlayFailVisibility(int visibility) {
        llPlayFail.setVisibility(visibility);
    }

    @UiThread
    public void showVideoLoading() {
        if (isPortrait()) {
            llNoService.setVisibility(View.VISIBLE);
        } else {
            showLoadingDialog();
        }
    }

    @UiThread
    public void hideVideoLoading() {
        if (isPortrait()) {
            llNoService.setVisibility(View.GONE);
        } else {
            hideLoadingDialog();
        }
    }

    private void setTextViewClickable(TextView textView, boolean clickable) {
        textView.setClickable(clickable);
        textView.setTextColor(clickable ? ContextCompat.getColor(context, R.color.c_white)
                : ContextCompat.getColor(context, R.color.white_40a));
    }

    private boolean isPortrait() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 视频全屏切换
     */
    public void switchOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
            setPortraitViewVisible(View.GONE);
            setLandscapeViewVisible(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitViewVisible(View.VISIBLE);
            setLandscapeViewVisible(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
        setVideoParams(orientation);
    }

    private void setLandscapeViewVisible(int visibility) {
        setPanelVisible(visibility);
    }

    private void setPortraitViewVisible(int visibility) {
        ivFullScreen.setVisibility(visibility);
        titleBar.setVisibility(visibility);
        llPortraitBar.setVisibility(visibility);
    }

    /**
     * 设置SurfaceView的参数
     */
    public void setVideoParams(int orientation) {
        int videoW = screenW, videoH = screenW;
        ViewGroup.LayoutParams rlLP = rlVideo.getLayoutParams();
        ViewGroup.LayoutParams bottomBarLp = rlBottomBar.getLayoutParams();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomBarLp.height = CommonHelper.dp2px(context, 64);
            int screenH = CommonHelper.getScreenWidth(context);//横屏
            float aspectRatio = screenW / screenH;//宽高比
            videoW = screenH;
            if (isSS1()) {
                videoW = screenW;
            } else {
                if (aspectRatio > 16 / 9) {
                    videoW = videoH * 16 / 9;
                } else {
                    videoH = videoW * 9 / 16;
                }
            }
            rlLP.height = screenW;
            rlLP.width = screenH;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomBarLp.height = CommonHelper.dp2px(context, 48);
            if (isSS1()) {
                videoH = screenW;
            } else {
                videoH = screenW * 9 / 16;
            }
            rlLP.width = screenW;
            rlLP.height = videoH;
        }

        rlVideo.setLayoutParams(rlLP);
        rlBottomBar.setLayoutParams(bottomBarLp);

        ViewGroup.LayoutParams lpCloud = ivpCloud.getLayoutParams();
        lpCloud.width = videoW;
        lpCloud.height = videoH;
        ivpCloud.setLayoutParams(lpCloud);
    }

    private boolean isSS1() {
        return DeviceTypeUtils.getInstance().isSS1(device.getModel());
    }

    private void stopPlay() {
        cloudPlayDestroy();//关闭云端视频
    }

    @UiThread
    void hideControllerPanel() {
        setPanelVisible(View.GONE);
    }

    private void setPanelVisible(int visible) {
        if (rlTopBar != null && rlBottomBar != null) {
            rlController.setVisibility(View.VISIBLE);
            rlTopBar.setVisibility(isPortrait() ? View.GONE : visible);
            rlBottomBar.setVisibility(visible);
        }
    }

    private void showDatePicker() {
        DatePickDialog datePickDialog = new DatePickDialog(context);
        datePickDialog.setType(DateType.TYPE_YMD);
        //设置点击确定按钮回调
        datePickDialog.setOnSureListener(this::onSureButton);

        datePickDialog.setStartDate(scrollTime > 0 ? new Date(scrollTime)
                : new Date(System.currentTimeMillis()));
        datePickDialog.show();
    }

    //开始计时录制
    private void startRecord() {
        cmTimer.setOnChronometerTickListener(cArg -> {
            long time = System.currentTimeMillis() - cArg.getBase();
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            cmTimer.setText(sdf.format(d));
        });
        cmTimer.setBase(System.currentTimeMillis());
        cmTimer.start();
    }

    /**
     * 播放云端回放
     */
    private void cloudPlay(List<String> urlList) {
        ivpCloud.setUrlQueue(urlList);
        try {
            ivpCloud.startPlay();
        } catch (Exception e) {
            shortTip(R.string.tip_play_fail);
            e.printStackTrace();
        }
        hideVideoLoading();
    }

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
        volumeHelper = new VolumeHelper(context);
        setVolumeViewImage(volumeHelper.get100CurrentVolume());
    }

    private void setVolumeViewImage(int currentVolume100) {
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
        scrollTime = timeLine.getCurrentInterval() * 1000;//获取实时滚动的时间
        setDay(timeLine.currentTimeStr().substring(6, 8));
        executorService.scheduleAtFixedRate(this::moveTo, 60, 60, TimeUnit.SECONDS);
    }

    @UiThread
    void moveTo() {
        timeLine.autoMove();
        //自动滑动时下一个视频ap还是cloud播放
        if (isVideoLess1Minute) {
            isVideoLess1Minute = false;
            switch2Playback(timeLine.getCurrentInterval());
        }
    }

    //结束移动
    public void closeMove() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    @UiThread
    void refreshScaleTimePanel() {
        timeLine.setLeftBound(startTimeCurrentDate);
        timeLine.setVideoData(timeSlotsInDay);
        timeLine.refresh();
    }

    //选择日历日期回调
    @SuppressLint("DefaultLocale")
    public void onSureButton(Date date) {
        resetCountdown();//重置隐藏控件计时
        long presentTime = System.currentTimeMillis() / 1000;//当前时间戳秒
        scrollTime = date.getTime();//选择日期的时间戳毫秒
        long time = scrollTime / 1000; //设置日期的秒数
        if (time > presentTime) {//未来时间或当前--滑动当前直播
            scrollTime = System.currentTimeMillis();
            setDay(String.format("%td", new Date()));
        } else {//回放时间
            ivPlay.setBackgroundResource(R.mipmap.pause_normal);
            ivLive.setVisibility(View.VISIBLE);
            setDay(String.format("%td", date));//显示日历天数
            currentTime = startTimeCurrentDate = DateTimeUtils.getDayStart(date).getTime() / 1000;
            endTimeCurrentDate = startTimeCurrentDate + SECONDS_IN_ONE_DAY;
            initTimeSlotData();
        }
    }

    //获取视频跳转播放的currentItemPosition
    private void videoSkipScrollPosition(long currentTimeMinutes) {
        timeLine.moveToTime(currentTimeMinutes);
    }

    //滑动回放定位的中间 position
    @UiThread
    void scrollCurrentPlayBackTime(long currentTimeMinutes) {
        ivPlay.setBackgroundResource(R.mipmap.pause_normal);
        isPaused = false;
        timeLine.moveToTime(currentTimeMinutes);
        openMove();
    }

    //拖动或选择的时间是否有video
    @Background
    void selectedTimeIsHaveVideo(long currTime) {
        int apSize = timeSlotsInDay.size();
        long mStartTime = startTimeCurrentDate, mEndTime = presentTime;
        for (int i = 0; i < apSize + 1; i++) {
            long startOpposite = 0, endOpposite = 0, start = 0, end = 0;
            //不包含ap时间轴内的时间
            if (i == 0) {
                startOpposite = mStartTime;
                endOpposite = timeSlotsInDay.get(i).getStartTime();
            } else if (i < apSize) {//包含ap时间内
                startOpposite = timeSlotsInDay.get(i - 1).getEndTime();
                endOpposite = timeSlotsInDay.get(i).getStartTime();
                start = timeSlotsInDay.get(i).getStartTime();
                end = timeSlotsInDay.get(i).getEndTime();
            } else if (i == apSize) {
                startOpposite = timeSlotsInDay.get(i - 1).getEndTime();
                endOpposite = mEndTime;
            }

            if (currTime >= startOpposite && currTime < endOpposite) {//空白区域
                if (i == apSize) {//最后一个无视频区域跳转直播
                    shortTip("没有回放视频");
                    return;
                }
                //当前的视频片段是否小于一分钟
                isVideoLess1Minute = timeSlotsInDay.get(i).getEndTime() - timeSlotsInDay.get(i).getStartTime() <= 60;
                getVideoList(endOpposite, endOpposite + tenMinutes);
                scrollCurrentPlayBackTime(endOpposite);//回放到拖动的时间点
                break;
            } else if (currTime >= start && currTime < end) {//视频区域
                //当前的视频片段是否小于一分钟
                isVideoLess1Minute = timeSlotsInDay.get(i).getEndTime() - currTime <= 60;
                getVideoList(currTime, currTime + tenMinutes);
                scrollCurrentPlayBackTime(currTime);//回放到拖动的时间点
                break;
            }
        }
    }

    /**
     * 切到云端回放
     */
    void getVideoList(long start, long end) {
        mPresenter.getCloudVideoList(device.getId(), start, end);
    }

    private void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private void switch2Playback(long currTime) {
        int availableVideoSize = timeSlotsInDay.size();
        for (int i = 0; i < availableVideoSize; i++) {
            VideoTimeSlotBean bean = timeSlotsInDay.get(i);
            long start = bean.getStartTime();
            long end = bean.getEndTime();
            if (end - currTime < 60 && currTime >= start && currTime < end) {
                if (i != availableVideoSize - 1) {
                    final int delayMillis = (int) end - currTime < 0 ? 1 : (int) (end - currTime);
                    final int finalI = i;
                    handler.postDelayed(() -> {
                        getVideoList(timeSlotsInDay.get(finalI + 1).getStartTime(),
                                timeSlotsInDay.get(finalI + 1).getStartTime() + tenMinutes);
                        videoSkipScrollPosition(timeSlotsInDay.get(finalI + 1).getStartTime()); //偏移跳转
                    }, delayMillis * 1000);
                    break;
                }
            }
        }
    }

    //初始化时间轴
    private void initTimeSlotData() {
        showVideoLoading();
        timeSlotsInDay.clear();
        timeLine.clearData();
        mPresenter.getTimeSlots(device.getId(), startTimeCurrentDate, endTimeCurrentDate);
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

    /**
     * 延时执行滑动处理，防止无视频区域直接跳过
     *
     * @param timeStamp
     */
    private void startDelayPlay(long timeStamp) {
        cancelDelayPlay();
        timeLineScrollTimer = new CountDownTimer(800, 200) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                selectedTimeIsHaveVideo(timeStamp);
            }
        };
        timeLineScrollTimer.start();
    }

    private void cancelDelayPlay() {
        if (timeLineScrollTimer != null) {
            timeLineScrollTimer.cancel();
            timeLineScrollTimer = null;
        }
    }

    @Override
    public void didMoveToTime(long timeStamp) {
        showVideoLoading();
        hideTimeScroll();
        if (timeStamp > System.currentTimeMillis() / 1000) {//超过当前时间
//            shortTip(getString(R.string.ipc_time_over_current_time));
        } else {
            startDelayPlay(timeStamp);
//        selectedTimeIsHaveVideo(timeStamp);
        }
    }

    @Override
    public void moveTo(String data, boolean isLeftScroll, long timeStamp) {
        cancelDelayPlay();
        showTimeScroll(data.substring(11), isLeftScroll);//toast显示时间
    }

    private Drawable drawableLeft, drawableRight;

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

    @UiThread
    void hideTimeScroll() {
        handler.postDelayed(() -> tvTimeScroll.setVisibility(View.GONE), 500);
    }

}
