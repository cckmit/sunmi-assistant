package com.sunmi.ipc.cash;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.adapter.CashAdapter;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.InputDialog;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author yangShiJie
 * @date 2019-12-02
 */
@EActivity(resName = "cash_activity_play")
@SuppressLint("ClickableViewAccessibility")
public class CashPlayActivity extends BaseActivity implements
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

    private CashAdapter adapter;
    private List<CashOrderResp.ProductListBean> list = new ArrayList<>();
    private CashVideoPopupWindow popupWindow;
    private float posX, posY, curPosX;
    private VolumeHelper volumeHelper = null;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 是否暂停，是否静音，是否初始化了截屏
     */
    private boolean isPaused, isOpenVolume, isInitTakeScreenShot;
    /**
     * 是否自动播放
     */
    private boolean isPlayLoop;
    /**
     * 视频列表
     */
    private List<VideoListResp.VideoBean> videoList = new ArrayList<>();
    /**
     * 播放视频的index
     */
    private int playIndex;
    /**
     * 滑屏
     */
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
                    break;
                case MotionEvent.ACTION_UP:
                    if (posY > CommonHelper.getScreenWidth(context) - context.getResources().getDimension(R.dimen.dp_60)) {
                        return false;
                    }
                    if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
//                        shortTip("向左滑动");
                        if (playIndex > 0) {
                            playIndex--;
                            initCashVideoPlay();
                        } else {
                            shortTip("最左视频了");
                        }
                    } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > MOVE_SCREEN_POSITION)) {
//                        shortTip("向右滑动");
                        if (playIndex < videoList.size()) {
                            playIndex++;
                            initCashVideoPlay();
                        } else {
                            shortTip("最右视频了");
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    /**
     * 消息处理
     */
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
        initVolume();
        initScreenWidthHeight();
        titleBar.getAppTitle().setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_drop_down_black), null);
        titleBar.getAppTitle().setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.dp_5));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        titleBar.getAppTitle().setOnClickListener(v -> setTitleView());
        new Handler().postDelayed(() -> ivpCash.setVisibility(View.VISIBLE), 800);
        showLoading();

        goodsList();
        testCashPlay();
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

    private void initVolume() {
        volumeHelper = new VolumeHelper(this);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
    }

    private void setVideoListener() {
        ivpCash.setOnTouchListener(cashTouchListener);
        sbBar.setOnSeekBarChangeListener(this);
        ivpCash.setOnPreparedListener(this);
        ivpCash.setOnCompletionListener(this);
        ivpCash.setOnErrorListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        volumeHelper.unregisterVolumeReceiver();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 播放-暂停
     */
    @Click(resName = "ib_play")
    void onPlayClick() {
        if (ivpCash == null || sbBar.getProgress() >= ivpCash.getDuration()) {
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
        LogCat.e(TAG, "1111 isPlayLoop=" + isPlayLoop);

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
        ivPlayType.setImageResource(isPlayLoop ? R.mipmap.ic_single : R.mipmap.ic_loop);
    }

    /**
     * 云端播放
     */
    @Click(resName = "iv_cloud")
    void cloudPlayClick() {
        //TODO
    }

    /**
     * 标记
     */
    @Click(resName = "tv_tag")
    void tagClick() {
        new InputDialog.Builder(this)
                .setTitle("请输入异常类型")
                .setHint("如：现金交易未入钱箱等")
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, (dialog, input) -> {
                    if (TextUtils.isEmpty(input)) {
                        shortTip("请输入异常类型");
                        return;
                    }
                    if (input.length() > 30) {
                        shortTip(R.string.ipc_face_name_length_tip);
                        return;
                    }
                    dialog.dismiss();
                }).create().show();
    }

    private void setTitleView() {
        titleBar.getAppTitle().setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_drop_up_black), null);
        List<String> mList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mList.add("TAG" + i);
        }
        popupWindow = new CashVideoPopupWindow(this, titleBar, mList, titleBar.getAppTitle());
    }

    private void goodsList() {
        CashOrderResp resp = new CashOrderResp();
        resp.setOrderNo("12345678");
        resp.setAmount(10.66);
        resp.setTotalQuantity(20);
        resp.setPurchaseType("支付宝");
        CashOrderResp.ProductListBean bean;
        for (int i = 0; i < 4; i++) {
            bean = new CashOrderResp.ProductListBean();
            bean.setName("Apple");
            bean.setQuantity(i);
            list.add(bean);
        }
        resp.setProductList(list);
        tvAmount.setText(String.format("¥%s", resp.getAmount()));
        tvOrderNo.setText(resp.getOrderNo());
        tvTotalCommodity.setText(String.format(Locale.getDefault(), "%d", resp.getTotalQuantity()));
        tvTradeType.setText(resp.getPurchaseType());
        adapter = new CashAdapter(context, resp.getProductList());
        recyclerView.setAdapter(adapter);
    }

    private void testCashPlay() {
        IpcCloudApi.getInstance().getVideoList(2261, 1575426500, 1575426510, new RetrofitCallback<VideoListResp>() {
            @Override
            public void onSuccess(int code, String msg, VideoListResp data) {
                videoList = data.getVideo_list();
//                List<String> urlList = new ArrayList<>();
//                for (VideoListResp.VideoBean bean : videoList) {
//                    urlList.add(bean.getUrl());
//                    LogCat.e(TAG, "1111 url= " + bean.getUrl());
//                }
//                cloudPlay(urlList);
                initCashVideoPlay();
            }

            @Override
            public void onFail(int code, String msg, VideoListResp data) {

            }
        });
    }

    /**
     * 播放云端回放
     */
    private void cloudPlay(List<String> urlList) {
        ivpCash.setUrlQueue(urlList);
        try {
            ivpCash.startPlay();
        } catch (Exception e) {
            shortTip(R.string.tip_play_fail);
            e.printStackTrace();
        }
    }

    /**
     * 初始化播放
     */
    private void initCashVideoPlay() {
        showLoading();
        if (videoList != null) {
            if (playIndex == videoList.size()) {
                shortTip("播放完毕");
                return;
            }
            //自动下一段播放
            if (isPlayLoop) {
                playIndex++;
            }
            ivpCash.release();
            sbBar.setProgress(0);
            new Handler().postDelayed(() -> {
                ivpCash.load(videoList.get(playIndex).getUrl());
                setVideoListener();
                tvCurrentPlayTime.setText(ivpCash.generateTime(0));
            }, 200);
        }
    }

    private void startCashPreparedPlay() {
        if (ivpCash != null) {
            ibPlay.setBackgroundResource(R.mipmap.pause_normal);
            isPaused = false;
            hideLoading();
            ivpCash.startVideo();
            //设置seekBar的最大限度值，当前视频的总时长（毫秒）
            long duration = ivpCash.getDuration();
            //不足一秒补一秒
            if (duration % 1000 > 0) {
                duration = duration + (1000 - duration % 1000);
            }
            sbBar.setMax((int) duration);
            //视频总时长
            tvCountPlayTime.setText(Objects.requireNonNull(ivpCash).generateTime(duration));
            //发送当前播放时间点通知
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, DELAY_MILLIS);
        }
    }

    @UiThread
    public void showLoading() {
        pBarLoading.setVisibility(View.VISIBLE);
        tvPlayFail.setVisibility(View.GONE);
    }

    @UiThread
    public void hideLoading() {
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
        tvPlayFail.setVisibility(View.VISIBLE);
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        try {
            if (ivpCash != null) {
                ivpCash.setVisibility(View.GONE);
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
}
