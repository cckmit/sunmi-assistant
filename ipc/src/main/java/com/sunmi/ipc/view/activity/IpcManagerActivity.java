package com.sunmi.ipc.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.CashVideoOverviewActivity_;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.contract.IpcManagerContract;
import com.sunmi.ipc.model.IpcManageBean;
import com.sunmi.ipc.presenter.IpcManagerPresenter;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.service.P2pService;
import com.sunmi.ipc.utils.IOTCClient;
import com.sunmi.ipc.utils.IpcUtils;
import com.sunmi.ipc.view.activity.setting.IpcSettingActivity_;
import com.xiaojinzi.component.impl.BiCallback;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterErrorResult;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.impl.RouterResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.VolumeHelper;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.activity.DeviceOfflineDescActivity_;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:
 * Created by bruce on 2019/9/11.
 */
@EActivity(resName = "activity_ipc_manager")
public class IpcManagerActivity extends BaseMvpActivity<IpcManagerPresenter>
        implements IpcManagerContract.View, SurfaceHolder.Callback, View.OnClickListener,
        VolumeHelper.VolumeChangeListener, P2pService.OnPlayStatusChangedListener,
        SeekBar.OnSeekBarChangeListener {

    private final static int PLAY_FAIL_OFFLINE = 1;
    private final static int PLAY_FAIL_NET_ERROR = 2;

    private final static int TIMEOUT_ADJUST_LOADING = 5_000;

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
    @ViewById(resName = "tv_living")
    TextView tvLiving;
    @ViewById(resName = "iv_volume")
    ImageView ivVolume;//音量
    @ViewById(resName = "tv_quality")
    TextView tvQuality;//画质
    @ViewById(resName = "iv_adjust")
    ImageView ivAdjust;//画面参数调整
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
    @ViewById(resName = "rg_adjust")
    RadioGroup rgAdjust;
    @ViewById(resName = "rb_brightness")
    RadioButton rbBrightness;
    @ViewById(resName = "cl_adjust")
    ConstraintLayout clAdjust;
    @ViewById(resName = "group_adjust_common")
    Group groupAdjustCommon;
    @ViewById(resName = "tv_percent")
    TextView tvPercent;
    @ViewById(resName = "sb_adjust")
    SeekBar sbAdjustCommon;
    @ViewById(resName = "group_adjust_focus")
    Group groupFocus;

    @Extra
    SunmiDevice device;

    private int screenW; //手机屏幕的宽
    private int qualityType = 1;//0-超清，1-高清
    private boolean isShowAdjust;//是否显示视频参数调整入口
    private boolean isControlPanelShow = true;//是否点击屏幕
    private boolean isPlayFailShown;

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
    private ArrayList<CashServiceInfo> serviceBeans = new ArrayList<>();

    P2pService p2pService;
    boolean isBind;
    private int compensation, saturation, contrast;//视频参数

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
        mPresenter.getStorageList(device.getDeviceid(), cloudStorageItem);
        if (!CommonHelper.isGooglePlay() && isSS1()) {
            mPresenter.getCashVideoService(device.getId());
        }
        if (!isSS1() &&
                IpcUtils.isNewVersion(device.getFirmware(), IpcConstants.IPC_VERSION_VIDEO_ADJUST)) {
            initVideoAdjust();
            isShowAdjust = true;
            ivAdjust.setVisibility(View.VISIBLE);
        }
        initVolume();
    }

    private void initVideoAdjust() {
        sbAdjustCommon.setOnSeekBarChangeListener(this);
        IPCCall.getInstance().getVideoParams(context, device.getModel(), device.getDeviceid());
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
        hideAdjustPanel();
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
//            if (llLoading != null && llLoading.isShown()) {
//                llLoading.setVisibility(View.GONE);
//                return;
//            }
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
                IpcSettingActivity_.intent(context).mDevice(device).isFromLive(true).start();
            } else {
                new CommonDialog.Builder(this)
                        .setTitle(R.string.str_device_offline)
                        .setMessage(R.string.msg_device_offline)
                        .setCancelButton(R.string.str_return)
                        .setConfirmButton(R.string.str_setting_detail, (dialog, which) ->
                                DeviceOfflineDescActivity_.intent(context).start())
                        .create().show();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvPercent.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        adjustVideo(rgAdjust.indexOfChild(rgAdjust.findViewById(rgAdjust.getCheckedRadioButtonId())),
                device.getModel(), device.getDeviceid(), seekBar.getProgress());
    }

    @Click(resName = "rl_top")
    void backClick() {
        if (llVideoQuality.isShown()) {
            llVideoQuality.setVisibility(View.GONE);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Click(resName = "iv_adjust")
    void adjustClick() {
        showAdjustPanel();
    }

    @Click(resName = "iv_increase")
    void increaseClick() {
        showAdjustLoading();
        IPCCall.getInstance().fsAdjustFocusAdd(context, device.getModel(), device.getDeviceid());
    }

    @Click(resName = "iv_decrease")
    void decreaseClick() {
        showAdjustLoading();
        IPCCall.getInstance().fsAdjustFocusMinus(context, device.getModel(), device.getDeviceid());
    }

    @Click(resName = "tv_reset")
    void resetClick() {
        showAdjustLoading();
        IPCCall.getInstance().fsAdjustFocusReset(context, device.getModel(), device.getDeviceid());
    }

    @Click(resName = "rb_brightness")
    void brightnessClick() {
        showCommonAdjustGroup(compensation);
    }

    @Click(resName = "rb_contrast")
    void contrastClick() {
        showCommonAdjustGroup(contrast);
    }

    @Click(resName = "rb_saturation")
    void saturationClick() {
        showCommonAdjustGroup(saturation);
    }

    @Click(resName = "rb_focus")
    void focusClick() {
        showFocusGroup();
    }

    @Click(resName = "iv_adjust_finish")
    void adjustFinishClick() {
        hideAdjustPanel();
    }

    @Click(resName = "iv_full_screen_live")
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
                .start().withAnimation(R.anim.slide_in_right, 0);
    }

    //点击屏幕
    @Click(resName = "rl_video")
    void screenClick() {
        if (isPlayFailShown || clAdjust.isShown()) {
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
        p2pServiceInit();
        initP2pLive();
    }

    private void p2pServiceInit() {
        if (p2pService != null)
            p2pService.init(videoView.getHolder().getSurface(), this);

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
    public void startLiveFail() {
        onPlayFail();
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
    public void getCashVideoServiceSuccess(ArrayList<CashServiceInfo> devices, boolean alreadySubscribe, int status, int validTime) {
        hideLoadingDialog();
        serviceBeans = devices;
        cashVideoItem.setStatus(status);
        if (status == CommonConstants.SERVICE_EXPIRED) {
            cashVideoItem.setRightText(getString(R.string.str_renew_now));
            cashVideoItem.setSummary(getString(R.string.tip_renew_service));
        }
        if (!devices.isEmpty()) {
            if (status == CommonConstants.SERVICE_ALREADY_OPENED) {
                cashVideoItem.setRightText(getString(R.string.str_setting_detail));
                cashVideoItem.setSummary(getString(R.string.cash_video_item_content));
                if (validTime / (3600 * 24) < 3) {
                    cashVideoItem.setSummary(getString(R.string.tip_validity_period, DateTimeUtils.secondToPeriod(validTime)));
                }
            }
            if (devices.get(0).isHasCashLossPrevention()) {
                cashVideoItem.setLeftImageResId(R.mipmap.ipc_manage_cash_loss_prevent);
                cashVideoItem.setTitle(getString(R.string.str_cash_loss_prevent));
            }
        } else if (alreadySubscribe) {// 已经有其他摄像机开通了收银视频服务
            cashVideoSubscribed = true;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void videoParamsObtained(int compensation, int saturation, int contrast) {
        this.compensation = compensation;
        this.saturation = saturation;
        this.contrast = contrast;
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
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcNameChanged, OpcodeConstants.getVideoParams,
                OpcodeConstants.fsAdjustFocusAdd, OpcodeConstants.fsAdjustFocusMinus,
                OpcodeConstants.fsAdjustFocusReset,
                CommonNotifications.cloudStorageChange, CommonNotifications.cashVideoSubscribe,
                CommonNotifications.cashPreventSubscribe
        };
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
        } else if (id == OpcodeConstants.fsAdjustFocusReset
                || id == OpcodeConstants.fsAdjustFocusAdd
                || id == OpcodeConstants.fsAdjustFocusMinus) {
            hideLoadingDialog();
        } else if (id == CommonNotifications.cashPreventSubscribe) {
            mPresenter.getCashPreventService(device.getDeviceid());
        }

        if (args != null && args[0] instanceof ResponseBean) {
            mPresenter.handleResponse(id, (ResponseBean) args[0]);
        }
    }

    private void resumePlay() {
        if (p2pService == null) {
            return;
        }
        if (p2pService.isNeedReinitialize()) {
            p2pServiceInit();
        }
        if (isPlayFailShown) {
            hideVideoLoading();
            return;
        }
        setPanelVisible(View.VISIBLE);
        if (p2pService != null) {
            showVideoLoading();
            mPresenter.resumePlay(qualityType, p2pService.getIOTCClient());
        }
    }

    private void pausePlay() {
        if (p2pService != null) {
            p2pService.stopRunning();
        }
    }

    private void showAdjustLoading() {
        showDarkLoading(getStringById(R.string.ipc_recognition_loading));
        handler.postDelayed(this::hideLoadingDialog, TIMEOUT_ADJUST_LOADING);
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
        isPlayFailShown = true;
        if (PLAY_FAIL_OFFLINE == type) {
            btnRetry.setVisibility(View.GONE);
            tvPlayFail.setText(R.string.tip_ipc_offline);
        } else if (PLAY_FAIL_NET_ERROR == type) {
            btnRetry.setVisibility(View.VISIBLE);
            tvPlayFail.setText(R.string.tip_network_fail_retry);
        }
        llPlayFail.setVisibility(View.VISIBLE);
        setTvLivingVisibility(View.GONE);
    }

    @UiThread
    public void hidePlayFail() {
        isPlayFailShown = false;
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
        if (isShowAdjust) {
            ivAdjust.setVisibility(visibility);
        }
        titleBar.setVisibility(visibility);
        ivFullScreen.setVisibility(visibility);
        llPortraitBar.setVisibility(visibility);
        rvManager.setVisibility(visibility);
    }

    private void showCommonAdjustGroup(int percent) {
        groupFocus.setVisibility(View.GONE);
        if (!groupAdjustCommon.isShown()) {
            groupAdjustCommon.setVisibility(View.VISIBLE);
        }
        initSeekBar(percent);
    }

    private void initSeekBar(int percent) {
        tvPercent.setText(String.valueOf(percent));
        sbAdjustCommon.setProgress(percent);
    }

    private void showFocusGroup() {
        groupAdjustCommon.setVisibility(View.GONE);
        groupFocus.setVisibility(View.VISIBLE);
    }

    private void hideAdjustPanel() {
        llPortraitBar.setVisibility(View.VISIBLE);
        rlBottomBar.setVisibility(View.VISIBLE);
        clAdjust.setVisibility(View.GONE);
        rgAdjust.setVisibility(View.GONE);
        groupFocus.setVisibility(View.GONE);
    }

    private void showAdjustPanel() {
        rgAdjust.setVisibility(View.VISIBLE);
        llPortraitBar.setVisibility(View.GONE);
        rlBottomBar.setVisibility(View.GONE);
        clAdjust.setVisibility(View.VISIBLE);
//        brightnessClick();
        groupAdjustCommon.setVisibility(View.VISIBLE);
        rbBrightness.setChecked(true);
        initSeekBar(compensation);
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

    @UiThread
    void setTvLivingVisibility(int visibility) {
        tvLiving.setVisibility(visibility);
    }

    private void setPanelVisible(int visible) {
        if (isPlayFailShown) {
            return;
        }
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
        ivVolume.setImageResource(currentVolume100 == 0 ? R.mipmap.ic_mute_enable : R.mipmap.ic_unmute_enable);
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

    public void adjustVideo(int type, String model, String sn, int value) {
        if (type == 0) {
            compensation = value;
            IPCCall.getInstance().fsAdjustBrightness(context, model, sn, value);
        } else if (type == 1) {
            contrast = value;
            IPCCall.getInstance().fsAdjustContrast(context, model, sn, value);
        } else if (type == 2) {
            saturation = value;
            IPCCall.getInstance().fsAdjustSaturation(context, model, sn, value);
        }
    }

    private void initManageList() {
        rvManager.init(0);
        if (!CommonHelper.isGooglePlay() && isSS1()) {
            cashVideoItem = new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_CASH, R.mipmap.ipc_manage_cashier, getString(R.string.cash_video),
                    getString(R.string.cash_video_item_content), getString(R.string.str_learn_more), true);
            list.add(cashVideoItem);
        }
        list.add(new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_DETECT, R.mipmap.ipc_manage_md, getString(R.string.str_motion_detection),
                getString(R.string.str_md_exception), getString(R.string.str_setting_detail), true));
        cloudStorageItem = new IpcManageBean(IpcConstants.IPC_MANAGE_TYPE_CLOUD, R.mipmap.ipc_cloud_storage, context.getString(R.string.str_cloud_storage),
                context.getString(R.string.str_setting_detail));
        cloudStorageItem.setEnabled(false);
        list.add(cloudStorageItem);
        adapter = new CommonListAdapter<IpcManageBean>(context, R.layout.item_ipc_manager, list) {
            @Override
            public void convert(ViewHolder holder, IpcManageBean bean) {
                Button btnDetail = holder.getView(R.id.btn_detail);
                ImageView ivTag = holder.getView(R.id.iv_tag);
                TextView tvSummary = holder.getView(R.id.tv_summary);
                holder.setImageResource(R.id.iv_icon, bean.getLeftImageResId());
                holder.setText(R.id.tv_title, bean.getTitle());
                tvSummary.setSelected(bean.getStatus() == CommonConstants.SERVICE_EXPIRED);
                tvSummary.setText(bean.getSummary());
                holder.setText(R.id.btn_detail, bean.getRightText());
                btnDetail.setEnabled(bean.isEnabled());
                if (bean.getTagImageResId() != -1) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(bean.getTagImageResId());
                } else {
                    ivTag.setVisibility(View.GONE);
                }
                btnDetail.setOnClickListener(v -> {
                    switch (bean.getType()) {
                        case IpcConstants.IPC_MANAGE_TYPE_CLOUD:
                            if (TextUtils.equals(bean.getRightText(), getString(R.string.str_setting_detail))) {
                                Router.withApi(SunmiServiceApi.class).goToServiceDetail(context,
                                        device.getDeviceid(), true, device.getName());
                            } else {
                                Router.withApi(SunmiServiceApi.class)
                                        .goToWebViewCloud(context, CommonConstants.H5_CLOUD_STORAGE,
                                                WebViewParamsUtils.getCloudStorageParams(device.getDeviceid(), ""));
                            }
                            break;
                        case IpcConstants.IPC_MANAGE_TYPE_CASH:
                            if (cashVideoSubscribed) {  //已有其他门店开启收银视频
                                shortTip(R.string.cash_video_other_device_already_subscribe_tip);
                            } else {
                                switch (bean.getStatus()) {
                                    case CommonConstants.SERVICE_NOT_OPENED:   //去开启收银视频
                                        Router.withApi(SunmiServiceApi.class)
                                                .goToWebViewCloud(context, CommonConstants.H5_CASH_VIDEO,
                                                        WebViewParamsUtils.getCashVideoParams(null, 0));
                                        break;
                                    case CommonConstants.SERVICE_ALREADY_OPENED:  //服务已开通
                                        if (cloudStorageServiceStatus == CommonConstants.SERVICE_ALREADY_OPENED) {
                                            CashVideoOverviewActivity_.intent(context).isSingleDevice(true)
                                                    .serviceBeans(serviceBeans)
                                                    .hasCashLossPrevent(serviceBeans.get(0).isHasCashLossPrevention()).start();
                                        } else if (cloudStorageServiceStatus == CommonConstants.SERVICE_NOT_OPENED) {
                                            shortTip(R.string.tip_after_cloud_cash_video);
                                        } else {
                                            shortTip(R.string.tip_cloud_expired);
                                        }
                                        break;
                                    case CommonConstants.SERVICE_EXPIRED: //服务已过期
                                        if (!serviceBeans.isEmpty()) {
                                            Router.withApi(SunmiServiceApi.class).goToWebViewCloud(context,
                                                    CommonConstants.H5_CASH_PREVENT_RENEW,
                                                    WebViewParamsUtils.getCashPreventLossParams(device.getDeviceid(), 1)
                                                    , new BiCallback<Intent>() {
                                                        @Override
                                                        public void onSuccess(@NonNull RouterResult result, @NonNull Intent intent) {
                                                            mPresenter.onServiceSubscribeResult(intent, device.getDeviceid());
                                                        }

                                                        @Override
                                                        public void onCancel(@Nullable RouterRequest originalRequest) {

                                                        }

                                                        @Override
                                                        public void onError(@NonNull RouterErrorResult errorResult) {

                                                        }
                                                    });
                                        } else {
                                            Router.withApi(SunmiServiceApi.class).goToWebViewCloud(context,
                                                    CommonConstants.H5_CASH_VIDEO_RENEW,
                                                    WebViewParamsUtils.getCashVideoParams(device.getDeviceid(), 1));
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case IpcConstants.IPC_MANAGE_TYPE_DETECT:
                            MotionVideoListActivity_.intent(context).device(device).start();
                            break;
                        default:
                            break;
                    }
                });
            }
        };
        rvManager.setAdapter(adapter);
    }

}
