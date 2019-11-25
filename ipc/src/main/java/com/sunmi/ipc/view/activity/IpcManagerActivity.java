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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.datelibrary.bean.DateType;
import com.datelibrary.view.DatePickDialog;
import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.model.VideoTimeSlotBean;
import com.sunmi.ipc.presenter.IpcManagerPresenter;
import com.sunmi.ipc.router.SunmiServiceApi;
import com.sunmi.ipc.setting.IpcSettingActivity_;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;
import com.sunmi.ipc.utils.IpcUtils;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.VerticalSeekBar;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
@EActivity(resName = "activity_ipc_manager")
public class IpcManagerActivity extends BaseMvpActivity<IpcManagerPresenter>
        implements IpcManagerContract.View, SurfaceHolder.Callback, IOTCClient.Callback,
//        IVideoPlayer.VideoPlayListener,
        ZFTimeLine.OnZFTimeLineListener, View.OnClickListener {

    private final static int PLAY_TYPE_LIVE = 0;          // 直播
    private final static int PLAY_TYPE_PLAYBACK_DEV = 1;  // 设备回放
    //    private final static int PLAY_TYPE_PLAYBACK_CLOUD = 2;// 云回放
    private final static int PLAY_FAIL_OFFLINE = 1;
    private final static int PLAY_FAIL_NET_ERROR = 2;

    private final static long threeDaysSeconds = 3 * 24 * 60 * 60;//3天秒数
//    private final static int tenMinutes = 10 * 60;//10分钟

    @ViewById(resName = "rl_screen")
    LinearLayout rlScreen;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    //    @ViewById(resName = "ivp_cloud")
//    IVideoPlayer ivpCloud;
//    @ViewById(resName = "rl_control_panel")
//    RelativeLayout rlController;
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
    TextView tvFHDQuality;//高清画质
    @ViewById(resName = "tv_hd_quality")
    TextView tvHDQuality;//标清画质
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
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "tv_play_fail")
    TextView tvPlayFail;
    @ViewById(resName = "tv_retry")
    TextView tvRetry;
    @ViewById(resName = "scale_panel")
    ZFTimeLine scalePanel;
    @ViewById(resName = "tv_time_scroll")
    TextView tvTimeScroll;
    @ViewById(resName = "rl_video")
    RelativeLayout rlVideo;

    @ViewById(resName = "ll_loading")
    LinearLayout llLoading;
    @ViewById(resName = "rl_portrait_video_controller")
    RelativeLayout rlVideoController;
    @ViewById(resName = "iv_play_portrait")
    ImageView ivPlayP;//暂停
    @ViewById(resName = "iv_volume_portrait")
    ImageView ivVolumeP;//音量
    @ViewById(resName = "tv_quality_portrait")
    TextView tvQualityP;//画质
    @ViewById(resName = "iv_full_screen_portrait")
    ImageView ivFullScreen;
    @ViewById(resName = "ll_portrait_controller_bar")
    LinearLayout llPortraitBar;
    @ViewById(resName = "iv_cloud_playback_portrait")
    ImageView ivCloudPlayback;
    @ViewById(resName = "ll_calender_portrait")
    LinearLayout llCalender;
    @ViewById(resName = "tv_calender_portrait")
    TextView tvCalenderP;//日历
    @ViewById(resName = "rv_manager")
    SmRecyclerView rvManager;

    @Extra
    SunmiDevice device;

    private int screenW; //手机屏幕的宽
    private int playType;
    private boolean isPaused;//回放是否暂停
    private int qualityType = 0;//0-超清，1-高清
    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isVideoLess1Minute;//视频片段是否小于一分钟
    private boolean isFirstScroll = true;//是否第一次滑动
    //日历
    private Calendar calendar;
    //当前时间 ，三天前秒数
    private long currentDateSeconds, threeDaysBeforeSeconds;
    //刻度尺移动定时器
    private ScheduledExecutorService executorService;
    //滑动停止的时间戳
    private long scrollTime;
    //选择日历当前的时间的0点
    private long selectedDate;
    //是否为选择的日期
    private boolean isSelectedDate;
    private Handler handler = new Handler();
    private IOTCClient iotcClient;
    private H264Decoder videoDecoder = null;
    private AACDecoder audioDecoder = null;
    private VolumeHelper volumeHelper = null;
    private Drawable drawableLeft, drawableRight;

    private List<VideoTimeSlotBean> listAp = new ArrayList<>();
    //    private List<VideoTimeSlotBean> listCloud = new ArrayList<>();
    private CountDownTimer timeLineScrollTimer;

    //竖屏切换高清
    private BottomPopMenu qualityPop;

    private CommonListAdapter adapter;
    private int cloudStorageServiceStatus;
    private List<IpcManageBean> list = new ArrayList<>();

    @AfterViews
    void init() {
        mPresenter = new IpcManagerPresenter();
        mPresenter.attachView(this);
        if (isSS1()) {
            mPresenter.getStorageList(device.getDeviceid());
            ivCloudPlayback.setVisibility(View.VISIBLE);
        }
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        titleBar.setAppTitle(device.getName());
        titleBar.getLeftLayout().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
        rlBottomBar.setVisibility(View.VISIBLE);
        initData();
        llLoading.setOnTouchListener((v, event) -> true);
        llPlayFail.setOnTouchListener((v, event) -> true);
        if (isDeviceOffline()) {
            showPlayFail(PLAY_FAIL_OFFLINE);
            tvCalenderP.setEnabled(false);
            llCalender.setClickable(false);
        } else {
            showVideoLoading();
        }
        initSurfaceView();
        initManageList();
        handler.postDelayed(this::initControllerPanel, 200);
    }

    private boolean isDeviceOffline() {
        return device.getStatus() == DeviceStatus.OFFLINE.ordinal();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initControllerPanel() {
        openMove();
        initVolume();
        scalePanel.setListener(this);
    }

    void initData() {
        screenW = CommonHelper.getScreenWidth(context);
        //当前天
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setCalendarText(day > 9 ? day + "" : "0" + day);
        currentDateSeconds = System.currentTimeMillis() / 1000;
        threeDaysBeforeSeconds = currentDateSeconds - threeDaysSeconds;

        iotcClient = new IOTCClient(device.getUid());
        iotcClient.setCallback(this);//直播回调
        audioDecoder = new AACDecoder();
    }

    private void setCalendarText(String day) {
        tvCalenderP.setText(day);
    }

    private void initSurfaceView() {
        switchOrientation(Configuration.ORIENTATION_PORTRAIT);
        SurfaceHolder surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
//        ivpCloud.setVideoPlayListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        removeCallbacks();
        closeMove();//关闭时间抽的timer
    }

    @Override
    public void onBackPressed() {
        if (isPortrait()) {
            if (llLoading != null && llLoading.isShown()) {
                llLoading.setVisibility(View.GONE);
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
            llChangeVolume.setVisibility(View.GONE);
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
        } else if (v.getId() == R.id.txt_right) {
            if (IpcUtils.isIpcManageable(device.getDeviceid(), device.getStatus())) {
                IpcSettingActivity_.intent(context).mDevice(device).disableAdjustScreen(true).start();
            } else {
                new CommonDialog.Builder(context)
                        .setTitle(R.string.str_device_offline)
                        .setMessage(R.string.msg_device_offline)
                        .setConfirmButton(R.string.str_confirm).create().show();
            }
        }
    }

    @Click(resName = "rl_top")
    void backClick() {
        if (llChangeVolume.isShown()) {
            llChangeVolume.setVisibility(View.GONE);
        }
        if (llVideoQuality.isShown()) {
            llVideoQuality.setVisibility(View.GONE);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Click(resName = "iv_full_screen_portrait")
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
    @Click(resName = "iv_volume")
    void volumeClick() {
        if (llChangeVolume.isShown()) {
            llChangeVolume.setVisibility(View.GONE);
        } else {
            llChangeVolume.setVisibility(View.VISIBLE);
            int currentVolume100 = volumeHelper.get100CurrentVolume();//获取当前音量
            sBarVoice.setProgress(currentVolume100);
            setVolumeViewImage(currentVolume100);
        }
    }

    //静音
    @Click(resName = "iv_volume_portrait")
    void muteClick() {
        if (volumeHelper.isMute()) {
            setVolumeViewImage(1);
            volumeHelper.unMute();
        } else {
            volumeHelper.mute();
            setVolumeViewImage(0);
        }
    }

    //画质
    @Click(resName = "tv_quality")
    void qualityClick() {
        if (playType != PLAY_TYPE_LIVE) {
            return;
        }
        llVideoQuality.setVisibility(llVideoQuality.isShown() ? View.GONE : View.VISIBLE);
        if (qualityType == 0) {
            tvFHDQuality.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
            tvHDQuality.setTextColor(ContextCompat.getColor(this, R.color.c_white));
        } else {
            tvFHDQuality.setTextColor(ContextCompat.getColor(this, R.color.c_white));
            tvHDQuality.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
        }
    }

    //画质
    @Click(resName = "tv_quality_portrait")
    void qualityPortraitClick() {
        if (playType == PLAY_TYPE_LIVE) {
            switchQuality();
        }
    }

    //超清画质
    @Click(resName = {"tv_fhd_quality"})
    void fhdQualityClick() {
        changeQuality(0);
    }

    //高清画质
    @Click(resName = {"tv_hd_quality"})
    void hdQualityClick() {
        changeQuality(1);
    }

    //开始，暂停
    @Click(resName = {"iv_play", "iv_play_portrait"})
    void pausePlayClick() {
        if (isFastClick(1000) || playType == PLAY_TYPE_LIVE) {
            return;
        }
        setIvPlayImage(isPaused);
        isPaused = !isPaused;
        if (playType == PLAY_TYPE_PLAYBACK_DEV) {
            if (iotcClient != null) {
                iotcClient.pausePlayback(isPaused);
            }
        }
//        else if (playType == PLAY_TYPE_PLAYBACK_CLOUD) {
//            if (isPaused) {
//                ivpCloud.pause();
//            } else {
//                ivpCloud.play();
//            }
//        }
    }

    //直播
    @Click(resName = "iv_live")
    void playApBackClick() {
        switch2Live();
    }

    //显示日历
    @Click(resName = "ll_calender_portrait")
    void calenderClick() {
        if (isFastClick(1000)) {
            return;
        }
        showDatePicker();
    }

    //云回放
    @Click(resName = "iv_cloud_playback_portrait")
    void cloudPlaybackClick() {
        if (isFastClick(1000)) {
            return;
        }
        pausePlay();
        CloudPlaybackActivity_.intent(context).device(device)
                .cloudStorageServiceStatus(cloudStorageServiceStatus).start();
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
        hidePlayFail();
        showVideoLoading();
        initP2pLive();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isDeviceOffline()) {
            return;
        }
        if (videoDecoder == null) {
            videoDecoder = new H264Decoder(holder.getSurface(), 0);
            initP2pLive();
        } else {
            resumePlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void initSuccess() {
        hideVideoLoading();
    }

    @UiThread
    @Override
    public void initFail() {
        hideVideoLoading();
        hideControllerPanel();
        showPlayFail(PLAY_FAIL_NET_ERROR);
    }

    @Override
    public void onVideoReceived(byte[] videoBuffer) {
        hidePlayFail();
        if (videoDecoder != null) {
            videoDecoder.setVideoData(videoBuffer);
            if (videoDecoder.isPlaying()) {
                if (playType == PLAY_TYPE_LIVE) {
                    hideVideoLoading();
                } else if (playType == PLAY_TYPE_PLAYBACK_DEV && (tvTimeScroll != null && tvTimeScroll.isShown())) {
//            hideVideoLoading();
                    hideTimeScroll();
                }
            }
        }
    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        if (audioDecoder != null) {
            audioDecoder.setAudioData(audioBuffer);
        }
    }

//    @Override
//    public void onStartPlay() {
//        hideVideoLoading();
//    }
//
//    @Override
//    public void onPlayComplete() {//获取当前播放完毕时间判断是否cloud or ap
//        selectedTimeIsHaveVideo(scalePanel.getCurrentInterval());
//    }
//
//    @Override
//    public void getCloudTimeSlotSuccess(long startTime, long endTime, List<VideoTimeSlotBean> slots) {
//        listCloud.clear();
//        listCloud.addAll(slots);
//        getCanvasList(startTime, endTime);
//    }
//
//    @Override
//    public void getCloudTimeSlotFail() {
//        if (listAp == null || listAp.size() == 0) {
//            hideVideoLoading();
//            switch2Live();//无ap且无cloud的时间列表
//        } else {
//            timeCanvasList(listAp); //ap时间列表>0且cloud列表=0
//        }
//    }

    @Override
    public void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots) {
        if (slots != null && slots.size() > 0) {
            listAp.addAll(slots);
            getDeviceTimeSlots(slots.get(slots.size() - 1).getEndTime(), currentDateSeconds);
        } else {
            if (listAp == null || listAp.size() == 0) {
                hideVideoLoading();
                switch2Live();//无ap且无cloud的时间列表
            } else {
                timeCanvasList(listAp); //ap时间列表>0且cloud列表=0
            }
//            getCloudTimeSlots(device.getId(), threeDaysBeforeSeconds, currentDateSeconds);
        }
    }

    @UiThread
    @Override
    public void startLiveSuccess() {
        ivPlay.setImageResource(R.mipmap.play_disable);
        ivPlayP.setImageResource(R.mipmap.play_disable);
        setPlayType(0);
        scrollCurrentLive();
        hideVideoLoading();
    }

    @UiThread
    @Override
    public void startPlaybackSuccess() {
        setPlayType(1);
        hideVideoLoading();
    }

    @UiThread
    @Override
    public void getCloudVideosSuccess(List<VideoListResp.VideoBean> videoBeans) {
        setPlayType(2);
        List<String> urlList = new ArrayList<>();
        for (VideoListResp.VideoBean bean : videoBeans) {
            urlList.add(bean.getUrl());
        }
//        cloudPlay(urlList);
    }

    @UiThread
    @Override
    public void changeQualitySuccess(int quality) {
        qualityType = quality;
        if (qualityType == 0) {
            tvQuality.setText(R.string.str_FHD);
            tvQualityP.setText(R.string.str_FHD);
            shortTip(R.string.tip_video_quality_fhd);
        } else if (qualityType == 1) {
            tvQuality.setText(R.string.str_HD);
            tvQualityP.setText(R.string.str_HD);
            shortTip(R.string.tip_video_quality_hd);
        }
    }

    @UiThread
    @Override
    public void getStorageSuccess(IpcManageBean bean) {
        list.add(0, bean);
        cloudStorageServiceStatus = bean.getStatus();
        adapter.notifyDataSetChanged();
    }

    @Override
    public int[] getUnStickNotificationId() {
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
            list.remove(0);
            mPresenter.getStorageList(device.getDeviceid());
        }
    }

    private void resumePlay() {
        if (llPlayFail != null && llPlayFail.isShown() || videoDecoder == null) {
            return;
        }
        setPanelVisible(View.VISIBLE);
        if (playType == PLAY_TYPE_LIVE && iotcClient != null) {
            showVideoLoading();
            iotcClient.startPlay();
        } else if (playType == PLAY_TYPE_PLAYBACK_DEV) {
            videoDecoder.startDecode();
        }
    }

    private void pausePlay() {
        if (playType != PLAY_TYPE_LIVE && !isPaused) {
            pausePlayClick();
        } else if (playType == PLAY_TYPE_LIVE && iotcClient != null) {
            iotcClient.stopLive();
            if (audioDecoder != null) {
                audioDecoder.stopRunning();
            }
        }
    }

    @UiThread
    public void showVideoLoading() {
        llLoading.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hideVideoLoading() {
        llLoading.setVisibility(View.GONE);
    }

    @UiThread
    public void showPlayFail(int type) {
        if (PLAY_FAIL_OFFLINE == type) {
            tvRetry.setVisibility(View.GONE);
            tvPlayFail.setText(R.string.tip_ipc_offline);
        } else if (PLAY_FAIL_NET_ERROR == type) {
            tvRetry.setVisibility(View.VISIBLE);
            tvPlayFail.setText(R.string.tip_network_fail_retry);
        }
        llPlayFail.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hidePlayFail() {
        llPlayFail.setVisibility(View.GONE);
    }

    private void setIvPlayImage(boolean isPaused) {
        ivPlay.setImageResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        ivPlayP.setImageResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
    }

    private void setPlayType(int type) {
        playType = type;
//        ivpCloud.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        videoView.setVisibility(type != 2 ? View.VISIBLE : View.GONE);
        ivLive.setVisibility(type != 0 ? View.VISIBLE : View.GONE);
        setTextViewClickable(tvQuality, type == 0);
        setTextViewClickable(tvQualityP, type == 0);
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
            ivPlay.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
            setPortraitViewVisible(View.GONE);
            setLandscapeViewVisible(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ivPlay.setVisibility(View.GONE);
            setPortraitViewVisible(View.VISIBLE);
            setLandscapeViewVisible(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
        setVideoParams(orientation);
    }

    private void setLandscapeViewVisible(int visibility) {
        tvQuality.setVisibility(visibility);
        ivVolume.setVisibility(visibility);
        setPanelVisible(View.VISIBLE);
    }

    private void setPortraitViewVisible(int visibility) {
        if (rvManager == null) return;
        titleBar.setVisibility(visibility);
        rlVideoController.setVisibility(visibility);
        llPortraitBar.setVisibility(visibility);
        rvManager.setVisibility(visibility);
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

        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = videoW;
        lp.height = videoH;
        videoView.setLayoutParams(lp);

//        ViewGroup.LayoutParams lpCloud = ivpCloud.getLayoutParams();
//        lpCloud.width = videoW;
//        lpCloud.height = videoH;
//        ivpCloud.setLayoutParams(lpCloud);
    }

    private boolean isSS1() {
        return DeviceTypeUtils.getInstance().isSS1(device.getModel());
    }

    private void stopPlay() {
//        cloudPlayDestroy();//关闭云端视频
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

    //开始直播
    @Background
    void initP2pLive() {
        if (iotcClient != null) {
            iotcClient.init();
        }
    }

    @UiThread
    void hideControllerPanel() {
        setPanelVisible(View.GONE);
        llChangeVolume.setVisibility(View.GONE);//音量
        llVideoQuality.setVisibility(View.GONE);//画质
    }

    private void setPanelVisible(int visible) {
        if (rlTopBar != null && rlBottomBar != null) {
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

    /**
     * 切回直播
     */
    private void switch2Live() {
        isFirstScroll = true;
        showVideoLoading();
        //如果是云端回放此时需要调用停止操作然后直播
//        if (playType == PLAY_TYPE_PLAYBACK_CLOUD) {
//            cloudPlayDestroy();
//        }
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
        showVideoLoading();
//        if (playType == PLAY_TYPE_PLAYBACK_CLOUD) {
//            cloudPlayDestroy();
//        }
        mPresenter.startPlayback(iotcClient, start);
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

    private void changeQuality(int type) {
        llVideoQuality.setVisibility(View.GONE);
        if (type == qualityType) {
            return;
        }
        mPresenter.changeQuality(type, iotcClient);
    }

//    /**
//     * 切到云端回放
//     */
//    void switch2CloudPlayback(long start, long end) {
//        if (iotcClient == null) {
//            return;
//        }
//        showVideoLoading();
//        if (playType == PLAY_TYPE_PLAYBACK_DEV) {
//            iotcClient.stopPlayback();//先停止设备回放
//        } else if (playType == PLAY_TYPE_LIVE) {
//            iotcClient.stopLive();//先停止直播
//        }
//        mPresenter.getCloudVideoList(device.getId(), start, end);
//    }
//
//    /**
//     * 播放云端回放
//     */
//    private void cloudPlay(List<String> urlList) {
//        hideVideoLoading();
//        ivpCloud.setUrlQueue(urlList);
//        try {
//            ivpCloud.startPlay();
//        } catch (Exception e) {
//            shortTip(R.string.tip_play_fail);
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * 云端回放销毁
//     */
//    private void cloudPlayDestroy() {
//        try {
//            if (ivpCloud != null) {
//                ivpCloud.release();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
                setVolumeViewImage(progress);
                volumeHelper.setVoice100(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        setVolumeViewImage(currentVolume100);
    }

    private void setVolumeViewImage(int currentVolume100) {
        if (currentVolume100 == 0) {
            ivVolume.setBackgroundResource(R.mipmap.ic_muse);
            ivVolumeP.setImageResource(R.mipmap.ic_muse);
        } else {
            ivVolume.setBackgroundResource(R.mipmap.ic_volume);
            ivVolumeP.setImageResource(R.mipmap.ic_volume);
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
        setCalendarText(scalePanel.currentTimeStr().substring(6, 8));
        executorService.scheduleAtFixedRate(this::moveTo, 60, 60, TimeUnit.SECONDS);
    }

    @UiThread
    void moveTo() {
        scalePanel.autoMove();
        //自动滑动时下一个视频ap还是cloud播放
        if (playType != PLAY_TYPE_LIVE && isVideoLess1Minute) {
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
            isFirstScroll = false;
            selectedTimeIsHaveVideo(selectedDate); //初始化左滑渲染及回放
        } else {
            if (isSelectedDate) {
                selectedTimeIsHaveVideo(selectedDate);//滑动到选择日期
            } else {
                scalePanel.refreshNow(); //滚动到当前时间
            }
        }
        //渲染完成
        hideVideoLoading();
    }

    //选择日历日期回调
    @SuppressLint("DefaultLocale")
    public void onSureButton(Date date) {
        long currentTime = System.currentTimeMillis() / 1000;//当前时间戳秒
        scrollTime = date.getTime();//选择日期的时间戳毫秒
        long time = scrollTime / 1000; //设置日期的秒数
        if (time > currentTime) {//未来时间或当前--滑动当前直播
            isSelectedDate = false;
            scrollTime = System.currentTimeMillis();
            setCalendarText(String.format("%td", new Date()));
            if (playType == PLAY_TYPE_LIVE) {
                return;
            }
            switch2Live();
        } else {//回放时间
            isFirstScroll = false;//非首次滑动
            isSelectedDate = true;
            setIvPlayImage(true);
            ivLive.setVisibility(View.VISIBLE);

            String strDate = DateTimeUtils.secondToDate(time, "yyyy-MM-dd");
            int year = Integer.valueOf(strDate.substring(0, 4));
            int month = Integer.valueOf(strDate.substring(5, 7));
            int day = Integer.valueOf(strDate.substring(8, 10));
            //显示日历天数
            setCalendarText(String.format("%td", date));
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
        setIvPlayImage(true);
        isPaused = false;
        scalePanel.moveToTime(currentTimeMinutes);
        openMove();
    }

    /**
     * 滑动到当前时间
     * <p>
     * 1 回放视频为空
     * 2 点击直播按钮
     */
    private void scrollCurrentLive() {
        playType = PLAY_TYPE_LIVE;
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
//                if (isCloud) {
//                    switch2CloudPlayback(endOpposite, endOpposite + tenMinutes);
//                } else {
                switch2DevPlayback(endOpposite);
//                }
                scrollCurrentPlayBackTime(endOpposite);//回放到拖动的时间点
                break;
            } else if (currTime >= start && currTime < end) {//视频区域
                boolean isCloud = !listAp.get(i).isApPlay();
                //当前的视频片段是否小于一分钟
                isVideoLess1Minute = listAp.get(i).getEndTime() - currTime <= 60;
//                if (isCloud) {
//                    switch2CloudPlayback(currTime, currTime + tenMinutes);
//                } else {
                switch2DevPlayback(currTime);
//                }
                scrollCurrentPlayBackTime(currTime);//回放到拖动的时间点
                break;
            }
        }
    }

    private void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private void switch2Playback(long currTime) {
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
//                    boolean isCloud = !listAp.get(i + 1).isApPlay();
                    final int delayMillis = (int) end - currTime < 0 ? 1 : (int) (end - currTime);
                    final int finalI = i;
//                    if (isCloud) {
//                        handler.postDelayed(() -> {
//                            switch2CloudPlayback(listAp.get(finalI + 1).getStartTime(),
//                                    listAp.get(finalI + 1).getStartTime() + tenMinutes);
//                            videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime()); //偏移跳转
//                        }, delayMillis * 1000);
//                        break;
//                    } else {
                    handler.postDelayed(() -> {
                        switch2DevPlayback(listAp.get(finalI + 1).getStartTime());
                        videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime());//偏移跳转
                    }, delayMillis * 1000);
                    break;
//                    }
                }
            }
        }
    }

    //发送请求获取组合时间轴
    private void refreshTimeSlotVideoList() {
        showVideoLoading();
        listAp.clear();
        getDeviceTimeSlots(threeDaysBeforeSeconds, currentDateSeconds);
    }

//    //获取cloud回放时间轴
//    public void getCloudTimeSlots(int deviceId, long startTime, long endTime) {
//        mPresenter.getTimeSlots(deviceId, startTime, endTime);
//    }

    //获取设备sd卡回放时间轴
    public void getDeviceTimeSlots(long startTime, long endTime) {
        mPresenter.getPlaybackList(iotcClient, startTime, endTime);
    }

//    //时间轴组合
//    private void getCanvasList(long mStartTime, long mEndTime) {
//        int apSize = listAp.size();
//        int cloudSize = listCloud.size();
//        if (apSize == 0 && cloudSize > 0) {
//            listAp = listCloud;
//            timeCanvasList(listAp);//组合时间轴渲染
//            return;
//        }
//        VideoTimeSlotBean bean;
//        //AP时间
//        for (int i = 0; i < apSize + 1; i++) {
//            long startAp = 0, endAp = 0;
//            //不包含ap时间轴内的时间
//            if (i == 0) {
//                startAp = mStartTime;
//                endAp = listAp.get(i).getStartTime();
//            } else if (i < apSize) {
//                startAp = listAp.get(i - 1).getEndTime();
//                endAp = listAp.get(i).getStartTime();
//            } else if (i == apSize) {
//                startAp = listAp.get(i - 1).getEndTime();
//                endAp = mEndTime;
//            }
//            //cloud时间
//            for (int j = 0; j < cloudSize; j++) {
//                bean = new VideoTimeSlotBean();
//                long startCloud = listCloud.get(j).getStartTime();
//                long endCloud = listCloud.get(j).getEndTime();
//
//                if (startCloud >= startAp && endAp > startCloud && endCloud >= endAp) {
//                    bean.setStartTime(startCloud);
//                    bean.setEndTime(endAp);
//                    bean.setApPlay(false);
//                    listAp.add(bean);
//                } else if (startAp >= startCloud && endCloud > startAp && endAp >= endCloud) {
//                    bean.setStartTime(startAp);
//                    bean.setEndTime(endCloud);
//                    bean.setApPlay(false);
//                    listAp.add(bean);
//                } else if (startAp != endAp && startAp >= startCloud && endAp <= endCloud) {
//                    bean.setStartTime(startAp);
//                    bean.setEndTime(endAp);
//                    bean.setApPlay(false);
//                    listAp.add(bean);
//                } else if (startCloud != endCloud && startCloud >= startAp && endCloud <= endAp) {
//                    bean.setStartTime(startCloud);
//                    bean.setEndTime(endCloud);
//                    bean.setApPlay(false);
//                    listAp.add(bean);
//                }
//            }
//        }
//        if (cloudSize > 0) {
//            listAp = duplicateRemoval(listAp);//去重
//            Collections.sort(listAp);//正序比较
//        }
//        timeCanvasList(listAp);//组合时间轴渲染
//    }

    //去重
    private List<VideoTimeSlotBean> duplicateRemoval(List<VideoTimeSlotBean> list) {
        LinkedHashSet<VideoTimeSlotBean> tmpSet = new LinkedHashSet<>(list.size());
        tmpSet.addAll(list);
        list.clear();
        list.addAll(tmpSet);
        return list;
    }

    @Override
    public void didMoveToTime(long timeStamp) {
        showVideoLoading();
        hideTimeScroll();
        if (timeStamp > System.currentTimeMillis() / 1000) {//超过当前时间
            shortTip(getString(R.string.ipc_time_over_current_time));
            if (playType == PLAY_TYPE_LIVE) {//当前处于直播
                scrollCurrentLive();
            } else {//当前处于回放
                switch2Live();
            }
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

    @UiThread
    void hideTimeScroll() {
        handler.postDelayed(() -> tvTimeScroll.setVisibility(View.GONE), 500);
    }

    /**
     * 延时执行滑动处理，防止无视频区域直接跳过
     */
    private void startDelayPlay(long timeStamp) {
        cancelDelayPlay();
        timeLineScrollTimer = new CountDownTimer(800, 200) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                openMove();
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

    private void switchQuality() {
        if (qualityPop == null) {
            qualityPop = new BottomPopMenu.Builder(this)
                    .addItemAction(new PopItemAction(R.string.str_HD,
                            PopItemAction.PopItemStyle.Normal, this::hdQualityClick))
                    .addItemAction(new PopItemAction(R.string.str_FHD,
                            PopItemAction.PopItemStyle.Normal, this::fhdQualityClick))
                    .create();
        }
        qualityPop.show();
    }

    private void initManageList() {
        rvManager.init(0);
      /*  if (!isSS1()) {
            list.add(new IpcManageBean(R.mipmap.ipc_manage_face_history,
                    getString(R.string.str_face_history),
                    getString(R.string.str_view_face_history), getString(R.string.str_coming_soon), false));
        }*/
        list.add(new IpcManageBean(R.mipmap.ipc_manage_md, getString(R.string.str_motion_detection),
                getString(R.string.str_md_exception), getString(R.string.str_coming_soon), false));
        adapter = new CommonListAdapter<IpcManageBean>(context,
                R.layout.item_ipc_manager, list) {
            @Override
            public void convert(ViewHolder holder, IpcManageBean bean) {
                Button btnDetail = holder.getView(R.id.btn_detail);
                holder.setImageResource(R.id.iv_icon, bean.getLeftImageResId());
                holder.setText(R.id.tv_title, bean.getTitle());
                holder.setText(R.id.tv_summary, bean.getSummary());
                holder.setText(R.id.btn_detail, bean.getRightText());
                btnDetail.setEnabled(bean.isEnabled());
                holder.setImageResource(R.id.iv_tag, bean.getTagImageResId());
                btnDetail.setOnClickListener(v -> {
                    if (bean.getLeftImageResId() == R.mipmap.ipc_cloud_storage) {
                        if (TextUtils.equals(bean.getRightText(), getString(R.string.str_setting_detail))) {
                            Router.withApi(SunmiServiceApi.class).goToServiceDetail(device.getDeviceid(),
                                    true, device.getName());
                        } else {
                            ArrayList<String> snList = new ArrayList<>();
                            snList.add(device.getDeviceid());
                            Router.withApi(SunmiServiceApi.class)
                                    .goToWebViewCloud(CommonConfig.CLOUD_STORAGE_URL, snList);
                        }
                    }
                });
            }
        };
        rvManager.setAdapter(adapter);
    }

}
