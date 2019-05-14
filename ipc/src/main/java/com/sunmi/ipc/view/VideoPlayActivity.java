package com.sunmi.ipc.view;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.datelibrary.DatePickDialog;
import com.datelibrary.bean.DateType;
import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.AudioMngHelper;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.ThreadPool;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.VerticalSeekBar;

/**
 * Description:
 * Created by bruce on 2019/4/11.
 */
@EActivity(resName = "activity_video_play")
public class VideoPlayActivity extends BaseActivity
        implements SurfaceHolder.Callback, View.OnTouchListener, IOTCClient.Callback,
        SeekBar.OnSeekBarChangeListener {
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "ocv_ipc")
    OverCameraView overCameraView;
    @ViewById(resName = "sb_zoom")
    SeekBar sbZoom;
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
    @ViewById(resName = "tv_hd_quality")
    TextView tvHDQuality;//高清画质
    @ViewById(resName = "tv_sd_quality")
    TextView tvSDQuality;//标清画质
    @ViewById(resName = "cm_timer")
    Chronometer cmTimer;//录制时间
    @ViewById(resName = "rl_record")
    RelativeLayout rlRecord;
    @ViewById(resName = "iv_calender")
    ImageView ivCalender;
    @ViewById(resName = "rl_top_setting")
    RelativeLayout rlTopSetting;//top设置
    @ViewById(resName = "ll_bottom")
    RelativeLayout rlBottomSetting;//bottom设置
    @ViewById(resName = "iv_screenshot")
    ImageView ivScreenshot;//截图
    @ViewById(resName = "iv_live")
    ImageView ivLive;//直播

    @Extra
    String UID;

    private int screenW, screenH;
    private float currX = 540, currY = 860;

    int currZoom, currFocus;

    private H264Decoder mPlayer = null;
    private AACDecoder mAudioPlayer = null;


    private AudioMngHelper audioMngHelper = null;
    private boolean isStartRecord;//是否开始录制
    private boolean isShowVolume;//是否显示音量调控
    private boolean isShowQuality;//是否画质
    private boolean isClickScreen;//是否点击屏幕


    @AfterViews
    void init() {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        screenW = CommonHelper.getScreenWidth(context);
        screenH = CommonHelper.getScreenHeight(context);
        sbZoom.setOnSeekBarChangeListener(this);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.height = lp.width = CommonHelper.getScreenHeight(context);
        videoView.setLayoutParams(lp);
        videoView.setOnTouchListener(this);
        IOTCClient.setCallback(this);
        videoView.getHolder().addCallback(this);
        mAudioPlayer = new AACDecoder();
//        IPCCall.getInstance().fsGetStatus(context);//fs
        playClick();
        adjustVoice();

    }

    @Override
    protected boolean needLandscape() {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogCat.e(TAG, "666666 seekBar.getProgress() = " + seekBar.getProgress());
        IPCCall.getInstance().fsZoom(seekBar.getProgress(), context);
    }

    void playClick() {
        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                IOTCClient.start(UID);
            }
        });
    }

    @Click(resName = "tv_add")
    void addClick() {
        if (currFocus > 775) {
            shortTip("已到最大焦距");
            return;
        }
        IPCCall.getInstance().fsFocus(currFocus += 5, context);
    }

    @Click(resName = "tv_minus")
    void minusClick() {
        if (currFocus < 5) {
            shortTip("已到最小焦距");
            return;
        }
        IPCCall.getInstance().fsFocus(currFocus -= 5, context);
    }

    @Click(resName = "tv_auto")
    void autoFocusClick() {
        IPCCall.getInstance().fsAutoFocus(context);
    }

    @Click(resName = "btn_next")
    void nextClick() {
        LogCat.e(TAG, "addClick");
        if (screenH <= 0 || screenW <= 0) {
            shortTip("已到最小焦距");
            return;
        }
        //        IPCCall.getInstance().fsReset(context);
        IPCCall.getInstance().fsIrMode(0, context);
        shortTip("x = " + (int) currX * 100 / screenW);
        shortTip("y = " + (int) currY * 100 / screenH);
        IPCCall.getInstance().fsSetFocusPoint((int) currX * 100 / screenW, (int) currY * 100 / screenH, context);
    }

    @Click(resName = "iv_back")
    void backClick() {
        finish();
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

    //开始计时录制
    private void startRecord() {
        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = System.currentTimeMillis() - cArg.getBase();
                Date d = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                cmTimer.setText(sdf.format(d));
            }
        });
        cmTimer.setBase(System.currentTimeMillis());
        cmTimer.start();
    }


    //音量
    @Click(resName = "iv_volume")
    void volumeClick() {
        if (isShowVolume) {
            llChangeVolume.setVisibility(View.GONE);
            isShowVolume = false;
        } else {
            llChangeVolume.setVisibility(View.VISIBLE);
            //获取当前音量
            int currentVolume100 = audioMngHelper.get100CurrentVolume();
            sBarVoice.setProgress(currentVolume100);
            isShowVolume = true;
        }
    }

    //画质
    @Click(resName = "tv_quality")
    void qualityClick() {
        if (isShowQuality) {
            llVideoQuality.setVisibility(View.GONE);
            isShowQuality = false;
        } else {
            llVideoQuality.setVisibility(View.VISIBLE);
            isShowQuality = true;
        }
    }

    //高清画质
    @Click(resName = "tv_hd_quality")
    void hdQualityClick() {
        llVideoQuality.setVisibility(View.GONE);
        isShowQuality = false;
        tvQuality.setText(R.string.str_HD);
    }

    //标清画质
    @Click(resName = "tv_sd_quality")
    void sdQualityClick() {
        llVideoQuality.setVisibility(View.GONE);
        isShowQuality = false;
        tvQuality.setText(R.string.str_SD);
    }

    //显示日历
    @Click(resName = "iv_calender")
    void calenderClick() {
        //第三方
        DatePickDialog dialog = new DatePickDialog(this);
        //设置上下年分限制
        dialog.setYearLimt(100);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd HH:mm");
//        dialog.setMessageFormat("yyyy-MM-dd");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(null);
        dialog.show();
    }

    //点击屏幕
    @Click(resName = "rl_screen")
    void screenClick() {
        if (isClickScreen) {
            rlTopSetting.setVisibility(View.VISIBLE);
            ivRecord.setVisibility(View.VISIBLE);
            ivScreenshot.setVisibility(View.VISIBLE);
            ivLive.setVisibility(View.VISIBLE);
            rlBottomSetting.setVisibility(View.VISIBLE);

            isClickScreen = false;
        } else {
            rlTopSetting.setVisibility(View.GONE);
            ivRecord.setVisibility(View.GONE);
            ivScreenshot.setVisibility(View.GONE);
            ivLive.setVisibility(View.GONE);
            rlBottomSetting.setVisibility(View.GONE);

            isClickScreen = true;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer = new H264Decoder(holder.getSurface(), 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //关闭操作
        if (mPlayer != null) {
            mPlayer.stopRunning();
            mPlayer = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取点击屏幕的位置，作为焦点位置，用于计算对焦区域
            float x = event.getX();
            float y = event.getY();

            if (120 < x && x < 1800 && 160 < y && y < 800) {
                currX = x;
                currY = y;
                //对焦并绘制对焦矩形框
                overCameraView.setTouchFoucusRect(x, y);
            }
        }
        return false;
    }

    @Override
    public void onVideoReceived(byte[] videoBuffer) {
        if (mPlayer != null)
            mPlayer.setVideoData(videoBuffer);
    }

    @Override
    public void onAudioReceived(byte[] audioBuffer) {
        mAudioPlayer.setAudioData(audioBuffer);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.fsAutoFocus, IpcConstants.fsFocus, IpcConstants.fsGetStatus,
                IpcConstants.fsIrMode, IpcConstants.fsReset, IpcConstants.fsZoom};
    }

    @UiThread
    void setSeekBarProgress(int progress) {
        sbZoom.setProgress(progress);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (res == null) return;
        if (id == IpcConstants.fsGetStatus) {
            //{"data":[{"opcode":"0x3109","result":{"zoom":0,"max_zoom":500,"max_focus":780,"irmode":0,"auto_focus_start":0,"focus":389},"errcode":1}],"msg_id":"11111","errcode":1}
            try {
                JSONObject jsonObject = res.getResult();
                if (jsonObject.has("zoom")) {
                    currZoom = jsonObject.getInt("zoom");
                    setSeekBarProgress(currZoom);
                }
                if (jsonObject.has("focus")) {
                    currFocus = jsonObject.getInt("focus");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogCat.e(TAG, "666666,111res = " + res.toString());
        } else if (id == IpcConstants.fsAutoFocus) {
            LogCat.e(TAG, "666666,222res = " + res.toString());
        } else if (id == IpcConstants.fsReset) {
        }
    }

    //调节音量
    private void adjustVoice() {
        audioMngHelper = new AudioMngHelper(this);
        int systemCurrent = audioMngHelper.getSystemCurrentVolume();
        int systemMax = audioMngHelper.getSystemMaxVolume();
        int currentVolume100 = audioMngHelper.get100CurrentVolume();
        sBarVoice.setMax(100);
        sBarVoice.setProgress(currentVolume100);
        sBarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioMngHelper.setVoice100(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //按键控制音量，return true时不显示系统音量 return false时显示系统音量
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            llChangeVolume.setVisibility(View.GONE);
            isShowVolume = false;
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            llChangeVolume.setVisibility(View.GONE);
            isShowVolume = false;
            return false;
        } else return super.onKeyDown(keyCode, event);
    }
}
