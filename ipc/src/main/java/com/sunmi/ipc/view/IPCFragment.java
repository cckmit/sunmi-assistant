package com.sunmi.ipc.view;

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;

import com.sunmi.ipc.utils.AACDecoder;
import com.sunmi.ipc.utils.H264Decoder;
import com.sunmi.ipc.utils.IOTCClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseFragment;
import sunmi.common.utils.ThreadPool;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_ipc")
public class IPCFragment extends BaseFragment implements SurfaceHolder.Callback {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "vv_ipc")
    SurfaceView videoView;
    @ViewById(resName = "btn_play")
    Button btnPlay;
    @ViewById(resName = "btn_pause")
    Button btnPause;
    @ViewById(resName = "btn_stop")
    Button btnStop;

    private static String UID = "C3YABT1MPRV4BM6GUHXJ";//ss
//    private static String UID = "CVYA8T1WKFV49NPGYHRJ";//fs
//    private static String UID = "CRYUBT1WKFV4UM6GUH71";

    private H264Decoder mPlayer = null;
    private AACDecoder mAudioPlayer = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            shortTip("播放结束!");
        }
    };

    @AfterViews
    void init() {
        //保持屏幕常亮
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        IOTCClient.setCallback(new IOTCClient.Callback() {
            @Override
            public void onVideoReceived(byte[] videoBuffer) {
                if (mPlayer != null)
                    mPlayer.setVideoData(videoBuffer);
            }

            @Override
            public void onAudioReceived(byte[] audioBuffer) {
                mAudioPlayer.setAudioData(audioBuffer);
            }
        });
        videoView.getHolder().addCallback(this);
        mAudioPlayer = new AACDecoder();
    }

    @Click(resName = "btn_play")
    void playClick() {
//        ReadAACFileThread audioThread = new ReadAACFileThread();
//        audioThread.start();
        ThreadPool.getCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                IOTCClient.start(UID);
            }
        });
    }

    @Click(resName = "btn_stop")
    void stopClick() {
        IPCConfigActivity_.intent(mActivity).shopId("6878").start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

}
