package com.sunmi.ipc.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.Config;
import com.sunmi.ipc.calendar.VerticalCalendar;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CloudPlaybackContract;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.presenter.CloudPlaybackPresenter;
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
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.BottomDialog;

/**
 * Description:
 * Created by bruce on 2019/11/15.
 */
@EActivity(resName = "activity_cloud_playback")
public class CloudPlaybackActivity extends BaseMvpActivity<CloudPlaybackPresenter>
        implements CloudPlaybackContract.View, IVideoPlayer.VideoPlayListener,
        ZFTimeLine.OnZFTimeLineListener, View.OnClickListener, VolumeHelper.VolumeChangeListener {

    private final static long SECONDS_IN_ONE_DAY = 24 * 60 * 60;
    private final static int tenMinutes = 10 * 60;//10分钟

    @ViewById(resName = "rl_screen")
    LinearLayout rlScreen;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "ivp_cloud")
    IVideoPlayer ivpCloud;
    @ViewById(resName = "rl_top")
    RelativeLayout rlTopBar;
    @ViewById(resName = "rl_bottom_playback")
    RelativeLayout rlBottomBar;
    @ViewById(resName = "iv_mute")
    ImageView ivMute;//音量
    @ViewById(resName = "cm_timer")
    Chronometer cmTimer;//录制时间
    @ViewById(resName = "rl_record")
    RelativeLayout rlRecord;
    @ViewById(resName = "iv_pause")
    ImageView ivPlay;//开始播放
    @ViewById(resName = "iv_full_screen")
    ImageView ivFullScreen;
    @ViewById(resName = "tv_time_scroll")
    TextView tvTimeScroll;
    @ViewById(resName = "rl_video")
    RelativeLayout rlVideo;
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "tv_play_fail")
    TextView tvPlayFail;
    @ViewById(resName = "ll_no_service")
    LinearLayout llNoService;
    @ViewById(resName = "btn_open_service")
    Button btnOpenService;
    @ViewById(resName = "ll_portrait_controller_bar")
    LinearLayout llPortraitBar;
    @ViewById(resName = "iv_pre_day")
    ImageView ivPreDay;
    @ViewById(resName = "tv_calendar")
    TextView tvCalendar;
    @ViewById(resName = "iv_next_day")
    ImageView ivNextDay;
    @ViewById(resName = "time_line")
    ZFTimeLine timeLine;
    @ViewById(resName = "rl_loading")
    RelativeLayout rlLoading;

    @Extra
    SunmiDevice device;
    @Extra
    int deviceId;//设备id
    @Extra
    String deviceName;//设备名称
    @Extra
    String deviceSn;//设备sn
    @Extra
    String deviceType;//ipc类型
    @Extra
    int cloudStorageServiceStatus;
    @Extra
    long currentTime;//进页面开始播放的时间 in seconds

    long startTimeCurrentDate, endTimeCurrentDate;//已选日期的开始和结束时间  in seconds
    private int screenW; //手机屏幕的宽
    private boolean isPaused;//回放是否暂停
    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isVideoLess1Minute;//视频片段是否小于一分钟

    private long lastVideoEndTime;    //已经在播放的视频结束时间

    private ScheduledExecutorService executorService;//刻度尺移动定时器

    private CountDownTimer timeLineScrollTimer; //时间轴滑动延时
    private Handler handler = new Handler();
    private VolumeHelper volumeHelper = null;

    private List<VideoTimeSlotBean> timeSlotsInDay = new ArrayList<>();

    private List<VideoTimeSlotBean> timeSlotsInMonth;

    private Dialog calendarDialog;
    private VerticalCalendar calendarView;
    private Calendar calendarSelected;

    @AfterViews
    void init() {
        mPresenter = new CloudPlaybackPresenter();
        mPresenter.attachView(this);
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        titleBar.setAppTitle(device.getName());
        titleBar.getLeftLayout().setOnClickListener(this);
        if (isServiceUnopened()) {
            setCalendarEnable(false);
            timeLine.setVisibility(View.GONE);
            llNoService.setVisibility(View.VISIBLE);
            btnOpenService.setVisibility(View.VISIBLE);
        } else {
            showDarkLoading();
            ivMute.setEnabled(false);
            ivFullScreen.setEnabled(false);
            rlBottomBar.setVisibility(View.VISIBLE);
        }
        ivNextDay.setEnabled(false);
        if (currentTime > 0) {
            setCalendarEnable(false);
        }
        ivpCloud.setVideoPlayListener(this);
        initData();
        initVolume();
        switchOrientation(Configuration.ORIENTATION_PORTRAIT);
        llNoService.setOnTouchListener((v, event) -> true);
        rlLoading.setOnTouchListener((v, event) -> true);
        llPlayFail.setOnTouchListener((v, event) -> true);
        initTimeLine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPaused) {
            pausePlayClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        volumeHelper.unregisterVolumeReceiver();
        stopPlay();
        removeCallbacks();
        closeMove();//关闭时间抽的timer
    }

    @Override
    public void onBackPressed() {
        if (isPortrait()) {
            if (rlLoading != null && rlLoading.isShown()) {
                rlLoading.setVisibility(View.GONE);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Click(resName = "iv_pre_day")
    void preDayClick() {
        if (isFastClick(1000) || isServiceUnopened()) {
            return;
        }
        switchDay(startTimeCurrentDate - SECONDS_IN_ONE_DAY);
    }

    @Click(resName = "iv_next_day")
    void nextDayClick() {
        if (isFastClick(1000) || isServiceUnopened()) {
            return;
        }
        switchDay(startTimeCurrentDate + SECONDS_IN_ONE_DAY);
    }

    @Click(resName = "tv_calendar")
    void chooseCalendarClick() {
        if (isFastClick(1000) || isServiceUnopened()) {
            return;
        }
        if (timeSlotsInMonth == null) {
            LogCat.e(TAG, "Time slots in month is Empty.");
            return;
        }
        if (calendarDialog == null || calendarView == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -24);
            Config config = new Config.Builder()
                    .setMinDate(c)
                    .setPoint(getTimeSlotOfCalendar(timeSlotsInMonth))
                    .build();
            int height = (int) (Utils.getScreenHeight(context) * 0.85);
            calendarView = new VerticalCalendar(this, config);
            calendarView.setOnCalendarSelectListener(calendar -> calendarSelected = calendar);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            calendarDialog = new BottomDialog.Builder(context)
                    .setTitle(R.string.str_title_calendar)
                    .setContent(calendarView, lp)
                    .setCancelButton(R.string.sm_cancel)
                    .setOkButton(R.string.str_confirm, (dialog, which) -> {
                        if (calendarSelected != null) {
                            switchDay(calendarSelected.getTimeInMillis() / 1000);
                        }
                    }).create();
        }
        calendarSelected = null;
        calendarView.setSelected(startTimeCurrentDate * 1000);
        calendarDialog.show();
    }

    @Click(resName = "btn_open_service")
    void openServiceClick() {
        Router.withApi(SunmiServiceApi.class)
                .goToWebViewCloud(context, CommonConstants.H5_CLOUD_STORAGE,
                        WebViewParamsUtils.getCloudStorageParams(device.getDeviceid(), ""));
    }

    @Click(resName = "rl_top")
    void backClick() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Click(resName = "iv_full_screen")
    void fullScreenClick() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

//    //视频录制
//    @Click(resName = "iv_record")
//    void recordClick() {
//        if (isStartRecord) {
//            ivRecord.setBackgroundResource(R.mipmap.ic_recording_normal);
//            rlRecord.setVisibility(View.GONE);
//            isStartRecord = false;
//            cmTimer.stop();//关闭录制
//        } else {
//            ivRecord.setBackgroundResource(R.mipmap.ic_recording);
//            rlRecord.setVisibility(View.VISIBLE);
//            isStartRecord = true;
//            startRecord();//开始录制
//        }
//    }

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
    @Click(resName = "iv_pause")
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
            setPanelVisible(View.GONE);
            isControlPanelShow = false;
        } else {
            setPanelVisible(View.VISIBLE);
            isControlPanelShow = true;
        }
    }

    @Override
    public void onStartPlay() {
        hideLoading();
    }

    @Override
    public void onPlayComplete() {
        if (isPlayOver(timeLine.getCurrentInterval())) {
            playOver();
        } else {
            selectedTimeHasVideo(timeLine.getCurrentInterval());
        }
    }

    @Override
    public void onPlayFail() {
        showPlayFail(getStringById(R.string.network_error));
    }

    private boolean isPlayOver(long time) {
        return lastVideoEndTime == endTimeCurrentDate
                || time == endTimeCurrentDate //time slots的最后一段视频的结束时间可能比当天的0点大
                || (timeSlotsInDay != null && timeSlotsInDay.size() > 0
                && lastVideoEndTime >= timeSlotsInDay.get(timeSlotsInDay.size() - 1).getEndTime());
    }

    @Override
    public void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots) {
        if (startTime == startTimeCurrentDate - 30 * SECONDS_IN_ONE_DAY) {
            timeSlotsInMonth = slots;
            return;
        }
        timeSlotsInDay.clear();
        timeSlotsInDay.addAll(slots);
        if (timeSlotsInDay.size() > 0) {
            timeLine.setVisibility(View.VISIBLE);
            refreshScaleTimePanel();
            openMove();
            selectedTimeHasVideo(currentTime > 0 ? currentTime : startTimeCurrentDate);
        } else {
            showNoVideoTip();
        }
        hideVideoLoading();
    }

    @Override
    public void showNoVideoTip() {
        timeLine.setVisibility(View.GONE);
        showPlayFail(getStringById(R.string.tip_no_video_current_day));
    }

    @Override
    public void getCloudTimeSlotFail() {
        showPlayFail(getStringById(R.string.network_error));
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
    public void getCloudVideosFail() {
        showPlayFail(getStringById(R.string.network_error));
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcNameChanged, CommonNotifications.cloudStorageChange};
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
        } else if (id == CommonNotifications.cloudStorageChange) {
            cloudStorageServiceOpened();
            initTimeSlotData(false);
        }
    }

    private void setCalendarEnable(boolean enable) {
        ivPreDay.setEnabled(enable);
        tvCalendar.setEnabled(enable);
    }

    private boolean isServiceUnopened() {
        return cloudStorageServiceStatus == CommonConstants.SERVICE_NOT_OPENED;
    }

    @SuppressLint("ClickableViewAccessibility")
    void initTimeLine() {
        timeLine.setInterval(300, 6);// 设置时间轴每个小刻度5分钟，每个大刻度包含6个小刻度
        timeLine.setListener(this);
    }

    void initData() {
        screenW = CommonHelper.getScreenWidth(context);
        if (currentTime > 0) {//指定某天开始
            startTimeCurrentDate = DateTimeUtils.getDayStart(new Date(currentTime * 1000)).getTime() / 1000;
            endTimeCurrentDate = startTimeCurrentDate + SECONDS_IN_ONE_DAY;
        } else {
            startTimeCurrentDate = DateTimeUtils.getDayStart(new Date()).getTime() / 1000;
            endTimeCurrentDate = System.currentTimeMillis() / 1000;
        }
        refreshDay();
        if (cloudStorageServiceStatus != CommonConstants.SERVICE_NOT_OPENED) {
            initTimeSlotData(true);
            if (currentTime <= 0) {
                mPresenter.getTimeSlots(device.getId(),
                        startTimeCurrentDate - 30 * SECONDS_IN_ONE_DAY, endTimeCurrentDate);
            }
        }
    }

    private void refreshDay() {
        tvCalendar.setText(DateTimeUtils.formatDateTime(new Date(startTimeCurrentDate * 1000)));
    }

    private void updateCalendarBtnEnable() {
        ivNextDay.setEnabled(endTimeCurrentDate <= System.currentTimeMillis() / 1000);
    }

    private List<Calendar> getTimeSlotOfCalendar(List<VideoTimeSlotBean> slots) {
        List<Calendar> result = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (VideoTimeSlotBean slot : slots) {
            long start = slot.getStartTime() * 1000;
            long end = slot.getEndTime() * 1000;
            c.clear();
            c.setTimeInMillis(start);
            while (c.getTimeInMillis() < end) {
                result.add((Calendar) c.clone());
                c.add(Calendar.DATE, 1);
            }
            c.setTimeInMillis(end);
            result.add((Calendar) c.clone());
        }
        return result;
    }

    private void switchDay(long currentDay) {
        startTimeCurrentDate = currentDay;
        hidePlayFail();
        endTimeCurrentDate = startTimeCurrentDate + SECONDS_IN_ONE_DAY;
        refreshDay();
        updateCalendarBtnEnable();
        initTimeSlotData(false);
    }

    @UiThread
    void cloudStorageServiceOpened() {
        cloudStorageServiceStatus = CommonConstants.SERVICE_ALREADY_OPENED;
        llNoService.setVisibility(View.GONE);
        setCalendarEnable(true);
    }

    @UiThread
    public void showPlayFail(String tip) {
        hideLoading();
        stopPlay();
        tvPlayFail.setText(tip);
        llPlayFail.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hidePlayFail() {
        llPlayFail.setVisibility(View.GONE);
    }

    @UiThread
    public void showVideoLoading() {
        rlLoading.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hideVideoLoading() {
        rlLoading.setVisibility(View.GONE);
    }

    @UiThread
    public void hideLoading() {
        ivMute.setEnabled(true);
        ivFullScreen.setEnabled(true);
        hideLoadingDialog();
        hideVideoLoading();
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
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitViewVisible(View.VISIBLE);
            setPanelVisible(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
        setPanelVisible(View.VISIBLE);
        setVideoParams(orientation);
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
            rlBottomBar.setBackgroundResource(R.mipmap.bg_video_controller_bottom_h);
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
            rlBottomBar.setBackgroundResource(R.mipmap.bg_video_controller_bottom_v);
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

    /*
     * 停止播放
     */
    private void stopPlay() {
        try {
            if (ivpCloud != null) {
                ivpCloud.setVisibility(View.GONE);
                ivpCloud.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void setPanelVisible(int visible) {
        if (rlTopBar != null && rlBottomBar != null) {
            rlTopBar.setVisibility(isPortrait() ? View.GONE : visible);
            rlBottomBar.setVisibility(visible);
        }
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
            ivpCloud.setVisibility(View.VISIBLE);
            ivpCloud.startPlay();
        } catch (Exception e) {
            shortTip(R.string.tip_play_fail);
            e.printStackTrace();
        }
        hideVideoLoading();
    }

    /**
     * 调节音量
     */
    private void initVolume() {
        volumeHelper = new VolumeHelper(context);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
        setVolumeViewImage(volumeHelper.get100CurrentVolume());
    }

    private void setVolumeViewImage(int currentVolume100) {
        ivMute.setImageResource(currentVolume100 == 0 ? R.drawable.ic_mute : R.drawable.ic_unmute);
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
        executorService.scheduleAtFixedRate(this::moveTo, 60, 60, TimeUnit.SECONDS);
    }

    @UiThread
    void moveTo() {
        if (!isVideoLess1Minute) {
            timeLine.autoMove();
        } else {
            isVideoLess1Minute = false;
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
        timeLine.setBound(startTimeCurrentDate, startTimeCurrentDate + SECONDS_IN_ONE_DAY,
                startTimeCurrentDate, endTimeCurrentDate);
        timeLine.setVideoData(timeSlotsInDay);
        timeLine.refresh();
    }

    //获取视频跳转播放的currentItemPosition
    @UiThread
    void timeLineScrollTo(long currentTimeMinutes) {
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
    void selectedTimeHasVideo(long currTime) {
        int apSize = timeSlotsInDay.size();
        if (apSize <= 0) {
            hideVideoLoading();
            return;
        }
        if (currTime >= endTimeCurrentDate) {
            playOver();
            return;
        }
        long slotStartTime, slotEndTime;
        for (int i = 0; i < apSize; i++) {
            slotStartTime = timeSlotsInDay.get(i).getStartTime();
            slotEndTime = timeSlotsInDay.get(i).getEndTime();
            if (currTime <= slotStartTime) {
                getVideoList(slotStartTime, slotEndTime);
                return;
            } else if (currTime < slotEndTime) {
                getVideoList(currTime, slotEndTime);
                return;
            }
        }
        playOver();
    }

    /**
     * 云端回放
     */
    void getVideoList(long start, long end) {
        lastVideoEndTime = end;
        isVideoLess1Minute = end - start < 60;
        mPresenter.getCloudVideoList(device.getId(), start,
                end <= start + tenMinutes ? end : start + tenMinutes);
        scrollCurrentPlayBackTime(isVideoLess1Minute ? end : start);
    }

    private void playOver() {
        if (llPlayFail != null && llPlayFail.isShown()) {
            return;
        }
        closeMove();
        showPlayFail(getString(R.string.tip_video_played_over));
    }

    private void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    //初始化时间轴
    private void initTimeSlotData(boolean isFirstInit) {
        if (isFirstInit) {
            setCalendarEnable(true);
        } else {
            showVideoLoading();
        }
        timeSlotsInDay.clear();
        timeLine.clearData();
        mPresenter.getTimeSlots(device.getId(), startTimeCurrentDate, endTimeCurrentDate);
    }

    /**
     * 延时执行滑动处理，防止无视频区域直接跳过
     */
    private void startDelayPlay(long timeStamp) {
        cancelDelayPlay();
        timeLineScrollTimer = new CountDownTimer(600, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                showVideoLoading();
                openMove();
                selectedTimeHasVideo(timeStamp);
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
        hideTimeScroll();
        lastVideoEndTime = 0;
        if (isPlayOver(timeStamp)) {
            playOver();
        } else if (!NetworkUtils.isNetworkAvailable(context)) {
            showPlayFail(getStringById(R.string.network_error));
        } else {
            hidePlayFail();
            startDelayPlay(timeStamp);
        }
    }

    @Override
    public void moveTo(String data, boolean isLeftScroll, long timeStamp) {
        cancelDelayPlay();
        showTimeScroll(data.substring(11), isLeftScroll);//toast显示时间
    }

    @UiThread
    void showTimeScroll(final String time, final boolean isLeft) {
        if (TextUtils.isEmpty(time)) {
            return;
        }
        tvTimeScroll.setVisibility(View.VISIBLE);
        tvTimeScroll.setText(time);
        tvTimeScroll.setEnabled(isLeft);
    }

    @UiThread
    void hideTimeScroll() {
        handler.postDelayed(() -> tvTimeScroll.setVisibility(View.GONE), 500);
    }

    @Override
    public void onVolumeChanged(int volume) {
        setVolumeViewImage(volume);
    }

}
