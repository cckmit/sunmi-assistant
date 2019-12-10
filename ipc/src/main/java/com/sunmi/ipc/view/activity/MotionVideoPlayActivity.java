package com.sunmi.ipc.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.IVideoPlayer;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.log.LogCat;
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

    @ViewById(resName = "content")
    ConstraintLayout clContent;
    @ViewById(resName = "title_bar")
    TitleBarView tbTitle;
    @ViewById(resName = "player_video")
    IVideoPlayer player;

    @ViewById(resName = "iv_pause")
    ImageButton ibPause;
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
    int deviceId;
    @Extra
    String deviceModel;
    @Extra
    MotionVideo motionVideo;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private VolumeHelper mVolumeHelper;
    private SimpleArrayAdapter<MotionVideo> mAdapter;
    private SparseArray<String> mSourceName = new SparseArray<>(3);

    private boolean isPaused;

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
        tbTitle.getRightText().setOnClickListener(v -> {
            // TODO: 跳设置
        });
    }

    private void initList() {
        List<MotionVideo> list = new ArrayList<>();
        list.add(motionVideo);
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
        String videoRatio = DeviceTypeUtils.getInstance().isSS1(deviceModel) ? "1:1" : "16:9";
        set.setDimensionRatio(player.getId(), videoRatio);
        set.applyTo(clContent);
    }

    private void initPlayer() {
        player.setVideoPlayListener(this);
        startPlay(motionVideo.getCdnAddress());
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

    private void updateVolumeIcon(boolean isMute) {
        ivVolume.setImageResource(isMute ? R.mipmap.ic_muse : R.mipmap.ic_volume);
    }

    private void startPlay(String url) {
        showLoading();
        player.load(url);
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
    @Click(resName = "iv_pause")
    void pausePlayClick() {
        if (isFastClick(CLICK_SHAKE_THRESHOLD)) {
            return;
        }
        ibPause.setBackgroundResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
        isPaused = !isPaused;
        if (isPaused) {
            player.pause();
        } else {
            player.play();
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

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onVolumeChanged(int volume) {
        updateVolumeIcon(volume <= 0);
    }

    @Override
    public void onStartPlay() {
        LogCat.d("yinhui", "onStartPlay");
    }

    @Override
    public void onPlayComplete() {
        LogCat.d("yinhui", "onPlayComplete");
    }

    @Override
    public void onPlayFail() {
        LogCat.d("yinhui", "onPlayFail");
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
