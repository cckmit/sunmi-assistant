package com.sunmi.ipc.view.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.model.MotionVideo;
import com.sunmi.ipc.view.activity.setting.MDSettingActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.SimpleArrayAdapter;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-12-10
 */
@EActivity(resName = "activity_motion_video_play")
public class MotionVideoPlayActivity extends BaseActivity implements
        IVideoPlayer.VideoPlayListener,
        VolumeHelper.VolumeChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private static final int CLICK_SHAKE_THRESHOLD = 1000;
    private static final int PLAY_DELAY = 200;
    private static final int PLAY_UPDATE_INTERVAL = 10;

    @ViewById(resName = "content")
    ConstraintLayout clContent;
    @ViewById(resName = "title_bar")
    TitleBarView tbTitle;
    @ViewById(resName = "player_video")
    IVideoPlayer player;

    @ViewById(resName = "ib_play")
    ImageButton ibPause;
    @ViewById(resName = "sb_bar")
    SeekBar sbProgress;
    @ViewById(resName = "tv_current_play_time")
    TextView tvCurrentTime;
    @ViewById(resName = "tv_count_play_time")
    TextView tvTotalTime;
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;
    @ViewById(resName = "iv_full_screen")
    ImageView ivFullScreen;

    @ViewById(resName = "tv_screenshot_tip")
    TextView tvScreenshotTip;

    @ViewById(resName = "pb_loading")
    ProgressBar pbLoading;
    @ViewById(resName = "tv_play_fail")
    TextView tvError;

    @ViewById(resName = "tv_date_tip")
    TextView tvDateTip;
    @ViewById(resName = "rv_list")
    RecyclerView rvList;

    @Extra
    SunmiDevice device;
    @Extra
    MotionVideo motionVideo;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mTask = new UpdateSeekBarTask();
    private VolumeHelper mVolumeHelper;
    private SimpleArrayAdapter<MotionVideo> mAdapter;
    private SparseArray<String> mSourceName = new SparseArray<>(3);

    private List<String> urls = new ArrayList<>();
    private boolean isPaused;
    private boolean isComplete;
    private boolean isDragging;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initResource();
        initTitle();
        initList();
        initVolume();
        initVideoRatio();
        initPlayer();
        showLoading();
    }

    private void initResource() {
        mSourceName.put(IpcConstants.MOTION_DETECTION_SOURCE_VIDEO,
                getString(R.string.motion_detection_source_video_name));
        mSourceName.put(IpcConstants.MOTION_DETECTION_SOURCE_SOUND,
                getString(R.string.motion_detection_source_sound_name));
        mSourceName.put(IpcConstants.MOTION_DETECTION_SOURCE_BOTH,
                getString(R.string.motion_detection_source_both_name));
    }

    private void initTitle() {
        tbTitle.getLeftLayout().setOnClickListener(v -> onBackPressed());
        tbTitle.getRightText().setOnClickListener(v ->
                MDSettingActivity_.intent(this).mDevice(device).start());
        tvDateTip.setText(DateTimeUtils.secondToDate(motionVideo.getDetectTime(), "yyyy-MM-dd"));
    }

    private void initList() {
        List<MotionVideo> list = new ArrayList<>();
        list.add(motionVideo);
        urls.clear();
        for (MotionVideo video : list) {
            urls.add(video.getCdnAddress());
        }
        mAdapter = new Adapter();
        mAdapter.setData(list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(mAdapter);
    }

    private void initVolume() {
        mVolumeHelper = new VolumeHelper(this);
        mVolumeHelper.setVolumeChangeListener(this);
        mVolumeHelper.registerVolumeReceiver();
        updateVolumeIcon(mVolumeHelper.get100CurrentVolume() <= 0);
    }

    private void initVideoRatio() {
        ConstraintSet set = new ConstraintSet();
        set.clone(clContent);
        String videoRatio = DeviceTypeUtils.getInstance().isSS1(device.getModel()) ? "1:1" : "16:9";
        set.setDimensionRatio(player.getId(), videoRatio);
        set.applyTo(clContent);
    }

    private void initPlayer() {
        sbProgress.setOnSeekBarChangeListener(this);
        player.setVideoPlayListener(this);
        player.setUrlQueue(urls);
        startPlay();
    }

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void hideLoading() {
        pbLoading.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void showError() {
        pbLoading.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        stopPlay();
    }

    private void switchOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            tbTitle.setVisibility(View.GONE);
            ivFullScreen.setVisibility(View.GONE);
            tvDateTip.setVisibility(View.GONE);
            rvList.setVisibility(View.GONE);
            clContent.setBackgroundColor(ContextCompat.getColor(this, R.color.c_black));
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            tbTitle.setVisibility(View.VISIBLE);
            ivFullScreen.setVisibility(View.VISIBLE);
            tvDateTip.setVisibility(View.VISIBLE);
            rvList.setVisibility(View.VISIBLE);
            clContent.setBackgroundColor(ContextCompat.getColor(this, R.color.c_white));
        }
    }

    private void updateVolumeIcon(boolean isMute) {
        ivVolume.setImageResource(isMute ? R.mipmap.ic_muse : R.mipmap.ic_volume);
    }

    private void updateSeekBar(int pos) {
        sbProgress.setProgress(pos);
        tvCurrentTime.setText(player.generateTime(pos));
    }

    private void startPlay() {
        showLoading();
        player.startPlay();
    }

    private void stopPlay() {
        try {
            if (player != null) {
                player.setVisibility(View.GONE);
                player.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isLandscape() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /**
     * 暂停
     */
    @Click(resName = "ib_play")
    void pausePlayClick() {
        if (isFastClick(CLICK_SHAKE_THRESHOLD)) {
            return;
        }
        isPaused = !isPaused;
        ibPause.setBackgroundResource(isPaused ? R.mipmap.play_normal : R.mipmap.pause_normal);
        if (!isPaused && isComplete) {
            startPlay();
            return;
        }
        if (isPaused) {
            player.pause();
        } else {
            player.play();
            mHandler.post(mTask);
        }
    }

    /**
     * 音量
     */
    @Click(resName = "iv_volume")
    void volumeClick() {
        if (mVolumeHelper.isMute()) {
            mVolumeHelper.unMute();
            updateVolumeIcon(false);
        } else {
            mVolumeHelper.mute();
            updateVolumeIcon(true);
        }
    }

    /**
     * 全屏
     */
    @Click(resName = "iv_full_screen")
    void fullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            tvCurrentTime.setText(player.generateTime(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDragging = false;
        isPaused = false;
        player.seekTo(seekBar.getProgress());
        if (isPaused && !isComplete) {
            ibPause.setBackgroundResource(R.mipmap.pause_normal);
            player.play();
        }
        mHandler.removeCallbacks(mTask);
        mHandler.post(mTask);
    }

    @Override
    public void onVolumeChanged(int volume) {
        updateVolumeIcon(volume <= 0);
    }

    @Override
    public void onStartPlay() {
        hideLoading();
        isPaused = false;
        isComplete = false;
        mHandler.post(mTask);
        long duration = player.getDuration();
        sbProgress.setMax((int) duration);
        tvTotalTime.setText(player.generateTime(duration));
        tvCurrentTime.setText(player.generateTime(0));
    }

    @Override
    public void onPlayComplete() {
        isPaused = true;
        isComplete = true;
        player.setUrlQueue(urls);
        tvCurrentTime.setText(player.generateTime(0));
        ibPause.setBackgroundResource(isPaused ? R.mipmap.play_normal : R.mipmap.pause_normal);
        sbProgress.setProgress(0);
    }

    @Override
    public void onPlayFail() {
        isPaused = true;
        showError();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switchOrientation(newConfig.orientation);
    }

    @Override
    public void onBackPressed() {
        if (isLandscape()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            stopPlay();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            updateVolumeIcon(mVolumeHelper.get100CurrentVolume() <= 0);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVolumeHelper.unregisterVolumeReceiver();
        stopPlay();
    }

    private class UpdateSeekBarTask implements Runnable {

        @Override
        public void run() {
            if (isPaused || isDragging) {
                return;
            }
            updateSeekBar((int) player.getCurrentPosition());
            mHandler.postDelayed(this, PLAY_UPDATE_INTERVAL);
        }
    }

    private class Adapter extends SimpleArrayAdapter<MotionVideo> {

        @Override
        public int getLayoutId() {
            return R.layout.item_motion_video_play;
        }

        @Override
        public void setupView(@NonNull BaseViewHolder<MotionVideo> holder, MotionVideo model, int position) {
            ImageView current = holder.getView(R.id.iv_item_current);
            TextView title = holder.getView(R.id.tv_item_title);
            TextView source = holder.getView(R.id.tv_item_source);
            title.setText(DateTimeUtils.secondToDate(model.getDetectTime(), "HH:mm:ss"));
            String type = mSourceName.get(model.getSource());
            source.setText(type == null ? "" : type);

            current.setVisibility(View.VISIBLE);
            title.setSelected(true);
        }

    }
}
