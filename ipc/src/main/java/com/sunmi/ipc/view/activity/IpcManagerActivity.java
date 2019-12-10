package com.sunmi.ipc.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.CashVideoOverviewActivity_;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.view.activity.MotionVideoListActivity_;
import com.sunmi.ipc.presenter.IpcManagerPresenter;
import com.sunmi.ipc.service.P2pService;
import com.sunmi.ipc.utils.IOTCClient;
import com.sunmi.ipc.utils.IpcUtils;
import com.sunmi.ipc.view.activity.setting.IpcSettingActivity_;
import com.xiaojinzi.component.impl.Router;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
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
        implements IpcManagerContract.View, SurfaceHolder.Callback,
        View.OnClickListener, VolumeHelper.VolumeChangeListener, P2pService.OnPlayStatusChangedListener {

    private final static int REQ_SDCARD_PLAYBACK = 10;

    private final static int PLAY_FAIL_OFFLINE = 1;
    private final static int PLAY_FAIL_NET_ERROR = 2;

    @ViewById(resName = "rl_screen")
    LinearLayout rlScreen;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "rl_top")
    RelativeLayout rlTopBar;
    @ViewById(resName = "rl_bottom")
    ConstraintLayout rlBottomBar;
    @ViewById(resName = "iv_record")
    ImageView ivRecord;//录制
    @ViewById(resName = "tv_living")
    TextView tvLiving;
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
    @ViewById(resName = "ll_play_fail")
    LinearLayout llPlayFail;
    @ViewById(resName = "tv_play_fail")
    TextView tvPlayFail;
    @ViewById(resName = "btn_retry")
    Button btnRetry;
    @ViewById(resName = "tv_time_scroll")
    TextView tvTimeScroll;
    @ViewById(resName = "rl_video")
    RelativeLayout rlVideo;
    @ViewById(resName = "ll_loading")
    LinearLayout llLoading;
    @ViewById(resName = "iv_full_screen_live")
    ImageView ivFullScreen;
    @ViewById(resName = "ll_portrait_controller_bar")
    LinearLayout llPortraitBar;
    @ViewById(resName = "iv_cloud_playback_portrait")
    ImageView ivCloudPlayback;
    @ViewById(resName = "iv_sdcard_playback")
    ImageView ivSdcardPlayback;//sd回放
    @ViewById(resName = "rv_manager")
    SmRecyclerView rvManager;

    @Extra
    SunmiDevice device;

    private int screenW; //手机屏幕的宽
    private int qualityType = 0;//0-超清，1-高清
    private boolean isStartRecord;//是否开始录制
    private boolean isControlPanelShow = true;//是否点击屏幕

    private Handler handler = new Handler();
    private VolumeHelper volumeHelper = null;

    //竖屏切换高清
    private BottomPopMenu qualityPop;

    private CommonListAdapter adapter;
    private int cloudStorageServiceStatus;
    private List<IpcManageBean> list = new ArrayList<>();
    private IpcManageBean cloudStorageItem;
    private IpcManageBean cashVideoItem;
    private boolean cashVideoSubscribed = false;
    ArrayList<CashVideoServiceBean> serviceBeans = new ArrayList<>();

    P2pService p2pService;
    boolean isBind;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            P2pService.MyBinder myBinder = (P2pService.MyBinder) binder;
            p2pService = myBinder.getService();
            p2pPrepare();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    @AfterViews
    void init() {
        mPresenter = new IpcManagerPresenter();
        mPresenter.attachView(this);
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        titleBar.setAppTitle(device.getName());
        titleBar.getLeftLayout().setOnClickListener(this);
        titleBar.getRightTextView().setOnClickListener(this);
        rlBottomBar.setVisibility(View.VISIBLE);
        screenW = CommonHelper.getScreenWidth(context);

        llLoading.setOnTouchListener((v, event) -> true);
        llPlayFail.setOnTouchListener((v, event) -> true);
        if (isDeviceOffline()) {
            showPlayFail(PLAY_FAIL_OFFLINE);
            ivSdcardPlayback.setEnabled(false);
            hideControllerPanel();
        } else {
            showVideoLoading();
        }
        initSurfaceView();
        initManageList();
        if (isSS1()) {
            mPresenter.getStorageList(device.getDeviceid(), cloudStorageItem);
            mPresenter.getCashVideoService(device.getId());
            ivCloudPlayback.setVisibility(View.VISIBLE);
        }
        initVolume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

    private void startService() {
        bindService(new Intent(context, P2pService.class)
                .putExtra("uid", device.getUid()), conn, BIND_AUTO_CREATE);
    }

    private void stopService() {
        unbindService(conn);
    }

    private boolean isDeviceOffline() {
        return device.getStatus() == DeviceStatus.OFFLINE.ordinal();
    }

    private void initSurfaceView() {
        switchOrientation(Configuration.ORIENTATION_PORTRAIT);
        videoView.getHolder().addCallback(this);
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
        stopService();
        volumeHelper.unregisterVolumeReceiver();
        stopPlay();
        removeCallbacks();
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
        if (llVideoQuality.isShown()) {
            llVideoQuality.setVisibility(View.GONE);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Click(resName = "iv_full_screen_live")
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

    //静音
    @Click(resName = "iv_volume")
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
        if (isPortrait()) {
            switchQualityDialog();
        } else {
            llVideoQuality.setVisibility(llVideoQuality.isShown() ? View.GONE : View.VISIBLE);
            if (qualityType == 0) {
                tvFHDQuality.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
                tvHDQuality.setTextColor(ContextCompat.getColor(context, R.color.c_white));
            } else {
                tvFHDQuality.setTextColor(ContextCompat.getColor(context, R.color.c_white));
                tvHDQuality.setTextColor(ContextCompat.getColor(context, R.color.common_orange));
            }
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

    //云回放
    @Click(resName = "iv_cloud_playback_portrait")
    void cloudPlaybackClick() {
        if (isFastClick(1000)) {
            return;
        }
        pausePlay();
        CloudPlaybackActivity_.intent(context).device(device)
                .cloudStorageServiceStatus(cloudStorageServiceStatus)
                .start().withAnimation(R.anim.slide_in_right, 0);
    }

    //sd卡回放
    @Click(resName = "iv_sdcard_playback")
    void sdPlaybackClick() {
        if (isFastClick(1000)) {
            return;
        }
        SDCardPlayBackActivity_.intent(context).device(device)
                .cloudStorageServiceStatus(cloudStorageServiceStatus)
                .startForResult(REQ_SDCARD_PLAYBACK).withAnimation(R.anim.slide_in_right, 0);
    }


    @OnActivityResult(REQ_SDCARD_PLAYBACK)
    void onCreateResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            p2pService.init(videoView.getHolder().getSurface(), this);
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

    @Click(resName = "btn_retry")
    void retryClick() {
        isControlPanelShow = false;
        hidePlayFail();
        showVideoLoading();
        setPanelVisible(View.VISIBLE);
        initP2pLive();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isDeviceOffline()) {
            return;
        }
        if (p2pService != null) {
            resumePlay();
        }
    }

    void p2pPrepare() {
        if (isDeviceOffline()) {
            return;
        }
        p2pService.init(videoView.getHolder().getSurface(), this);
        initP2pLive();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPlayFail() {
        hideVideoLoading();
        hideControllerPanel();
        showPlayFail(PLAY_FAIL_NET_ERROR);
    }

    @Override
    public void startLiveSuccess() {
        hideVideoLoading();
    }

    @UiThread
    @Override
    public void changeQualitySuccess(int quality) {
        qualityType = quality;
        if (qualityType == 0) {
            tvQuality.setText(R.string.str_FHD);
            shortTip(R.string.tip_video_quality_fhd);
        } else if (qualityType == 1) {
            tvQuality.setText(R.string.str_HD);
            shortTip(R.string.tip_video_quality_hd);
        }
    }

    @UiThread
    @Override
    public void getStorageSuccess(IpcManageBean bean) {
        cloudStorageServiceStatus = bean.getStatus();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getCashVideoServiceSuccess(ArrayList<CashVideoServiceBean> devices, boolean alreadySubscribe) {
        hideLoadingDialog();
        serviceBeans = devices;
        if (!devices.isEmpty()) {
            cashVideoItem.setRightText(context.getString(R.string.str_setting_detail));
            adapter.notifyDataSetChanged();
        } else if (alreadySubscribe) {// 已经有其他摄像机开通了收银视频服务
            cashVideoSubscribed = true;
        }
    }

    @Override
    public void onVolumeChanged(int volume) {
        setVolumeViewImage(volume);
    }

    @Override
    public void onPlayStarted() {
        hidePlayFail();
        hideVideoLoading();
        setTvLivingVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayFinished() {
        setTvLivingVisibility(View.GONE);
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.ipcNameChanged, CommonNotifications.cloudStorageChange,
                CommonNotifications.cashVideoSubscribe};
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
            mPresenter.getStorageList(device.getDeviceid(), cloudStorageItem);
        } else if (id == CommonNotifications.cashVideoSubscribe) {
            mPresenter.getCashVideoService(device.getId());
        }
    }

    private void resumePlay() {
        if (llPlayFail != null && llPlayFail.isShown() || p2pService == null) {
            return;
        }
        setPanelVisible(View.VISIBLE);
        if (p2pService != null) {
            showVideoLoading();
            p2pService.startPlay();
        }
    }

    private void pausePlay() {
        if (p2pService != null) {
            p2pService.stopRunning();
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
            btnRetry.setVisibility(View.GONE);
            tvPlayFail.setText(R.string.tip_ipc_offline);
        } else if (PLAY_FAIL_NET_ERROR == type) {
            btnRetry.setVisibility(View.VISIBLE);
            tvPlayFail.setText(R.string.tip_network_fail_retry);
        }
        llPlayFail.setVisibility(View.VISIBLE);
        setTvLivingVisibility(View.VISIBLE);
    }

    @UiThread
    public void hidePlayFail() {
        llPlayFail.setVisibility(View.GONE);
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
            setPanelVisible(View.VISIBLE);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitViewVisible(View.VISIBLE);
            setPanelVisible(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
        setVideoParams(orientation);
    }

    private void setPortraitViewVisible(int visibility) {
        if (rvManager == null) {
            return;
        }
        titleBar.setVisibility(visibility);
        ivFullScreen.setVisibility(visibility);
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

        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = videoW;
        lp.height = videoH;
        videoView.setLayoutParams(lp);
    }

    private boolean isSS1() {
        return DeviceTypeUtils.getInstance().isSS1(device.getModel());
    }

    private void stopPlay() {
        if (p2pService != null) {
            p2pService.release();
            p2pService = null;
        }
    }

    //开始直播
    @Background
    void initP2pLive() {
        if (p2pService != null) {
            p2pService.initP2pLive();
        }
    }

    @UiThread
    void hideControllerPanel() {
        setPanelVisible(View.GONE);
        llVideoQuality.setVisibility(View.GONE);//画质
    }

    private void setPanelVisible(int visible) {
        if (rlTopBar != null && rlBottomBar != null) {
            rlTopBar.setVisibility(isPortrait() ? View.GONE : visible);
            rlBottomBar.setVisibility(visible);
        }
    }

    private IOTCClient getIOTCClient() {
        return p2pService == null ? null : p2pService.getIOTCClient();
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
        mPresenter.changeQuality(type, getIOTCClient());
    }

    /**
     * 初始化音量
     */
    private void initVolume() {
        volumeHelper = new VolumeHelper(context);
        volumeHelper.setVolumeChangeListener(this);
        volumeHelper.registerVolumeReceiver();
        int currentVolume100 = volumeHelper.get100CurrentVolume();
        setVolumeViewImage(currentVolume100);
    }

    private void setVolumeViewImage(int currentVolume100) {
        ivVolume.setImageResource(currentVolume100 == 0 ? R.mipmap.ic_muse : R.mipmap.ic_volume);
    }

    private void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }

    private void switchQualityDialog() {
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
        if (isSS1()) {
            cashVideoItem = new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_CASH, R.mipmap.ipc_manage_cashier, getString(R.string.cash_video),
                    getString(R.string.cash_video_item_content), getString(R.string.str_learn_more), true);
            list.add(cashVideoItem);
            cloudStorageItem = new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_CLOUD, R.mipmap.ipc_cloud_storage, context.getString(R.string.str_cloud_storage),
                    context.getString(R.string.str_setting_detail));
            cloudStorageItem.setEnabled(false);
            list.add(cloudStorageItem);
        }
        list.add(new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_DETECT, R.mipmap.ipc_manage_md, getString(R.string.str_motion_detection),
                getString(R.string.str_md_exception), getString(R.string.str_setting_detail), true));
        adapter = new CommonListAdapter<IpcManageBean>(context, R.layout.item_ipc_manager, list) {
            @Override
            public void convert(ViewHolder holder, IpcManageBean bean) {
                Button btnDetail = holder.getView(R.id.btn_detail);
                holder.setImageResource(R.id.iv_icon, bean.getLeftImageResId());
                holder.setText(R.id.tv_title, bean.getTitle());
                holder.setText(R.id.tv_summary, bean.getSummary());
                holder.setText(R.id.btn_detail, bean.getRightText());
                btnDetail.setEnabled(bean.isEnabled());
                if (bean.getTagImageResId() != -1) {
                    holder.setImageResource(R.id.iv_tag, bean.getTagImageResId());
                }
                btnDetail.setOnClickListener(v -> {
                    switch (bean.getType()) {
                        case IpcConstants.IPC_MANAGE_TYPE_CLOUD:
                            if (TextUtils.equals(bean.getRightText(), getString(R.string.str_setting_detail))) {
                                Router.withApi(SunmiServiceApi.class).goToServiceDetail(context,
                                        device.getDeviceid(), true, device.getName());
                            } else {
                                ArrayList<String> snList = new ArrayList<>();
                                snList.add(device.getDeviceid());
                                Router.withApi(SunmiServiceApi.class)
                                        .goToWebViewCloud(context, CommonConfig.CLOUD_STORAGE_URL, snList);
                            }
                            break;
                        case IpcConstants.IPC_MANAGE_TYPE_CASH:
                            if (!serviceBeans.isEmpty()) {// 跳转收银视频页面
                                CashVideoOverviewActivity_.intent(context).isSingleDevice(true)
                                        .serviceBeans(serviceBeans).start();
                            } else if (cashVideoSubscribed) {// 已经有其他摄像机开通了收银视频服务
                                shortTip(R.string.cash_video_other_device_already_subscribe_tip);
                            } else {//去开通
                                Router.withApi(SunmiServiceApi.class)
                                        .goToWebViewCash(context, CommonConfig.CASH_VIDEO_URL);
                            }
                            break;
                        case IpcConstants.IPC_MANAGE_TYPE_DETECT:
                            MotionVideoListActivity_.intent(context).deviceId(device.getId()).deviceModel(device.getModel()).start();
                            break;
                        default:
                            break;
                    }
                });
            }
        };
        rvManager.setAdapter(adapter);
    }

    @UiThread
    void setTvLivingVisibility(int visibility) {
        tvLiving.setVisibility(visibility);
    }

//    private void setPlayType(int type) {
//        playType = type;
//        videoView.setVisibility(type != 2 ? View.VISIBLE : View.GONE);
//        setTextViewClickable(tvQuality, type == 0);
//    }
//
//    private void setTextViewClickable(TextView textView, boolean clickable) {
//        textView.setClickable(clickable);
//        textView.setTextColor(clickable ? ContextCompat.getColor(context, R.color.c_white)
//                : ContextCompat.getColor(context, R.color.white_40a));
//    }
//
//    @UiThread
//    @Override
//    public void startPlaybackSuccess() {
//        setPlayType(1);
//        hideVideoLoading();
//    }

//    private void setIvPlayImage(boolean isPaused) {
//        ivPlay.setImageResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
//        ivPlayP.setImageResource(isPaused ? R.mipmap.pause_normal : R.mipmap.play_normal);
//    }

//    /**
//     *      * 切到设备回放
//     *
//     */
//    void switch2DevPlayback(long start) {
//        showVideoLoading();
//        mPresenter.startPlayback(getIOTCClient(), start);
//    }

    //    @ViewById(resName = "sBar_voice")
//    VerticalSeekBar sBarVoice;//音量控制
    //    @ViewById(resName = "iv_live")
//    ImageView ivLive;//直播
//    @ViewById(resName = "iv_play")
//    ImageView ivPlay;//开始播放
    //    @ViewById(resName = "scale_panel")
//    ZFTimeLine scalePanel;
    //    @ViewById(resName = "rl_portrait_video_controller")
//    RelativeLayout rlVideoController;
//    @ViewById(resName = "iv_play_portrait")
//    ImageView ivPlayP;//暂停
//    @ViewById(resName = "iv_volume_portrait")
//    ImageView ivVolumeP;//音量
//    @ViewById(resName = "tv_quality_portrait")
//    TextView tvQualityP;//画质

//    private final static long threeDaysSeconds = 3 * 24 * 60 * 60;//3天秒数
//    private boolean isVideoLess1Minute;//视频片段是否小于一分钟
//    private boolean isFirstScroll = true;//是否第一次滑动
//    private boolean isPaused;//回放是否暂停
//    //日历
//    private Calendar calendar;
//    //当前时间 ，三天前秒数
//    private long currentDateSeconds, threeDaysBeforeSeconds;
//    //刻度尺移动定时器
//    private ScheduledExecutorService executorService;
//    //滑动停止的时间戳
//    private long scrollTime;
//    //选择日历当前的时间的0点
//    private long selectedDate;
//    //是否为选择的日期
//    private boolean isSelectedDate;
//    private Drawable drawableLeft, drawableRight;
//    private List<VideoTimeSlotBean> listAp = new ArrayList<>();
//    private CountDownTimer timeLineScrollTimer;

//    @SuppressLint("ClickableViewAccessibility")
//    void initControllerPanel() {
//        openMove();
//        scalePanel.setListener(this);
//    }

//    void initData() {
    //当前天
//        calendar = Calendar.getInstance();
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        setCalendarText(day > 9 ? day + "" : "0" + day);
//        currentDateSeconds = System.currentTimeMillis() / 1000;
//        threeDaysBeforeSeconds = currentDateSeconds - threeDaysSeconds;
//    }

//    private void setCalendarText(String day) {
//        tvCalendarP.setText(day);
//    }

//    //开始，暂停
//    @Click(resName = {"iv_play", "iv_play_portrait"})
//    void pausePlayClick() {
//        if (isFastClick(1000) || playType == PLAY_TYPE_LIVE) {
//            return;
//        }
//        setIvPlayImage(isPaused);
//        isPaused = !isPaused;
//        if (playType == PLAY_TYPE_PLAYBACK_DEV) {
//            if (p2pService != null) {
//                p2pService.pausePlayback(isPaused);
//            }
//        }
//    }

//    //直播
//    @Click(resName = "iv_live")
//    void playApBackClick() {
//        switch2Live();
//    }

//    //显示日历
//    @Click(resName = "ll_calender_portrait")
//    void calenderClick() {
//        if (isFastClick(1000)) {
//            return;
//        }
//        showDatePicker();
//    }

//    @Override
//    public void onVideoReceived(byte[] videoBuffer) {
//        hidePlayFail();
//        if (p2pService != null) {
//            if (p2pService.isPlaying()) {
//                if (playType == PLAY_TYPE_LIVE) {
//                    hideVideoLoading();
//                } else if (playType == PLAY_TYPE_PLAYBACK_DEV
//                        && (tvTimeScroll != null && tvTimeScroll.isShown())) {
////                    hideTimeScroll();
//                }
//            }
//            p2pService.setVideoData(videoBuffer);
//        }
//    }
//
//    @Override
//    public void onAudioReceived(byte[] audioBuffer) {
//        if (p2pService != null) {
//            p2pService.setAudioData(audioBuffer);
//        }
//    }

//    @Override
//    public void getDeviceTimeSlotSuccess(List<VideoTimeSlotBean> slots) {
//        if (slots != null && slots.size() > 0) {
//            listAp.addAll(slots);
//            getDeviceTimeSlots(slots.get(slots.size() - 1).getEndTime(), currentDateSeconds);
//        } else {
//            if (listAp == null || listAp.size() == 0) {
//                hideVideoLoading();
//                switch2Live();//无ap且无cloud的时间列表
//            } else {
//                timeCanvasList(listAp); //ap时间列表>0且cloud列表=0
//            }
//        }
//    }

//    private void showDatePicker() {
//        DatePickDialog datePickDialog = new DatePickDialog(context);
//        datePickDialog.setType(DateType.TYPE_YMD);
//        //设置点击确定按钮回调
//        datePickDialog.setOnSureListener(this::onSureButton);
//
//        datePickDialog.setStartDate(scrollTime > 0 ? new Date(scrollTime)
//                : new Date(System.currentTimeMillis()));
//        datePickDialog.show();
//    }
//
//    /**
//     * 切回直播
//     */
//    private void switch2Live() {
//        isFirstScroll = true;
//        showVideoLoading();
//        //当前时间秒数 TODO 需优化播放中渲染的时间
//        currentDateSeconds = System.currentTimeMillis() / 1000;
//        selectedDate = currentDateSeconds;
//        if (listAp.size() > 0) {
//            refreshTimeSlotVideoList();
//        }
//        mPresenter.startLive(getIOTCClient());
//    }

//    /**
//     * 一分钟轮询一次
//     * 开始移动
//     */
//    public void openMove() {
//        closeMove();
//        if (executorService == null) {
//            executorService = Executors.newSingleThreadScheduledExecutor();
//        }
////        scrollTime = scalePanel.getCurrentInterval() * 1000;//获取实时滚动的时间
////        setCalendarText(scalePanel.currentTimeStr().substring(6, 8));
//        executorService.scheduleAtFixedRate(this::moveTo, 60, 60, TimeUnit.SECONDS);
//    }

//    @UiThread
//    void moveTo() {
////        scalePanel.autoMove();
//        //自动滑动时下一个视频ap还是cloud播放
//        if (playType != PLAY_TYPE_LIVE && isVideoLess1Minute) {
//            isVideoLess1Minute = false;
////            switch2Playback(scalePanel.getCurrentInterval());
//        }
//    }
//
//    //结束移动
//    public void closeMove() {
//        if (executorService != null) {
//            executorService.shutdownNow();
//            executorService = null;
//        }
//    }

    //渲染时间轴并滚动到指定时间
//    @UiThread
//    void timeCanvasList(final List<VideoTimeSlotBean> apCloudList) {
//        scalePanel.setVideoData(listAp);
//        scalePanel.refresh();
//        if (isFirstScroll && !isSelectedDate) {
//            isFirstScroll = false;
//            selectedTimeIsHaveVideo(selectedDate); //初始化左滑渲染及回放
//        } else {
//            if (isSelectedDate) {
//                selectedTimeIsHaveVideo(selectedDate);//滑动到选择日期
//            } else {
//                scalePanel.refreshNow(); //滚动到当前时间
//            }
//        }
//        //渲染完成
//        hideVideoLoading();
//    }

//    //选择日历日期回调
//    @SuppressLint("DefaultLocale")
//    public void onSureButton(Date date) {
//        long currentTime = System.currentTimeMillis() / 1000;//当前时间戳秒
//        scrollTime = date.getTime();//选择日期的时间戳毫秒
//        long time = scrollTime / 1000; //设置日期的秒数
//        if (time > currentTime) {//未来时间或当前--滑动当前直播
//            isSelectedDate = false;
//            scrollTime = System.currentTimeMillis();
//            setCalendarText(String.format("%td", new Date()));
//            if (playType == PLAY_TYPE_LIVE) {
//                return;
//            }
//            switch2Live();
//        } else {//回放时间
//            isFirstScroll = false;//非首次滑动
//            isSelectedDate = true;
//            setIvPlayImage(true);
//            ivLive.setVisibility(View.VISIBLE);
//
//            String strDate = DateTimeUtils.secondToDate(time, "yyyy-MM-dd");
//            int year = Integer.valueOf(strDate.substring(0, 4));
//            int month = Integer.valueOf(strDate.substring(5, 7));
//            int day = Integer.valueOf(strDate.substring(8, 10));
//            //显示日历天数
//            setCalendarText(String.format("%td", date));
//            //设置选择日期的年月日0时0分0秒
//            calendar.clear();
//            calendar.set(year, month - 1, day, 0, 0, 0);//设置时候月份减1即是当月
//            selectedDate = calendar.getTimeInMillis() / 1000;//设置日期的秒数
//            //当前时间秒数
//            currentDateSeconds = System.currentTimeMillis() / 1000;
//            //选择日期三天前的秒数
//            threeDaysBeforeSeconds = selectedDate - threeDaysSeconds;
//            //加载时间轴
//            refreshTimeSlotVideoList();
//        }
//    }

//    //获取视频跳转播放的currentItemPosition
//    private void videoSkipScrollPosition(long currentTimeMinutes) {
//        scalePanel.moveToTime(currentTimeMinutes);
//    }

//    //滑动回放定位的中间 position
//    private void scrollCurrentPlayBackTime(long currentTimeMinutes) {
//        setIvPlayImage(true);
//        isPaused = false;
//        scalePanel.moveToTime(currentTimeMinutes);
//        openMove();
//    }

//    /**
//     * 滑动到当前时间
//     * <p>
//     * 1 回放视频为空
//     * 2 点击直播按钮
//     */
//    private void scrollCurrentLive() {
//        playType = PLAY_TYPE_LIVE;
//        scalePanel.refreshNow();
//        openMove();
//    }

//    //拖动或选择的时间是否有video（ap或cloud）
//    private void selectedTimeIsHaveVideo(long currTime) {
//        int apSize = listAp.size();
//        if (apSize == 0) {
//            switch2Live();//跳转直播
//            return;
//        }
//        long mStartTime = threeDaysBeforeSeconds, mEndTime = currentDateSeconds;
//        for (int i = 0; i < apSize + 1; i++) {
//            long startOpposite = 0, endOpposite = 0, start = 0, end = 0;
//            //不包含ap时间轴内的时间
//            if (i == 0) {
//                startOpposite = mStartTime;
//                endOpposite = listAp.get(i).getStartTime();
//            } else if (i < apSize) {
//                startOpposite = listAp.get(i - 1).getEndTime();
//                endOpposite = listAp.get(i).getStartTime();
//            } else if (i == apSize) {
//                startOpposite = listAp.get(i - 1).getEndTime();
//                endOpposite = mEndTime;
//            }
//            //包含ap时间内
//            if (i < apSize) {
//                start = listAp.get(i).getStartTime();
//                end = listAp.get(i).getEndTime();
//            }
//            if (currTime >= startOpposite && currTime < endOpposite) {//空白区域
//                if (i == apSize) {//最后一个无视频区域跳转直播
//                    switch2Live();
//                    return;
//                }
//                //当前的视频片段是否小于一分钟
//                isVideoLess1Minute = listAp.get(i).getEndTime() - listAp.get(i).getStartTime() <= 60;
//                switch2DevPlayback(endOpposite);
//                scrollCurrentPlayBackTime(endOpposite);//回放到拖动的时间点
//                break;
//            } else if (currTime >= start && currTime < end) {//视频区域
//                //当前的视频片段是否小于一分钟
//                isVideoLess1Minute = listAp.get(i).getEndTime() - currTime <= 60;
//                switch2DevPlayback(currTime);
//                scrollCurrentPlayBackTime(currTime);//回放到拖动的时间点
//                break;
//            }
//        }
//    }

//    private void switch2Playback(long currTime) {
//        int availableVideoSize = listAp.size();
//        for (int i = 0; i < availableVideoSize; i++) {
//            VideoTimeSlotBean bean = listAp.get(i);
//            long start = bean.getStartTime();
//            long end = bean.getEndTime();
//            //当滑动到最后前后一分钟时，判断下一个视频片段ap还是cloud
//            if (end - currTime < 60 && currTime >= start && currTime < end) {
//                if (i == availableVideoSize - 1) {//todo 最后一个，需要渲染后面的数据
////                    refreshTimeSlotVideoList();//i是最后一个，基于i的end作为start再拉7天的数据。
//                } else {
//                    final int delayMillis = (int) end - currTime < 0 ? 1 : (int) (end - currTime);
//                    final int finalI = i;
//                    handler.postDelayed(() -> {
//                        switch2DevPlayback(listAp.get(finalI + 1).getStartTime());
//                        videoSkipScrollPosition(listAp.get(finalI + 1).getStartTime());//偏移跳转
//                    }, delayMillis * 1000);
//                    break;
//                }
//            }
//        }
//    }

//    //发送请求获取组合时间轴
//    private void refreshTimeSlotVideoList() {
//        showVideoLoading();
//        listAp.clear();
//        getDeviceTimeSlots(threeDaysBeforeSeconds, currentDateSeconds);
//    }

//    //获取设备sd卡回放时间轴
//    public void getDeviceTimeSlots(long startTime, long endTime) {
//        mPresenter.getPlaybackList(getIOTCClient(), startTime, endTime);
//    }

//    @Override
//    public void didMoveToTime(long timeStamp) {
//        hideTimeScroll();
//        if (timeStamp > System.currentTimeMillis() / 1000) {//超过当前时间
//            shortTip(getString(R.string.ipc_time_over_current_time));
//            if (playType == PLAY_TYPE_LIVE) {//当前处于直播
//                scrollCurrentLive();
//            } else {//当前处于回放
//                switch2Live();
//            }
//            return;
//        }
//        if (timeStamp < threeDaysBeforeSeconds) {
//            shortTip(R.string.ipc_time_over_back_time);
//            startDelayPlay(threeDaysBeforeSeconds);
//            return;
//        }
//        if (isFirstScroll && listAp.size() == 0) {
//            selectedDate = timeStamp;
//            scalePanel.clearData();//clear渲染时间轴
//            getDeviceTimeSlots(threeDaysBeforeSeconds, currentDateSeconds);
//            return;
//        }
//        startDelayPlay(timeStamp);
//    }
//
//    @Override
//    public void moveTo(String data, boolean isLeftScroll, long timeStamp) {
//        cancelDelayPlay();
//        showTimeScroll(data.substring(11), isLeftScroll);//toast显示时间
//    }

//    @UiThread
//    void showTimeScroll(final String time, final boolean isLeft) {
//        if (TextUtils.isEmpty(time)) {
//            return;
//        }
//        tvTimeScroll.setVisibility(View.VISIBLE);
//        tvTimeScroll.setText(time);
//        if (isLeft) {
//            if (drawableLeft == null) {
//                drawableLeft = ContextCompat.getDrawable(this, R.mipmap.ic_fast_backward);
//            }
//            tvTimeScroll.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
//        } else {
//            if (drawableRight == null) {
//                drawableRight = ContextCompat.getDrawable(this, R.mipmap.ic_fast_forward);
//            }
//            tvTimeScroll.setCompoundDrawablesWithIntrinsicBounds(drawableRight, null, null, null);
//        }
//    }

//    @UiThread
//    void hideTimeScroll() {
//        handler.postDelayed(() -> tvTimeScroll.setVisibility(View.GONE), 500);
//    }

//    /**
//     * 延时执行滑动处理，防止无视频区域直接跳过
//     */
//    private void startDelayPlay(long timeStamp) {
//        cancelDelayPlay();
//        timeLineScrollTimer = new CountDownTimer(500, 100) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                showVideoLoading();
//                selectedTimeIsHaveVideo(timeStamp);
//            }
//        };
//        timeLineScrollTimer.start();
//    }

//    private void cancelDelayPlay() {
//        if (timeLineScrollTimer != null) {
//            timeLineScrollTimer.cancel();
//            timeLineScrollTimer = null;
//        }
//    }

}
