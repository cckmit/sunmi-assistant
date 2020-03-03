package com.sunmi.ipc.cash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
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
import com.sunmi.ipc.cash.adapter.CashTagAdapter;
import com.sunmi.ipc.cash.model.CashBox;
import com.sunmi.ipc.cash.model.CashTagFilter;
import com.sunmi.ipc.cash.model.CashVideo;
import com.sunmi.ipc.cash.view.CashBoxView;
import com.sunmi.ipc.cash.view.CashProgressMark;
import com.sunmi.ipc.cash.view.CashVideoPopupWindow;
import com.sunmi.ipc.cash.view.OpenLossPreventServiceDialog;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.CashVideoContract;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.presenter.CashVideoPresenter;
import com.sunmi.ipc.view.activity.CloudPlaybackActivity_;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.ServiceResp;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.ConfigManager;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.ImageUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.BottomDialog;
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
    private static final int CASH_TAG_MAX_LENGTH = 36;
    /**
     * -1点击下拉选择  0 没有手势滑动  1 左滑  2 右滑
     */
    private static final int PLAY_TYPE_DROP_SELECT = -1;
    private static final int PLAY_TYPE_NORMAL = 0;
    private static final int PLAY_TYPE_LEFT_FLING = 1;
    private static final int PLAY_TYPE_RIGHT_FLING = 2;
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
    @ViewById(resName = "cash_box_overlay")
    CashBoxView cashBoxOverlay;
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
    @ViewById(resName = "sb_bar_mark")
    CashProgressMark sbMark;
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;
    @ViewById(resName = "iv_play_type")
    ImageView ivPlayType;
    @ViewById(resName = "tv_screenshot_tip")
    TextView tvScreenshotTip;
    @ViewById(resName = "iv_tag")
    ImageView ivTag;
    @ViewById(resName = "iv_video_change")
    ImageView ivVideoChange;
    @ViewById(resName = "tv_empty")
    TextView tvEmpty;
    @ViewById(resName = "iv_screen_play_pause")
    ImageView ivScreenPlayPause;

    @ViewById(resName = "tv_abnormal_tip")
    TextView tvAbnormalTip;

    /**
     * ipc名称 ，视频列表 ，是否一天快放,设备id, 一天快放的开始结束时间 ,是否有更多列表数据（一天快放或点击item进入）
     */
    @Extra
    HashMap<Integer, CashServiceInfo> serviceInfoMap;
    @Extra
    ArrayList<CashVideo> videoList = new ArrayList<>();
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
    @Extra
    boolean isAbnormalBehavior;

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
     * 当前播放视频的index和Model对象
     */
    private int playIndex;
    /**
     * 点击item页码
     */
    private int mWholeDayPlayPageNum;
    private CashAdapter adapter;
    private CashTagAdapter mTagAdapter;
    private Dialog mTagDialog;
    private Dialog mLossPreventDialog;
    private CashTagFilter mSelectedTag;
    /**
     * 视频订单详情
     */
    private List<CashOrderResp.ProductListBean> productList = new ArrayList<>();
    private CashVideoPopupWindow popupWindow;
    private VolumeHelper volumeHelper = null;
    private FFmpegMediaMetadataRetriever retriever;
    private GestureDetector gestureDetector = null;
    private View.OnTouchListener cashTouchListener = (v, event) -> {
        gestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ivVideoChange.setVisibility(View.GONE);
        }
        return false;
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

    private int playCashVideoStatus;
    private Bitmap bitmap;

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
        playType(true);
        if (isWholeDayVideoPlay) {
            //初始化一天快放
            hasMore = true;
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
            bundle.putParcelableArrayList("videoList", videoList);
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
        mHandler.postDelayed(() -> ivpCash.setVisibility(View.VISIBLE), 800);
        ivpCash.setOnTouchListener(cashTouchListener);
        //手势
        gestureDetector = new GestureDetector(this, new GestureScaleListener());
        ivpCash.setLongClickable(true);
    }

    /**
     * 初始化播放方式
     *
     * @param isLoop true 轮播 false 单循
     */
    private void playType(boolean isLoop) {
        playCashVideoStatus = PLAY_TYPE_DROP_SELECT;
        isPlayLoop = isLoop;
        ivPlayType.setImageResource(isPlayLoop ? R.mipmap.ic_loop : R.mipmap.ic_single);
    }

    private void initVolume() {
        volumeHelper = new VolumeHelper(this);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
        if (volumeHelper.isMute()) {
            isOpenVolume = true;
            ivVolume.setImageResource(R.mipmap.ic_mute_enable);
        }
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
        popupWindow = new CashVideoPopupWindow(CashPlayActivity.this, titleBar, playIndex,
                videoList, serviceInfoMap, titleBar.getAppTitle(), isAbnormalBehavior);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        pausedVideo(false);
        if (bitmap != null) {
            ivScreenPlayPause.setVisibility(View.VISIBLE);
            ivScreenPlayPause.setImageBitmap(bitmap);
        } else {
            ivScreenPlayPause.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausedVideo(true);
        if (ivpCash.getCurrentPosition() > 0 && retriever != null) {
            bitmap = retriever.getFrameAtTime(ivpCash.getCurrentPosition() * 1000,
                    FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
        }
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
        if (bitmap != null) {
            bitmap.recycle();
        }
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
                mPresenter.getCashVideoList(deviceId, -1, startTime, endTime, ++mWholeDayPlayPageNum, 10);
            } else if (isAbnormalBehavior) {
                // 行为异常视频拉取更多
                mPresenter.getAbnormalBehaviorList(deviceId, videoType, startTime, endTime, ++pageNum, 10);
            } else {
                //item点击进入是否有更多
                mPresenter.getCashVideoList(deviceId, videoType, startTime, endTime, ++pageNum, 10);
            }
        } else {
            shortTip(R.string.tip_no_more_data);
        }
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
            retriever.setDataSource(getCurrent().getVideoUrl());
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
            mHandler.postDelayed(() -> {
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
        ivScreenPlayPause.setVisibility(View.GONE);
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
        checkRequestPermissions();
    }

    /**
     * 音量
     */
    @Click(resName = "iv_volume")
    void volumeClick() {
        ivVolume.setImageResource(isOpenVolume ? R.mipmap.ic_unmute_enable : R.mipmap.ic_mute_enable);
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
            ivVolume.setImageResource(R.mipmap.ic_mute_enable);
        } else {
            isOpenVolume = false;
            volumeHelper.unMute();
            ivVolume.setImageResource(R.mipmap.ic_unmute_enable);
        }
    }

    /**
     * 播放方式
     */
    @Click(resName = "iv_play_type")
    void playTypeClick() {
        isPlayLoop = !isPlayLoop;
        if (isPlayLoop) {
            ivPlayType.setImageResource(R.mipmap.ic_loop);
            shortTip(R.string.cash_auth_paly_loop);
        } else {
            ivPlayType.setImageResource(R.mipmap.ic_single);
            shortTip(R.string.cash_paly_single);
        }
    }

    /**
     * 云端播放
     */
    @Click(resName = "iv_cloud")
    void cloudPlayClick() {
        if (pBarLoading.isShown()) {
            return;
        }
        mPresenter.getStorageList(getCurrent().getDeviceSn());
    }

    /**
     * 标记 1:正常视频，2:异常视频
     */
    @Click(resName = "iv_tag")
    void tagClick() {
        if (isFastClick(500)) {
            return;
        }
        pausedVideo(true);
        if (mTagDialog == null) {
            mTagAdapter = new CashTagAdapter(this);
            View root = mTagAdapter.getRootView();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mTagDialog = new BottomDialog.Builder(this)
                    .setTitle(R.string.cash_tag_dialog_title)
                    .setContent(root, lp)
                    .setCancelButton(R.string.sm_cancel)
                    .setOkButton(R.string.str_confirm, (dialog, which) -> {
                        if (updateVideoTag()) {
                            dialog.dismiss();
                        }
                    }, false)
                    .create();
        }
        if (mSelectedTag != null) {
            mTagAdapter.setSelected(mSelectedTag.getId());
        } else {
            CashVideo current = getCurrent();
            if (current.getVideoType() == IpcConstants.CASH_VIDEO_NORMAL) {
                // 正常视频
                mTagAdapter.setSelected(CashTagFilter.TAG_ID_NORMAL);
            } else {
                // 异常视频
                int[] tags = current.getVideoTag();
                // noinspection StatementWithEmptyBody
                if (tags == null || tags.length <= 0) {
                    // FIXME: 其他异常如何在选择异常对话框中展示，目前处理方法为不默认选中任何一个选项
                } else if (tags[0] == IpcConstants.CASH_VIDEO_TAG_CUSTOM) {
                    mTagAdapter.setCustom(current.getDescription());
                } else {
                    mTagAdapter.setSelected(tags[0]);
                }
            }
        }
        mTagDialog.show();
    }

    private boolean updateVideoTag() {
        CashTagFilter selected = mTagAdapter.getSelected();
        if (selected == null) {
            return false;
        }
        if (selected.getId() == CashTagFilter.TAG_ID_CUSTOM) {
            String desc = selected.getDesc();
            desc = desc == null ? null : desc.trim();
            if (TextUtils.isEmpty(desc)) {
                shortTip(R.string.ipc_cash_tag_empty_tip);
                return false;
            }
        }
        if (mSelectedTag != null && mSelectedTag.equals(selected)) {
            return true;
        }
        showDarkLoading();
        mPresenter.updateTag(getCurrent().getVideoId(), isAbnormalBehavior ? 1 : 2, selected);
        return true;
    }

    /**
     * 初始化播放
     */
    private void initCashVideoPlay() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            showPlayFail(getStringById(R.string.network_error));
            return;
        }
        if (videoList == null) {
            return;
        }
        ivScreenPlayPause.setVisibility(View.GONE);
        if (playCashVideoStatus != PLAY_TYPE_DROP_SELECT) {
            if (isPlayLoop && playIndex >= videoList.size() - 1 &&
                    playCashVideoStatus != PLAY_TYPE_LEFT_FLING) {
                if (hasMore) {
                    loadMoreVideoList();
                } else {
                    shortTip(R.string.cash_play_complete);
                }
                return;
            }
            showLoading();
            if (playCashVideoStatus == PLAY_TYPE_NORMAL) {
                //无手势滑动
                if (isPlayLoop) {
                    playIndex++;
                }
            } else if (playCashVideoStatus == PLAY_TYPE_LEFT_FLING) {
                //左滑
                playIndex--;
            } else if (playCashVideoStatus == PLAY_TYPE_RIGHT_FLING) {
                //右滑
                playIndex++;
            }
        }
        //恢复初始化播放状态
        playCashVideoStatus = PLAY_TYPE_NORMAL;
        CashVideo current = getCurrent();
        boolean isAbnormal = current.getVideoType() != IpcConstants.CASH_VIDEO_NORMAL;
        int[] videoTag = current.getVideoTag();
        if (isAbnormal && current.getUserModified() == 0) {
            mPresenter.getAbnormalEvent(current.getEventId(), current.getStartTime());
            if (CashTagManager.get(this).getTag(videoTag).getTag() != CashTagManager.TAG_ID_ORDER_MISMATCH) {
                // 只有飞单有风险率。换言之，如果不是飞单，则直接展示tip；否则拉取AI数据后展示。
                tvAbnormalTip.setText(CashTagManager.get(this).getTagName(videoTag, current.getDescription()));
                tvAbnormalTip.setVisibility(View.VISIBLE);
            }
        } else {
            cashBoxOverlay.setVisibility(View.GONE);
            sbMark.setVisibility(View.GONE);
            tvAbnormalTip.setText(CashTagManager.get(this).getTagName(videoTag, current.getDescription()));
            tvAbnormalTip.setVisibility(isAbnormal ? View.VISIBLE : View.GONE);
        }
        //查询当前视频订单信息
        if (!isAbnormalBehavior) {
            mPresenter.getOrderInfo(getCurrent().getOrderNo());
        }
        //更新title
        if (getCurrentService() != null && getCurrentService().isHasCashLossPrevention()) {
            titleBar.setAppTitle(R.string.str_cash_loss_prevent);
        } else {
            titleBar.setAppTitle(R.string.cash_video);
        }
        rlOrderInfo.setVisibility(isAbnormalBehavior ? View.GONE : View.VISIBLE);
        ivTag.setSelected(isAbnormal);
        sbBar.setProgress(0);
        ivpCash.release();
        mHandler.postDelayed(() -> {
            ivpCash.load(current.getVideoUrl());
            setVideoListener();
            tvCurrentPlayTime.setText(ivpCash.generateTime(0));
        }, 200);

    }

    private void startCashPreparedPlay() {
        if (ivpCash != null) {
            hideLoading();
            isPaused = false;
            ibPlay.setBackgroundResource(R.mipmap.pause_normal);
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
            sbMark.setDuration((int) duration);
            //视频总时长
            tvCountPlayTime.setText(String.format("/ %s", Objects.requireNonNull(ivpCash).generateTime(duration)));
            //发送当前播放时间点通知
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    @UiThread
    public void showLoading() {
        startCountDownTimer();
        pBarLoading.setVisibility(View.VISIBLE);
        llPlayFail.setVisibility(View.GONE);
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
            cashBoxOverlay.setCurrent(progress);
            tvCurrentPlayTime.setText(ivpCash.generateTime(generateTime));
        }
    }

    private CashVideo getCurrent() {
        return videoList.get(playIndex);
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
        ivScreenPlayPause.setVisibility(View.GONE);
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
     * return true不执行onCompletion  false执行onCompletion
     */
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        showPlayFail(getStringById(R.string.network_error));
        return true;
    }

    /**
     * 开始播放
     *
     * @param iMediaPlayer iMediaPlayer
     */
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        startCashPreparedPlay();
    }

    @Override
    public void updateTagSuccess(CashTagFilter selected) {
        hideLoadingDialog();
        mSelectedTag = selected;
        // 更新VideoList
        CashVideo video = getCurrent();
        boolean isAbnormal = selected.getId() != CashTagFilter.TAG_ID_NORMAL;
        ivTag.setSelected(isAbnormal);
        if (isAbnormal) {
            video.setVideoType(IpcConstants.CASH_VIDEO_ABNORMAL);
            video.setVideoTag(new int[]{selected.getId()});
            if (selected.getId() == CashTagFilter.TAG_ID_CUSTOM) {
                tvAbnormalTip.setText(selected.getDesc());
                video.setDescription(selected.getDesc());
            } else {
                tvAbnormalTip.setText(selected.getName());
            }
            shortTip(R.string.cash_tag_success);
            tvAbnormalTip.setVisibility(View.VISIBLE);
        } else {
            video.setVideoType(IpcConstants.CASH_VIDEO_NORMAL);
            shortTip(R.string.cash_tag_cancel_success);
            tvAbnormalTip.setVisibility(View.GONE);
        }
        // 清除AI加框信息
        video.setUserModified(1);
        cashBoxOverlay.setVisibility(View.GONE);
        sbMark.setVisibility(View.GONE);
        // 如果没有开通收银防损，那么弹窗推广
        CashServiceInfo service = serviceInfoMap.get(video.getDeviceId());
        if (service != null && !service.isHasCashLossPrevention() && ConfigManager.get().getCashSecurityEnable()) {
            if (mLossPreventDialog == null) {
                mLossPreventDialog = new OpenLossPreventServiceDialog.Builder(this)
                        .setListener((dialog, which) -> Router.withApi(SunmiServiceApi.class)
                                .goToWebViewCloud(context, CommonConstants.H5_CASH_PREVENT_LOSS,
                                        WebViewParamsUtils.getCashPreventLossParams(service.getDeviceSn(),0)
                                        , new BiCallback<Intent>() {
                                            @Override
                                            public void onSuccess(@NonNull RouterResult result, @NonNull Intent intent) {
                                                mPresenter.onServiceSubscribeResult(intent);
                                            }

                                            @Override
                                            public void onCancel(@Nullable RouterRequest originalRequest) {
                                            }

                                            @Override
                                            public void onError(@NonNull RouterErrorResult errorResult) {
                                            }
                                        }))
                        .create();
            }
            mLossPreventDialog.show();
        }
    }

    @Override
    public void updateTagFail(int code, String msg, CashTagFilter tag) {
        hideLoadingDialog();
        boolean isAbnormal = tag.getId() != CashTagFilter.TAG_ID_NORMAL;
        if (isAbnormal) {
            shortTip(R.string.cash_tag_fail);
        } else {
            shortTip(R.string.cash_tag_cancel_fail);
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
    public void cashVideoListSuccess(List<CashVideo> list) {
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
    public void getStorageSuccess(ServiceResp.Info data) {
        int status = data.getStatus();
        gotoCloudPlaybackActivity(status);
    }

    @Override
    public void getAbnormalEventSuccess(float riskScore, List<CashBox> boxes) {
        if (riskScore < 0) {
            return;
        }
        cashBoxOverlay.setData(boxes);
        sbMark.setData(boxes);
        cashBoxOverlay.setVisibility(View.VISIBLE);
        sbMark.setVisibility(View.VISIBLE);
        int[] videoTag = getCurrent().getVideoTag();
        if (CashTagManager.get(this).getTag(videoTag).getTag() == CashTagManager.TAG_ID_ORDER_MISMATCH) {
            // 只有飞单有风险率。
            String tip = getString(R.string.cash_abnormal_tip, CashTagManager.get(this).getTag(videoTag).getName(),
                    (int) riskScore);
            tvAbnormalTip.setText(tip);
            tvAbnormalTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getAbnormalEventFail(int code, String msg) {
        if (code != RpcErrorCode.ERR_CASH_EVENT_NOT_EXIST) {
            shortTip(R.string.toast_network_error);
        }
        CashVideo current = getCurrent();
        tvAbnormalTip.setText(CashTagManager.get(this).getTagName(current.getVideoTag(), current.getDescription()));
        tvAbnormalTip.setVisibility(View.VISIBLE);
        cashBoxOverlay.setVisibility(View.GONE);
        sbMark.setVisibility(View.GONE);
    }

    //进入云回放
    private void gotoCloudPlaybackActivity(int status) {
        pausedVideo(true);
        CashServiceInfo service = getCurrentService();
        CashVideo video = getCurrent();

        SunmiDevice device = new SunmiDevice();
        device.setId(video.getDeviceId());
        device.setDeviceid(video.getDeviceSn());
        device.setName(service == null ? "" : service.getDeviceName());
        device.setModel("SS1");
        long cashVideoStartTime = video.getPurchaseTime();
        CloudPlaybackActivity_.intent(context)
                .device(device)
                .cloudStorageServiceStatus(status)
                .currentTime(cashVideoStartTime)
                .start().withAnimation(R.anim.slide_in_right, 0);
    }

    @Nullable
    private CashServiceInfo getCurrentService() {
        return serviceInfoMap.get(getCurrent().getDeviceId());
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.cashVideoPlayPosition, CommonNotifications.cashPreventSubscribe};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (args == null) {
            return;
        }
        if (id == CommonNotifications.cashVideoPlayPosition) {
            playIndex = (int) args[0];
            playCashVideoStatus = PLAY_TYPE_DROP_SELECT;
            initCashVideoPlay();
        } else if (id == CommonNotifications.cashPreventSubscribe) {
            if (args.length <= 0 || !(args[0] instanceof Set)) {
                return;
            }
            @SuppressWarnings("unchecked")
            Set<String> snSet = (Set<String>) args[0];
            for (Map.Entry<Integer, CashServiceInfo> entry : serviceInfoMap.entrySet()) {
                CashServiceInfo info = entry.getValue();
                if (snSet.contains(info.getDeviceSn())) {
                    info.setHasCashLossPrevention(true);
                }
            }
        }
    }

    /**
     * 手势左右滑动
     */
    class GestureScaleListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1.getY() > CommonHelper.getScreenWidth(context) - context.getResources().getDimension(R.dimen.dp_100)) {
                return false;
            }
            ivVideoChange.setVisibility(View.VISIBLE);
            if (e1.getX() - e2.getX() > MOVE_SCREEN_POSITION) {
                ivVideoChange.setImageResource(R.mipmap.ic_cash_next);
            } else if (e2.getX() - e1.getX() > MOVE_SCREEN_POSITION) {
                ivVideoChange.setImageResource(R.mipmap.ic_cash_previous);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            ivVideoChange.setVisibility(View.GONE);
            if (e1.getY() > CommonHelper.getScreenWidth(context) - context.getResources().getDimension(R.dimen.dp_100)) {
                return false;
            }
            if (e1.getX() - e2.getX() > MOVE_SCREEN_POSITION) {
                playCashVideoStatus = PLAY_TYPE_RIGHT_FLING;
                if (playIndex < videoList.size() - 1) {
                    initCashVideoPlay();
                } else {
                    loadMoreVideoList();
                }
                return true;
            } else if (e2.getX() - e1.getX() > MOVE_SCREEN_POSITION) {
                if (playIndex > 0) {
                    playCashVideoStatus = PLAY_TYPE_LEFT_FLING;
                    initCashVideoPlay();
                } else {
                    shortTip(R.string.cash_left_first_video);
                }
                return true;
            }
            return false;
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
