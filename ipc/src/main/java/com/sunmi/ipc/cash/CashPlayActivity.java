package com.sunmi.ipc.cash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.adapter.CashAdapter;
import com.sunmi.ipc.contract.CashVideoContract;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.presenter.CashVideoPresenter;
import com.sunmi.ipc.view.activity.CloudPlaybackActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ServiceListResp;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.InputDialog;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * @author yangShiJie
 * @date 2019-12-02
 */
@EActivity(resName = "cash_activity_play")
@SuppressLint("ClickableViewAccessibility")
public class CashPlayActivity extends BaseMvpActivity<CashVideoPresenter> implements
        CashVideoContract.View,
        VolumeHelper.VolumeChangeListener,
        SeekBar.OnSeekBarChangeListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnErrorListener {
    /**
     * 滑屏上下一段视频的滑动距离
     */
    private static final int MOVE_SCREEN_POSITION = 60;
    /**
     * 同步进度
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1000;
    /**
     * 延迟毫秒数
     */
    private static final int DELAY_MILLIS = 10;
    /**
     * 正常，异常视频标记
     */
    private static final int CASH_TAG_NORMAL = 1;
    private static final int CASH_TAG_EXCEPTION = 2;
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
    /**
     * 超时
     */
    private final CashCountDownTimer connectTimeout = new CashCountDownTimer(15 * 1000, 1000);
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;
    @ViewById(resName = "ivp_cash")
    IVideoPlayer ivpCash;
    @ViewById(resName = "rl_cash")
    RelativeLayout rlCashVideo;
    @ViewById(resName = "tv_play_fail")
    TextView tvPlayFail;
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "rl_order_info")
    RelativeLayout rlOrderInfo;
    @ViewById(resName = "pBar_loading")
    ProgressBar pBarLoading;
    @ViewById(resName = "tv_amount")
    TextView tvAmount;
    @ViewById(resName = "tv_order_no")
    TextView tvOrderNo;
    @ViewById(resName = "tv_total_commodity")
    TextView tvTotalCommodity;
    @ViewById(resName = "tv_trade_type")
    TextView tvTradeType;
    @ViewById(resName = "ib_play")
    ImageButton ibPlay;
    @ViewById(resName = "tv_current_play_time")
    TextView tvCurrentPlayTime;
    @ViewById(resName = "tv_count_play_time")
    TextView tvCountPlayTime;
    @ViewById(resName = "sb_bar")
    SeekBar sbBar;
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;
    @ViewById(resName = "iv_play_type")
    ImageView ivPlayType;
    @ViewById(resName = "tv_screenshot_tip")
    TextView tvScreenshotTip;
    @ViewById(resName = "tv_tag")
    TextView tvTag;
    @ViewById(resName = "iv_video_change")
    ImageView ivVideoChange;
    @ViewById(resName = "tv_empty")
    TextView tvEmpty;
    /**
     * ipc名称 ，视频列表 ，是否一天快放,设备id, 一天快放的开始结束时间 ,是否有更多列表数据（一天快放或点击item进入）
     */
    @Extra
    HashMap<Integer, String> ipcName;
    @Extra
    ArrayList<CashVideoResp.AuditVideoListBean> videoList = new ArrayList<>();
    @Extra
    boolean isWholeDayVideoPlay;
    @Extra
    int deviceId;
    @Extra
    long startTime, endTime;
    @Extra
    boolean hasMore;
    /**
     * pageNum 视频列表第几页， videoListPosition 视频列表点击的position ，videoType视频列表类型：异常，正常
     */
    @Extra
    int pageNum, videoListPosition, videoType;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 是否暂停，是否静音，是否初始化了截屏
     */
    private boolean isPaused, isOpenVolume;
    /**
     * 是否自动播放
     */
    private boolean isPlayLoop;
    /**
     * 播放视频的index ,当前播放的position
     */
    private int playIndex, currentPlayPosition;
    /**
     * 点击item页码
     */
    private int mWholeDayPlayPageNum;
    private CashAdapter adapter;
    /**
     * 视频订单详情
     */
    private List<CashOrderResp.ProductListBean> productList = new ArrayList<>();
    private CashVideoPopupWindow popupWindow;
    private float posX, posY, curPosX;
    private VolumeHelper volumeHelper = null;
    private FFmpegMediaMetadataRetriever retriever;
    private View.OnTouchListener cashTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    posX = event.getX();
                    posY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    curPosX = event.getX();
                    ivVideoChange.setVisibility(View.VISIBLE);
                    if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
                        ivVideoChange.setImageResource(R.mipmap.ic_cash_previous);
                    } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
                        ivVideoChange.setImageResource(R.mipmap.ic_cash_next);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    ivVideoChange.setVisibility(View.GONE);
                    if (posY > CommonHelper.getScreenWidth(context) - context.getResources().getDimension(R.dimen.dp_100)) {
                        return false;
                    }
                    if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
                        if (playIndex > 0) {
                            playIndex--;
                            initCashVideoPlay();
                        } else {
                            shortTip(R.string.cash_no_more_video);
                        }
                    } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
                        if (playIndex < videoList.size() - 1) {
                            playIndex++;
                            initCashVideoPlay();
                        } else {
                            loadMoreVideoList();
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //滑动中，同步播放进度
            if (msg.what == MESSAGE_SHOW_PROGRESS) {
                if (!isDragging) {
                    msg = obtainMessage(MESSAGE_SHOW_PROGRESS, ivpCash.getCurrentPosition());
                    sendMessageDelayed(msg, DELAY_MILLIS);
                    syncProgress(msg.obj);
                }
            }
        }
    };

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new CashVideoPresenter();
        mPresenter.attachView(this);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        initVolume();
        initScreenWidthHeight();
        initInfo();
        showLoading();
        if (isWholeDayVideoPlay) {
            //初始化一天快放
            hasMore = true;
//            isPlayLoop = true;
            loadMoreVideoList();
        } else {
            playIndex = videoListPosition;
            initCashVideoPlay();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isWholeDayVideoPlay) {
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            bundle.putInt("videoListPosition", playIndex);
            bundle.putSerializable("videoList", videoList);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    /**
     * 开启计时
     */
    private void startCountDownTimer() {
        connectTimeout.start();
    }

    /**
     * 关闭计时
     */
    private void stopCountDownTimer() {
        connectTimeout.cancel();
    }

    private void initScreenWidthHeight() {
        int screenWidth = CommonHelper.getScreenWidth(context);
        ViewGroup.LayoutParams params = rlCashVideo.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth;
        rlCashVideo.setLayoutParams(params);
        ViewGroup.LayoutParams paramsCash = ivpCash.getLayoutParams();
        paramsCash.width = screenWidth;
        paramsCash.height = screenWidth;
        ivpCash.setLayoutParams(paramsCash);
    }

    private void initInfo() {
        titleBar.getAppTitle().setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_arrow_down_big_gray), null);
        titleBar.getAppTitle().setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_5));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        titleBar.getAppTitle().setOnClickListener(v -> setTitleView());
        new Handler().postDelayed(() -> ivpCash.setVisibility(View.VISIBLE), 800);
        ivpCash.setOnTouchListener(cashTouchListener);
    }

    private void initVolume() {
        volumeHelper = new VolumeHelper(this);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
    }

    private void setVideoListener() {
        sbBar.setOnSeekBarChangeListener(this);
        ivpCash.setOnPreparedListener(this);
        ivpCash.setOnCompletionListener(this);
        ivpCash.setOnErrorListener(this);
    }

    private void setTitleView() {
        if (videoList == null || videoList.size() == 0) {
            shortTip(R.string.str_no_cash_video);
            return;
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            titleBar.getAppTitle().setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_arrow_down_big_gray), null);
            popupWindow.dismiss();
            return;
        }
        titleBar.getAppTitle().setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_arrow_up_big_gray), null);
        popupWindow = new CashVideoPopupWindow(CashPlayActivity.this, titleBar, currentPlayPosition,
                videoList, titleBar.getAppTitle());
    }

    //视频标记
    private void setViewVideoTag() {
        if (videoType() == CASH_TAG_NORMAL) {
            tvTag.setText(R.string.cash_tag_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.c_white));
        } else if (videoType() == CASH_TAG_EXCEPTION) {
            tvTag.setText(R.string.cash_tag_cancel_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        pausedVideo(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausedVideo(true);
    }

    /**
     * 退到后台
     *
     * @param paused 是否暂停
     */
    private void pausedVideo(boolean paused) {
        if (ivpCash == null || pBarLoading.isShown() || sbBar.getProgress() >= ivpCash.getDuration()) {
            return;
        }
        if (paused) {
            ivpCash.pauseVideo();
        } else {
            ivpCash.startVideo();
        }
        isPaused = paused;
        ibPlay.setBackgroundResource(isPaused ? R.mipmap.play_normal : R.mipmap.pause_normal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        stopCountDownTimer();
        volumeHelper.unregisterVolumeReceiver();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private String videoUrl() {
        return videoList.get(playIndex).getVideoUrl();
    }

    private int videoType() {
        return videoList.get(playIndex).getVideoType();
    }

    private String orderNo() {
        return videoList.get(playIndex).getOrderNo();
    }

    private int auditVideoId() {
        return videoList.get(playIndex).getVideoId();
    }

    /**
     * 手势滑动到最后一个,或自动播放最后一个
     */
    private void loadMoreVideoList() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            showPlayFail(getStringById(R.string.network_error));
            return;
        }
        if (hasMore) {
            if (isWholeDayVideoPlay) {
                //一天快放
                mPresenter.getCashVideoList(ipcName, deviceId, -1, startTime, endTime, mWholeDayPlayPageNum++, 10);
            } else {
                //item点击进入是否有更多
                mPresenter.getCashVideoList(ipcName, deviceId, videoType, startTime, endTime, pageNum++, 10);
            }
        } else {
            shortTip(R.string.tip_no_more_data);
        }
    }

    //取消标记
    private void requestCancelTag() {
        mPresenter.updateTag(auditVideoId(), "", CASH_TAG_NORMAL);
    }

    //添加标记
    private void requestAddTag(String des) {
        mPresenter.updateTag(auditVideoId(), des, CASH_TAG_EXCEPTION);
    }

    //订单详情
    private void requestOrderInfo() {
        mPresenter.getOrderInfo(orderNo());
    }

    /**
     * 检测是否需要读写权限
     */
    private void checkRequestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else {
                saveVideoFrameAtTime();
            }
        }
    }

    /**
     * 获取权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //已授权
                saveVideoFrameAtTime();
            } else {
                //未授权提示dialog
                shortTip(R.string.cash_creenshot_permission_tip);
            }
        }
    }

    /**
     * 初始化设置截屏数据
     */
    private void initTakeScreenShot() {
        retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(videoUrl());
        } catch (Exception e) {
            shortTip(R.string.cash_init_screenshot_exception);
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
        if (ivpCash.getCurrentPosition() > 0 && retriever != null) {
            tvScreenshotTip.setVisibility(View.VISIBLE);
            final Bitmap bitmap = retriever.getFrameAtTime(ivpCash.getCurrentPosition() * 1000,
                    FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            new Handler().postDelayed(() -> {
                if (ImageUtils.saveImageToGallery(context, bitmap, 100)) {
                    shortTip(getString(R.string.ipc_dynamic_take_screen_shot_success));
                } else {
                    shortTip(getString(R.string.ipc_dynamic_take_screen_shot_fail));
                }
                tvScreenshotTip.setVisibility(View.GONE);
            }, 1000);
        }
    }

    /**
     * 重试
     */
    @Click(resName = "btn_retry")
    void retryClick() {
        ivpCash.setVisibility(View.VISIBLE);
        if (isWholeDayVideoPlay && videoList.size() == 0) {
            loadMoreVideoList();
        } else {
            initCashVideoPlay();
        }
    }

    /**
     * 播放-暂停
     */
    @Click(resName = "ib_play")
    void onPlayClick() {
        if (ivpCash == null || pBarLoading.isShown() || sbBar.getProgress() >= ivpCash.getDuration()) {
            return;
        }
        ibPlay.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isPaused) {
            ivpCash.pauseVideo();
        } else {
            ivpCash.startVideo();
        }
    }

    /**
     * 截图
     */
    @Click(resName = "iv_screenshot")
    void screenShotClick() {
        if (pBarLoading.isShown()) {
            return;
        }
        LogCat.e(TAG, "1111 screenshot isPlayLoop=" + isPlayLoop);
        checkRequestPermissions();
    }

    /**
     * 音量
     */
    @Click(resName = "iv_volume")
    void volumeClick() {
        ivVolume.setImageResource(isOpenVolume ? R.mipmap.ic_volume : R.mipmap.ic_muse);
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
            ivVolume.setImageResource(R.mipmap.ic_muse);
        } else {
            isOpenVolume = false;
            volumeHelper.unMute();
            ivVolume.setImageResource(R.mipmap.ic_volume);
        }
    }

    /**
     * 播放方式
     */
    @Click(resName = "iv_play_type")
    void playTypeClick() {
        isPlayLoop = !isPlayLoop;
        ivPlayType.setImageResource(isPlayLoop ? R.mipmap.ic_loop : R.mipmap.ic_single);
    }

    /**
     * 云端播放
     */
    @Click(resName = "iv_cloud")
    void cloudPlayClick() {
        if (pBarLoading.isShown()) {
            return;
        }
        mPresenter.getStorageList(videoList.get(playIndex).getDeviceSn());
    }

    /**
     * 标记 1:正常视频，2:异常视频
     */
    @Click(resName = "tv_tag")
    void tagClick() {
        if (videoType() == CASH_TAG_EXCEPTION) {
            requestCancelTag();
            return;
        }
        pausedVideo(true);
        InputDialog inputDialog = new InputDialog.Builder(this)
                .setTitle(R.string.cash_input_title_tag_type)
                .setHint(R.string.cash_input_title_tag_tip)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, (dialog, input) -> {
                    if (TextUtils.isEmpty(input)) {
                        shortTip(R.string.cash_input_title_tag_type);
                        return;
                    }
                    if (input.length() > 30) {
                        shortTip(R.string.ipc_face_name_length_tip);
                        return;
                    }
                    dialog.dismiss();
                    requestAddTag(input);
                }).create();
        inputDialog.setCancelable(false);
        inputDialog.showWithOutTouchable(false);
    }

    /**
     * 初始化播放
     */
    private void initCashVideoPlay() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            showPlayFail(getStringById(R.string.network_error));
            return;
        }
        LogCat.e(TAG, "11111 00playIndex=" + playIndex + ", currentPlayPosition=" + currentPlayPosition);
        if (videoList != null) {
            if (playIndex >= videoList.size() - 1 && isPlayLoop) {
                if (hasMore) {
                    loadMoreVideoList();
                } else {
                    shortTip(R.string.cash_play_complete);
                }
                return;
            }
            showLoading();
            //自动下一段播放
            if (isPlayLoop) {
                playIndex++;
            }
            //异常标记
            setViewVideoTag();
            currentPlayPosition = playIndex;
            LogCat.e(TAG, "11111 11playIndex=" + playIndex + ", currentPlayPosition=" + currentPlayPosition);
            ivpCash.release();
            sbBar.setProgress(0);
            new Handler().postDelayed(() -> {
                ivpCash.load(videoUrl());
                setVideoListener();
                tvCurrentPlayTime.setText(ivpCash.generateTime(0));
            }, 200);
        }
    }

    private void startCashPreparedPlay() {
        if (ivpCash != null) {
            hideLoading();
            isPaused = false;
            ibPlay.setBackgroundResource(R.mipmap.pause_normal);
            //查询当前视频订单信息
            requestOrderInfo();
            //开始播放
            ivpCash.startVideo();
            //初始化截屏
            initTakeScreenShot();
            //设置seekBar的最大限度值，当前视频的总时长（毫秒）
            long duration = ivpCash.getDuration();
            //不足一秒补一秒
            if (duration % 1000 > 0) {
                duration = duration + (1000 - duration % 1000);
            }
            sbBar.setMax((int) duration);
            //视频总时长
            tvCountPlayTime.setText(String.format("/ %s", Objects.requireNonNull(ivpCash).generateTime(duration)));
            //发送当前播放时间点通知
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    @UiThread
    public void showLoading() {
        startCountDownTimer();
        if (!pBarLoading.isShown()) {
            pBarLoading.setVisibility(View.VISIBLE);
        }
        llPlayFail.setVisibility(View.GONE);
        rlOrderInfo.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    @UiThread
    public void hideLoading() {
        stopCountDownTimer();
        pBarLoading.setVisibility(View.GONE);
    }

    /**
     * 播放失败
     *
     * @param tip
     */
    @UiThread
    public void showPlayFail(String tip) {
        hideLoading();
        stopPlay();
        tvPlayFail.setText(tip);
        llPlayFail.setVisibility(View.VISIBLE);
        rlOrderInfo.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        productList.clear();
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        try {
            if (ivpCash != null) {
                ivpCash.setVisibility(View.INVISIBLE);
                ivpCash.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            if (progress + DELAY_MILLIS >= ivpCash.getDuration()) {
                progress = sbBar.getMax();
                generateTime = sbBar.getMax();//毫秒
            } else {
                generateTime = (Long) obj;
            }
            sbBar.setProgress(progress);
            tvCurrentPlayTime.setText(ivpCash.generateTime(generateTime));
        }
    }


    /**
     * 音量改变
     *
     * @param volume volume
     */
    @Override
    public void onVolumeChanged(int volume) {
        setVolumeViewImage(volume);
    }

    /**
     * 进度条滑动监听
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (pBarLoading.isShown()) {
            return;
        }
        if (fromUser) {
            String time = ivpCash.generateTime(progress);
            tvCurrentPlayTime.setText(time);
        }
    }

    /**
     * 开始拖动
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (pBarLoading.isShown()) {
            return;
        }
        isDragging = true;
        mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
    }

    /**
     * 停止拖动
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (ivpCash != null) {
            ivpCash.seekTo(seekBar.getProgress());
            if (!ivpCash.isPlaying()) {
                ivpCash.startVideo();
                isPaused = false;
                ibPlay.setBackgroundResource(R.mipmap.pause_normal);
            }
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "1111 onCompletion");
        if (ivpCash != null) {
            isPaused = true;
            ibPlay.setBackgroundResource(R.mipmap.play_normal);
        }
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        }
        sbBar.setProgress(sbBar.getMax());
        tvCurrentPlayTime.setText(ivpCash.generateTime(sbBar.getMax()));
        initCashVideoPlay();
    }

    /**
     * 失败
     */
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        LogCat.e(TAG, "1111 onError" + i + ", " + i1);
        hideLoading();
        showPlayFail(getStringById(R.string.network_error));
        return false;
    }

    /**
     * 开始播放
     *
     * @param iMediaPlayer iMediaPlayer
     */
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        LogCat.e(TAG, "1111 onPrepared");
        startCashPreparedPlay();
    }

    /**
     * 设置标记更新列表
     */
    private void updateVideoTag(int videoType, String description) {
        CashVideoResp.AuditVideoListBean bean = new CashVideoResp.AuditVideoListBean();
        bean.setVideoType(videoType);
        bean.setDescription(description);
        bean.setAmount(videoList.get(playIndex).getAmount());
        bean.setDeviceName(videoList.get(playIndex).getDeviceName());
        bean.setOrderNo(videoList.get(playIndex).getOrderNo());
        bean.setPurchaseTime(videoList.get(playIndex).getPurchaseTime());
        bean.setVideoUrl(videoList.get(playIndex).getVideoUrl());
        bean.setVideoId(videoList.get(playIndex).getVideoId());
        bean.setDeviceSn(videoList.get(playIndex).getDeviceSn());
        bean.setSnapshotUrl(videoList.get(playIndex).getSnapshotUrl());
        videoList.set(playIndex, bean);
    }

    @Override
    public void updateTagSuccess(int videoType, String description) {
        if (videoType == CASH_TAG_NORMAL) {
            tvTag.setText(R.string.cash_tag_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.c_white));
            shortTip(R.string.cash_tag_cancel_success);
            updateVideoTag(CASH_TAG_NORMAL, description);
        } else if (videoType == CASH_TAG_EXCEPTION) {
            tvTag.setText(R.string.cash_tag_cancel_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
            shortTip(R.string.cash_tag_success);
            updateVideoTag(CASH_TAG_EXCEPTION, description);
        }
    }

    @Override
    public void updateTagFail(int code, String msg, int videoType) {
        if (videoType == CASH_TAG_NORMAL) {
            tvTag.setText(R.string.cash_tag_cancel_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.common_orange));
            shortTip(R.string.cash_tag_cancel_fail);
        } else if (videoType == CASH_TAG_EXCEPTION) {
            tvTag.setText(R.string.cash_tag_exception);
            tvTag.setTextColor(ContextCompat.getColor(this, R.color.c_white));
            shortTip(R.string.cash_tag_fail);
        }
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void getOrderInfoSuccess(CashOrderResp resp) {
        productList = resp.getProductList();
        tvEmpty.setVisibility(View.GONE);
        tvAmount.setText(String.format("¥%s", resp.getAmount()));
        tvOrderNo.setText(resp.getOrderNo());
        tvTotalCommodity.setText(getString(R.string.cash_video_total_commodity, resp.getTotalQuantity()));
        tvTradeType.setText(resp.getPurchaseType());
        adapter = new CashAdapter(context, productList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getOrderInfoFail(int code, String msg) {
        productList.clear();
        tvAmount.setText("");
        tvOrderNo.setText("");
        tvTotalCommodity.setText("");
        tvTradeType.setText("");
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(R.string.cash_empty_data);
    }

    /**
     * 获取一天快放,item列表选择播放的视频
     */
    @Override
    public void cashVideoListSuccess(List<CashVideoResp.AuditVideoListBean> list) {
        hasMore = list.size() >= 10;
        videoList.addAll(list);
        if (videoList.size() == 0) {
            showPlayFail(getString(R.string.str_no_cash_video));
            return;
        }
        initCashVideoPlay();
    }

    @Override
    public void cashVideoListFail(int code, String msg) {
        showPlayFail(getStringById(R.string.network_error));
    }

    /**
     * 当前云存储服务状态，1：开通；2：未开通 3：已过期
     *
     * @param data
     */
    @Override
    public void getStorageSuccess(ServiceListResp.DeviceListBean data) {
        int status = data.getStatus();
        gotoCloudPlaybackActivity(status);
    }

    //进入云回放
    private void gotoCloudPlaybackActivity(int status) {
        pausedVideo(true);
        SunmiDevice device = new SunmiDevice();
        device.setId(videoList.get(playIndex).getDeviceId());
        device.setDeviceid(videoList.get(playIndex).getDeviceSn());
        device.setName(videoList.get(playIndex).getDeviceName());
        device.setModel("SS1");
        long cashVideoStartTime = videoList.get(playIndex).getPurchaseTime();
        CloudPlaybackActivity_.intent(context)
                .device(device)
                .cloudStorageServiceStatus(status)
                .currentTime(cashVideoStartTime)
                .start().withAnimation(R.anim.slide_in_right, 0);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.cashVideoPlayPosition};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (args == null) {
            return;
        }
        if (id == CommonNotifications.cashVideoPlayPosition) {
            playIndex = (int) args[0];
            LogCat.e(TAG, "1111 didReceivedNotification playIndex=" + playIndex);
            initCashVideoPlay();
        }
    }

    /**
     * 倒计时函数
     */
    private class CashCountDownTimer extends CountDownTimer {
        CashCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            showPlayFail(getStringById(R.string.network_error));
        }
    }
}
